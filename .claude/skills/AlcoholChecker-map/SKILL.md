---
name: AlcoholChecker-map
generated-from: AlcoholChecker:753e5731d0b96e8bee48943958fe3d4ec44b4f62
paths: [app/]
description: ippoan/AlcoholChecker (Android キオスクアプリ) の構造ナビゲーション skill。リリースフロー (release.yml の PR/master 二相動作)・Device Owner プロビジョニング (GetProvisioningModeActivity/PolicyComplianceActivity 必須・QR JSON 形式)・主要コンポーネント表 (WebViewActivity/AppDeviceAdminReceiver/WatchdogService 等)・SharedPreferences キー一覧をまとめる。トリガー:「AlcoholChecker 構造」「Device Owner プロビジョニング」「GetProvisioningModeActivity」「PolicyComplianceActivity」「プロビジョニング QR」「versionCode run_number」「主要コンポーネント」「SharedPreferences device_settings」「release.yml PR master」等。
---

# AlcoholChecker-map — ippoan/AlcoholChecker 構造ナビゲーション

CLAUDE.md 骨格化 (ippoan/claude-md#90) で詳細セクションを verbatim 移設した map skill。
配信モデルそのものの深掘りは `alcoholchecker-deploy` skill を参照 (本 skill は構造索引)。

## リリース (CI/CD 自動化済み、dev/prod 配信分離)

`release.yml` が PR / master push の両方で動く (run_number カウンタ共有 = versionCode 単調増加):

### PR (staging = dev 端末配信)

1. PR を master に向ける (`app/**` 変更時)
2. CI が release 署名 APK をビルドし:
   - prerelease tag `dev-pr<N>-<run>` (releases/latest には出ない)
   - gh-pages `dev/pr<N>/` に APK + QR 配置、PR に QR コメント
   - **dev 端末 (`is_dev_device=true`) へ `download_url` 付き OTA push** (trigger-update-dev)
3. build green 後、`auto-merge` job (ci-workflows auto-merge.yml@main、CI_APP_ID org secret) が squash auto-merge を queue

### master merge (stable)

- `v{versionName}+{run}` タグ + GitHub Release (releases/latest 更新) + gh-pages root 更新
- **OTA は発火しない** — その他 (prod) 端末への配信は Release Wave / 管理画面の trigger-update に分離

### 注意

- `versionName` は patch bump (x.y.Z) を PR に含める (明示指示があるまで major/minor は上げない)
- `versionCode` はローカルデバッグ用の初期値。CI では `run_number` で上書き
- dev 端末の OTA `download_url` は backend (rust-alc-api `TriggerUpdateBody.download_url`) が FCM data に forward する

## Device Owner プロビジョニング

工場出荷リセット → QR スキャン → アプリ自動インストール + バックエンド自動登録

### 必須コンポーネント (Android 10+)
Android 10+ の QR プロビジョニングでは、DeviceAdminReceiver に加えて **2つの Activity** が必須:
1. **`GetProvisioningModeActivity`** — `android.app.action.GET_PROVISIONING_MODE` を処理し、`PROVISIONING_MODE_FULLY_MANAGED_DEVICE` を返す
2. **`PolicyComplianceActivity`** — `android.app.action.ADMIN_POLICY_COMPLIANCE` を処理し、`RESULT_OK` を返す

これらがないと「セットアップできません」エラーになる。`adb dpm set-device-owner` はこのフローをバイパスするため影響なし。

### フロー
1. 管理者ダッシュボード「デバイス管理」→「Device Owner プロビジョニング」でコード生成 + APK URL 入力 → QR 表示
2. 端末を工場出荷リセット → 初期設定画面で QR スキャン
3. Android が APK をダウンロード → `GetProvisioningModeActivity` を呼び出し → デバイスオーナーモードで続行
4. `AppDeviceAdminReceiver.onProfileProvisioningComplete` がプロビジョニング extras から `registration_code` を保存
5. `PolicyComplianceActivity` が呼ばれ、`RESULT_OK` を返す
6. アプリ起動時に `WebViewActivity.autoRegisterDeviceOwner()` が `POST /api/devices/register/claim` を自動呼び出し → 即承認
7. フロントエンド `useAuth.ts` が自動アクティベーション

### プロビジョニング QR 形式
```json
{
  "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME":
    "com.example.alcoholchecker/com.example.alcoholchecker.admin.AppDeviceAdminReceiver",
  "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION": "<APK URL>",
  "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM": "<SHA-256 Base64URL>",
  "android.app.extra.PROVISIONING_SKIP_ENCRYPTION": true,
  "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE": {
    "registration_code": "<generated_code>",
    "device_name": "営業車X",
    "is_dev_device": "false"
  }
}
```

## 主要コンポーネント

| ファイル | 役割 |
|---------|------|
| `WebViewActivity.kt` | メイン Activity。WebView + NFC/BLE/Serial/ScreenCapture ブリッジ |
| `AppDeviceAdminReceiver.kt` | Device Owner/Admin。プロビジョニング extras 読み取り |
| `GetProvisioningModeActivity.kt` | QR プロビジョニング時にデバイスオーナーモードを返す (Android 10+ 必須) |
| `PolicyComplianceActivity.kt` | QR プロビジョニング完了時にポリシー適用完了を返す (Android 10+ 必須) |
| `WatchdogService.kt` | ヘルスチェック + 自動再起動 |
| `RoomWatcher.kt` | WebSocket で遠隔点呼の着信監視 |
| `IncomingCallActivity.kt` | 着信 UI (フルスクリーン、ロック画面対応) |
| `MyFirebaseMessagingService.kt` | FCM 着信通知 + OTA アップデート |
| `device_admin.xml` | Device Admin ポリシー (force-lock) |

## SharedPreferences

- `device_settings`: `device_id`, `fcm_token`, `is_dev_device`, `registration_code`, `device_name`, `settings_token` (settings API の `X-Device-Token` ヘッダ用、ippoan/rust-alc-api#388)
- `call_settings`: `schedule` (JSON)

## 関連

- `alcoholchecker-deploy` — 配信モデル (dev/prod 分離・download_url allowlist・rollback 不可等) の SoT
- `ippoan-android-baseline` — org Android 標準
- バックエンド: `rust-alc-api` (Axum + PostgreSQL) / フロントエンド: `alc-app/web` (Nuxt 4 on Cloudflare Workers) / シグナリング: `cf-alc-signaling` (Cloudflare Durable Objects)
