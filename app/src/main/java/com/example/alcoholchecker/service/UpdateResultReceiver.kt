package com.example.alcoholchecker.service

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.alcoholchecker.R
import com.example.alcoholchecker.net.DeviceToken
import com.example.alcoholchecker.net.EnvironmentStore
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class UpdateResultReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "UpdateResult"
        const val ACTION_INSTALL_RESULT = "com.example.alcoholchecker.INSTALL_RESULT"
        // OtaUpdateService が作成する既存チャンネルを流用 (失敗通知用)
        private const val CHANNEL_ID = "ota_update"
        private const val FAILURE_NOTIFICATION_ID = 9002
    }

    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
        val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE) ?: ""
        val versionName = intent.getStringExtra("version_name") ?: ""

        when (status) {
            PackageInstaller.STATUS_SUCCESS -> {
                Log.d(TAG, "Package install succeeded")
                UpdateStatusStore.save(
                    context, ok = true, status = status, message = "",
                    hint = "更新に成功しました", versionName = versionName
                )
                applyDeviceOwnerSettings(context)
                val versionCode = intent.getIntExtra("version_code", 0)
                reportVersionToBackend(context, versionCode, versionName)
            }
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                // Device Owner でない場合、ユーザーの確認が必要
                val confirmIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                if (confirmIntent != null) {
                    confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(confirmIntent)
                }
            }
            else -> {
                // 従来は Log.e だけで通知も UI フィードバックも無く「無音の失敗」になっていた。
                // 署名不一致 (STATUS_FAILURE_CONFLICT / message に signature) は最頻の失敗。
                Log.e(TAG, "Package install failed: status=$status, message=$message")
                val hint = when {
                    status == PackageInstaller.STATUS_FAILURE_CONFLICT ||
                        message.contains("signature", ignoreCase = true) ->
                        "署名が異なるため上書き更新できません。" +
                            "一度アプリをアンインストールしてから再インストールしてください。"
                    else -> "インストールに失敗しました (status=$status)"
                }
                UpdateStatusStore.save(
                    context, ok = false, status = status, message = message,
                    hint = hint, versionName = versionName
                )
                showFailureNotification(context, hint)
            }
        }
    }

    private fun showFailureNotification(context: Context, text: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("アプリ更新エラー")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java)
            ?.notify(FAILURE_NOTIFICATION_ID, notification)
    }

    private fun reportVersionToBackend(context: Context, versionCode: Int, versionName: String) {
        val prefs = context.getSharedPreferences(OtaUpdateService.PREFS_NAME, Context.MODE_PRIVATE)
        val deviceId = prefs.getString("device_id", null) ?: return

        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val isDeviceOwner = dpm.isDeviceOwnerApp(context.packageName)
        val isDevDevice = prefs.getBoolean("is_dev_device", false)

        // 新しいバージョン情報を使用 (インストール直後はまだ旧アプリが動作中の可能性)
        val actualVersionCode = if (versionCode > 0) versionCode else getCurrentVersionCode(context)
        val actualVersionName = versionName.ifEmpty { getCurrentVersionName(context) }

        thread {
            try {
                val url = URL("${EnvironmentStore.apiBase(context)}/api/devices/report-version")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Content-Type", "application/json")
                // lockdown 対応: device JWT を Bearer で送る (Refs ippoan/rust-alc-api#480)。
                // 無いと alc-app 側 createDeviceProxyHandler が rust 直叩き fallback に落ち、
                // Cloud Run IAM lockdown 後は 403 になる。
                DeviceToken.get(context)
                    ?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
                conn.doOutput = true
                conn.connectTimeout = 10_000
                conn.readTimeout = 10_000

                val body = """{"device_id":"$deviceId","version_code":$actualVersionCode,"version_name":"$actualVersionName","is_device_owner":$isDeviceOwner,"is_dev_device":$isDevDevice}"""
                conn.outputStream.use { it.write(body.toByteArray()) }

                val responseCode = conn.responseCode
                Log.d(TAG, "Version reported after update: $responseCode (v$actualVersionName/$actualVersionCode)")
                conn.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to report version after update", e)
            }
        }
    }

    private fun applyDeviceOwnerSettings(context: Context) {
        try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            if (!dpm.isDeviceOwnerApp(context.packageName)) return
            val componentName = android.content.ComponentName(
                context, com.example.alcoholchecker.admin.AppDeviceAdminReceiver::class.java
            )
            dpm.setGlobalSetting(componentName, "double_tap_to_wake", "1")
            Log.d(TAG, "Double tap to wake enabled after update")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply device owner settings: ${e.message}")
        }
    }

    private fun getCurrentVersionCode(context: Context): Int {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            info.versionCode
        }
    }

    private fun getCurrentVersionName(context: Context): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
    }
}
