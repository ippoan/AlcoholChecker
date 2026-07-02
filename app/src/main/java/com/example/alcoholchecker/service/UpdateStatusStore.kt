package com.example.alcoholchecker.service

import android.content.Context
import org.json.JSONObject

/** OTA 更新の最終結果を device_settings prefs に保存する単一の書き込み口。
 *  WebView (デバイス設定タブ) が getLastUpdateResult ブリッジ経由で読み、
 *  「無音の失敗」(署名不一致で silent install が失敗しても通知すら出ない状態) を
 *  UI に可視化するために使う。 */
object UpdateStatusStore {
    const val KEY = "last_update_result"

    fun save(
        context: Context,
        ok: Boolean,
        status: Int,
        message: String,
        hint: String,
        versionName: String,
    ) {
        val json = JSONObject().apply {
            put("ok", ok)
            put("status", status)
            put("message", message)
            put("hint", hint)
            put("version", versionName)
            put("ts", System.currentTimeMillis())
        }.toString()
        context.getSharedPreferences(OtaUpdateService.PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY, json).apply()
    }
}
