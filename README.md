# MatCal

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)

**A minimalist calendar for Android.**

Recently I switched to [GrapheneOS](https://grapheneos.org/). And as it turns out, it does not ship with a built-in calendar app. So I created MatCal as both a personal learning project and a practical solution.
The goal is to provide a lightweight, functional, and system-native calendar experience without compromising privacy.

## Features

- **Event Management**: Create, view and manage events
- **Offline first**: All data is stored locally using SQLite (Room)
- **iCal Sync**: Import and sync external calendars (University, Work, Holidays) via URL
- **No Bloat**: No ads, no tracking, no unneeded permissions

## Privacy

Events are stored locally in an embedded SQLite database managed through Room.

MatCal includes **no trackers, analytics, or background data collection**.

`android.permissions.INTERNET` is **only** used when you explicitly add or refresh an iCal subscription URL

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
- Libraries:
    - [Biweekly](https://github.com/mangstadt/biweekly): Parsing iCalendar (.ics)
    - [Calendar View](https://github.com/kizitonwose/Calendar): The core Compose calendar component
    - [AboutLibraries](https://github.com/mikepenz/AboutLibraries): Automatic open-source license attribution.

## Contributing

MatCal is a learning-focused project and contributions are appreciated!

If you find a bug or have an idea for improvement, feel free to open an issue or submit a pull request.

## License

Distributed under MIT License. See [LICENSE](https://github.com/MaKhandare/MatCal/blob/main/LICENSE) for more information.
