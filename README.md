# Voice Notepad 🎙️

**Voice Notepad** is a simple, privacy-first Android application that allows users to record, play, and manage voice notes efficiently. Designed with modern Android development practices, this app stores all audio data locally using SQLite, ensuring full offline functionality and data privacy.

## ✅ Key Features

- 🎤 One-tap voice recording
- ▶️ Play back saved recordings
- 🗑️ Delete or rename recordings
- 🔘 Press-and-hold or toggle recording modes
- 🌙 Clean and intuitive user interface
- 🔒 **No internet access required** – all data is stored locally

## 🛠️ Tech Stack

- **Kotlin** with **Jetpack Compose** for UI
- **MVVM architecture** for scalable and maintainable code
- **Hilt** for Dependency Injection
- **SQLite** via a custom `SQLiteOpenHelper` for persistent local storage
- **MediaRecorder** and **MediaPlayer** for audio capture and playback
- No third-party analytics, tracking, or ads

## 🔐 Privacy & Security

- This app does **not** use internet access.
- No data is collected or transmitted externally.
- All voice recordings are saved **only** on the user’s device using SQLite.
- There are **no ads**, **no cloud backup**, and **no third-party SDKs**.


## 📥 Download

[Google Play Store](https://play.google.com/store/apps/details?id=com.agilefalcon.voicenotepad) 




