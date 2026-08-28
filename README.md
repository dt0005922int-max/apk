# Cipher Chat (Android)

**Encrypt • Share • Decrypt**

A two-person secret message app for Android, built the same way as Visa Checklist: Kotlin, Jetpack Compose, no backend, no API keys. Person A encrypts a message. Person B pastes the encrypted message, sets the same shift, and enters the shared key to recover the original English text exactly.

## Features

- Caesar shift (1–25) plus Vigenère secret key
- Clean encrypted output — no prefix, nothing reveals which cipher was used (shift and key are shared separately; old `CC1|S3|…` messages still decrypt)
- Copy and Android share sheet (WhatsApp, Messenger, email, and others)
- Show / hide / generate / copy key (`SecureRandom`, not `Math.random`)
- Local history on this phone (DataStore) — keys are never saved
- Reversibility self-test

## Download the APK (GitHub Actions)

Same process as Visa Checklist. You do not need Android Studio on this PC.

If this folder is inside the **visa-checklist** repo:

1. Push to GitHub.
2. Open **Actions → Build Cipher Chat APK**.
3. Download the **cipher-chat-debug-apk** artifact.
4. Unzip it and copy `app-debug.apk` to your phone.
5. On Android: allow install from that source, then open the APK.

If you publish **cipher-chat-android** as its own repo, use **Actions → Build APK**.

The debug APK is for personal testing, not Play Store.

## Open in Android Studio

1. **File → Open** → the `cipher-chat-android` folder
2. Wait for Gradle sync
3. Press **Run** on a phone or emulator (API 26+)

## Security warning

Caesar and Vigenère are classical ciphers and are not secure against modern cryptographic attacks. Do not use this app for passwords, banking information, financial data, or highly sensitive information.
