# Finora Android

Finora is an offline-first Android personal finance tracker for local testing.

## Build a debug APK

GitHub Actions runs the Android build on pushes to `main` and manual dispatches. Open the **Actions** tab, choose **Finora Android Build**, and download the `finora-android-build` artifact after a successful run.

The project stores app data locally on the device. Backup and restore use Android's document picker. This is a testing build, not a Play Store release.
