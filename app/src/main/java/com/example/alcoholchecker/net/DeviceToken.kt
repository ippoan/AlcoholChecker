package com.example.alcoholchecker.net

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * auth-worker の device JWT を取得・cache する (Refs ippoan/rust-alc-api#434 caller #5 案B)。
 *
 * 端末登録 (claim) 時に alc-app から受け取った device credential
 * (`auth_device_id` + `device_secret`、SharedPreferences `device_settings`) を auth-worker
 * `/device/token` に提示して短命 (1h) device JWT を mint する。device 経路 (report-version 等)
 * はこの JWT を `Authorization: Bearer` で alc-app server proxy に送り、proxy が /alc-proxy →
 * rust に OIDC で forward する (Cloud Run IAM lockdown 対応)。
 *
 * - credential 無し / mint 失敗時は null を返す → 呼び出し側は Authorization を付けずに送る
 *   (lockdown 前は直叩き fallback が効くため非破壊)。
 * - JWT は expiry の 60s 手前まで in-memory cache を再利用する。
 *
 * 注意: ここで使う `auth_device_id` は **auth-worker の device credential id** であり、
 * rust-alc-api の devices テーブル id (`device_id`) とは別系統。混同しないこと。
 */
object DeviceToken {
    private const val AUTH_WORKER_URL = "https://auth.ippoan.org"
    private const val PREFS = "device_settings"
    private const val KEY_AUTH_DEVICE_ID = "auth_device_id"
    private const val KEY_DEVICE_SECRET = "device_secret"

    /** JWT 再利用の手前マージン (ms)。 */
    private const val REFRESH_BEFORE_MS = 60_000L
    /** expires_in 欠落時の fallback TTL (秒、auth-worker DEVICE_JWT_TTL_SECONDS と同値)。 */
    private const val DEFAULT_TTL_SECONDS = 3600L

    private var cachedJwt: String? = null
    private var cachedExpMs: Long = 0

    /**
     * 有効な device JWT を返す。cache が生きていればそれ、無ければ `/device/token` で mint。
     * credential 未保存 / mint 失敗時は null。**Dispatchers.IO 等のワーカースレッドから呼ぶこと**
     * (同期 HTTP)。
     */
    @Synchronized
    fun get(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val deviceId = prefs.getString(KEY_AUTH_DEVICE_ID, null)
        val deviceSecret = prefs.getString(KEY_DEVICE_SECRET, null)
        if (deviceId.isNullOrEmpty() || deviceSecret.isNullOrEmpty()) return null

        val now = System.currentTimeMillis()
        val jwt = cachedJwt
        if (jwt != null && cachedExpMs - REFRESH_BEFORE_MS > now) return jwt

        return try {
            val url = URL("$AUTH_WORKER_URL/device/token")
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 10_000
                conn.readTimeout = 10_000
                val body = JSONObject().apply {
                    put("device_id", deviceId)
                    put("device_secret", deviceSecret)
                }
                conn.outputStream.use { it.write(body.toString().toByteArray()) }
                if (conn.responseCode != 200) return null
                val json = JSONObject(conn.inputStream.bufferedReader().readText())
                val accessToken = json.optString("access_token", "")
                if (accessToken.isEmpty()) return null
                val ttl = if (json.has("expires_in")) json.optLong("expires_in", DEFAULT_TTL_SECONDS)
                else DEFAULT_TTL_SECONDS
                cachedJwt = accessToken
                cachedExpMs = now + ttl * 1000
                accessToken
            } finally {
                conn.disconnect()
            }
        } catch (_: Exception) {
            null
        }
    }

    /** device credential 破棄時に cache も捨てる (端末退役・revoke 後)。 */
    @Synchronized
    fun clearCache() {
        cachedJwt = null
        cachedExpMs = 0
    }
}
