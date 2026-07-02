package com.example.alcoholchecker.net

import android.content.Context

/**
 * 端末が接続する環境 (prod / staging)。
 *
 * このアプリは単一署名・単一 package の APK を prod/staging 両方の端末で共用する
 * (Play Store 非経由のサイドロード配布 + Device Owner プロビジョニングの都合上、
 * build flavor でパッケージを分けると assetlinks.json / FCM / QR checksum の運用が
 * 分岐して複雑になるため採用しない)。
 *
 * 環境は device-claim App Link の host (alc.ippoan.org / alc-staging.ippoan.org) から
 * 一意に決まるので、claim 成功時にこの enum を [EnvironmentStore] へ永続化し、以降の
 * 全 API 呼び出し (WebView BASE_URL, FCM token 登録, watchdog, OTA report, device JWT
 * mint 等) はここから解決したベース URL を使う。QR/URL からの自由入力は受け付けず、
 * 常にこの 2 値のいずれかにしか倒れない (任意サーバーへの誘導を防ぐ)。
 */
enum class AppEnvironment(
    val apiBase: String,
    val authWorkerUrl: String,
) {
    PROD(
        apiBase = "https://alc.ippoan.org",
        authWorkerUrl = "https://auth.ippoan.org",
    ),
    STAGING(
        apiBase = "https://alc-staging.ippoan.org",
        authWorkerUrl = "https://auth-staging.ippoan.org",
    );

    companion object {
        /** device-claim App Link の host から環境を解決する。未知の host は null。 */
        fun fromHost(host: String?): AppEnvironment? = when (host) {
            "alc.ippoan.org" -> PROD
            "alc-staging.ippoan.org" -> STAGING
            else -> null
        }
    }
}

/** [AppEnvironment] を SharedPreferences (`device_settings`) に永続化する単一 SoT。 */
object EnvironmentStore {
    private const val PREFS = "device_settings"
    private const val KEY_ENV = "app_env"

    /** 保存済み環境を返す。未保存/不正値は [AppEnvironment.PROD] にフォールバックする。 */
    fun get(context: Context): AppEnvironment {
        val name = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ENV, null)
        return AppEnvironment.entries.find { it.name == name } ?: AppEnvironment.PROD
    }

    fun set(context: Context, env: AppEnvironment) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ENV, env.name)
            .apply()
    }

    /** 現在の環境の API ベース URL。 */
    fun apiBase(context: Context): String = get(context).apiBase

    /** 現在の環境の auth-worker URL (device JWT mint 先)。 */
    fun authWorkerUrl(context: Context): String = get(context).authWorkerUrl
}
