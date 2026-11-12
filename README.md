# NexusPulse — Native Android Social Contact & Sync Hub

NexusPulse is a native Android application built with **Kotlin**, **Jetpack Compose (Material 3)**, **Room**, **Coroutines/Flow**, and **Retrofit/OkHttp**. It unifies contacts across social platforms with real-time background sync telemetry, QR code contact sharing, and adaptive dark/light themes.

---

## Features

- **Multi-Platform Contact Hub**: Link and sync contacts across **GitHub**, **LinkedIn**, **Instagram**, **Facebook**, **WhatsApp**, and **Facebook Messenger**.
- **Share Contact via QR Code**:
  - Generate a unique, zero-bitmap pure-Canvas 25×25 QR code (`pulse://contact?v=1&d=...`) for any contact.
  - Scan or paste a NexusPulse QR code to preview and instantly import contacts with all linked social profiles.
- **Persistent Animated Search & Filtering**:
  - Filter contacts in real time by name, role/company, social platform, or recent activity status (`ONLINE`, `RECENT`, `IDLE`, `OFFLINE`).
  - Sort contacts by **Last Updated** or **Alphabetical**.
- **Important Contacts (Favorites)**:
  - Star toggle on every contact with subtle haptic feedback, a dedicated horizontal **Important Contacts** bar, and a `★ Favorites` filter chip.
- **Live Sync Summary & Background Sync Engine**:
  - Displays the timestamp of the last successful global sync and active background connections.
  - Real GitHub REST API integration with automatic fallback telemetry for other platforms.
- **Adaptive Light & Dark Theme**:
  - One-tap theme switcher in the top bar with custom Cyber-Editorial obsidian dark mode and crisp daylight palettes.

---

## Building on GitHub (GitHub Actions)

This repository includes an automated GitHub Actions pipeline in [`.github/workflows/android.yml`](.github/workflows/android.yml).

### What Runs Automatically
1. **On every Push / Pull Request (`main`, `master`, `develop`)**:
   - Sets up **JDK 21 (Temurin)**, **Android SDK 36**, and **Gradle 9.3.1** (via `gradle/actions/setup-gradle@v4`).
   - Restores `debug.keystore` from `debug.keystore.base64`.
   - Executes unit and Robolectric tests (`gradle :app:testDebugUnitTest`).
   - Builds the Debug APK (`gradle :app:assembleDebug`) and uploads it as the **`NexusPulse-debug-apk`** artifact.
2. **On Manual Trigger (`workflow_dispatch`) or Version Tags (`v*`)**:
   - Builds signed Release APK (`:app:assembleRelease`) and Android App Bundle (`:app:bundleRelease`).
   - Uploads **`NexusPulse-release-apk`** and **`NexusPulse-release-aab`** artifacts.

### Optional GitHub Secrets for Release Signing
To sign release builds with your own upload keystore on GitHub, add the following **Repository Secrets** under **Settings → Secrets and variables → Actions**:

| Secret Name | Description |
| :--- | :--- |
| `RELEASE_KEYSTORE_BASE64` | Base64-encoded `.jks` keystore file (`base64 -w 0 my-upload-key.jks`) with key alias `upload` |
| `STORE_PASSWORD` | Keystore password |
| `KEY_PASSWORD` | Key password for the `upload` alias |

*(If these secrets are not configured, the workflow automatically generates a fallback signing key so CI release builds still succeed.)*

---

## Building Locally

### Prerequisites
- **JDK 21**
- **Android SDK Platform 36** (`compileSdk 36`, `minSdk 24`)
- **Gradle 9.3.1** (as configured in `gradle/wrapper/gradle-wrapper.properties`)

### Commands
```bash
# 1. Decode the debug keystore (first time only)
base64 --decode debug.keystore.base64 > debug.keystore

# 2. Copy default environment file if needed
cp -n .env.example .env

# 3. Build the Debug APK
gradle :app:assembleDebug

# 4. Run Unit & Robolectric Tests
gradle :app:testDebugUnitTest
```

The compiled Debug APK will be output to:
`app/build/outputs/apk/debug/app-debug.apk`
