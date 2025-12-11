# MatCal

**A minimalist calendar for Android.**

Recently I switched to GrapheneOS. And as it turns out, it does not ship with a built-in calendar app, so I created MatCal as both a personal learning project and a practical solution. 
The goal is to provide a lightweight, functional, and system-native calendar experience without compromising privacy.

## Features

- Create, view and manage events
- Local-only storage (SQLite via Room)
- Import and sync external calendars via iCal subscription (optional. requires internet)

## Privacy

Events are stored locally in an embedded SQLite database managed through Room.

MatCal includes **no trackers, analytics, or background data collection**.

Network access is only used when importing and or syncing iCal subscriptions.

## Design

MatCal utilizes the system's **Dynamic Colors**, adapting automatically to your wallpaper and system theme (light/dark mode)

## Screenshots

*Coming soon*

## Requirements

- Everything was tested on a Google Pixel 9a with GrapheneOS and Android 16.0 (Baklava) with API Level 36.0.
- Dynamic Colors need at least Android 12+.

## Download

You can grab the latest APK from the [Releases Page](https://github.com/MaKhandare/MatCal/releases).

## Tech Stack

- Language: [Kotlin](https://kotlinlang.org/)
- UI: [Jetpack Compose](https://developer.android.com/compose) & [Material 3](https://developer.android.com/develop/ui/compose/designsystems/material3)
- Database: [Room](https://developer.android.com/training/data-storage/room).

## Contributing

MatCal is a learning-focused project and contributions are appreciated!

If you find a bug or have an idea for improvement, feel free to open an issue or submit a pull request.

## License

Distributed under MIT License. See [LICENSE](https://github.com/MaKhandare/MatCal/blob/main/LICENSE) for more information.
