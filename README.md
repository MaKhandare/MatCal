# MatCal

**A minimalist calendar for Android.**

GrapheneOS does not ship with a default calendar, so I decided to build my own solution while learning Android Development. 
The goal is primarily learning Android Development with a functional, privacy-respecting calendar app that fits the system aesthetic.

## Features

- Create, view and manage events
- Local-only storage (SQLite via Room)
- Import and sync external calendars via iCal subscription (requires internet)

## Privacy

All events are saved locally in a SQLite database handled via Room. 
MatCal does not contain any trackers or analytics. 
The only time network access is used is to optionally sync your read-only iCal subscriptions.

## Design

MatCal utilizes the system's **Dynamic Colors**, adapting automatically to your wallpaper and system theme (light/dark mode)

## Screenshots

*Coming soon*

## Download

You can grab the latest APK from the [Releases Page](https://github.com/MaKhandare/MatCal/releases).

## Tech Stack

- Language: [Kotlin](https://kotlinlang.org/)
- UI: [Jetpack Compose](https://developer.android.com/compose) & [Material 3](https://developer.android.com/develop/ui/compose/designsystems/material3)
- Architecture: MVVM
- Database: [Room](https://developer.android.com/training/data-storage/room).

## Contributing

Since I am learning, feedback and pull requests are appreciated! 
If you find a bug or have a suggestion, feel free to open an issue.

## License

Distributed under MIT License. See [LICENSE](https://github.com/MaKhandare/MatCal/blob/main/LICENSE) for more information.

