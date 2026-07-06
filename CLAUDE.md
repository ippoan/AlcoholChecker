# AlcoholChecker Android App

WebView ベースのアルコールチェックアプリ。NFC/BLE/シリアル通信ブリッジ + Device Owner 対応。
Device Owner プロビジョニング・リリースフロー詳細・主要コンポーネント表・SharedPreferences は
`AlcoholChecker-map` skill、配信モデルの深掘りは `alcoholchecker-deploy` skill を参照。

## ビルド

- デバッグ: `./gradlew installDebug`
- リリース: CI/CD で自動ビルド (手動ビルドには release.keystore が必要)
- **署名不一致エラー**: リリース署名 APK がある端末には `adb uninstall com.example.alcoholchecker` してからデバッグビルドをインストール

## リリース (CI/CD 自動化済み、dev/prod 配信分離)

`release.yml` が PR (dev 端末へ OTA push) / master push (stable、OTA 発火なし) の両方で
run_number カウンタ共有 (= versionCode 単調増加) で動く。

- `versionName` は patch bump (x.y.Z) を PR に含める (明示指示があるまで major/minor は上げない)

## 関連リポジトリ

- バックエンド: `rust-alc-api` (Axum + PostgreSQL)
- フロントエンド: `alc-app/web` (Nuxt 4 on Cloudflare Workers)
- シグナリング: `cf-alc-signaling` (Cloudflare Durable Objects)
