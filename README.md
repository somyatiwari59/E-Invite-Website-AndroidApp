# Wedding Invites (Android)

Standalone sender app. Guests still open the wedding **website** from the WhatsApp link.

- Somya’s side → `https://wedding.st59.in`
- Yashika’s side → `https://wedding.yashikabum.in`

## Build

1. Install [Android Studio](https://developer.android.com/studio).
2. **File → Open** this `android` folder (not the repo root).
3. Run the **app** configuration, or:

```bash
cd android
./gradlew assembleRelease
```

APK: `app/build/outputs/apk/release/app-release.apk`

Install on the phone (allow unknown sources).

## Domain (App Links)

Signed with `keystore/invite.jks`. Fingerprint is in `website/public/.well-known/assetlinks.json`.

`https://wedding.st59.in/admin` and `https://wedding.yashikabum.in/admin` can open this app. Guest links stay `/?i=…` on the website.
