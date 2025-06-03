# BandBuddy 🎸

A comprehensive setlist and song management app for musicians and bands. Built with Kotlin
Multiplatform and Compose Multiplatform for Android and iOS.

## 🎵 Features

### Song Management

- **📱 Add Songs**: Easy form to add songs with title and artist
- **🗃️ Song Library**: View all your songs with search and filtering
- **📊 Status Tracking**: Mark songs as "Known" or "To Learn"
- **🔍 Smart Search**: Search by song title or artist name
- **👥 Grouped View**: Toggle between list view and artist-grouped view
- **📈 Filter by Status**: Show only known songs, songs to learn, or all
- **📤 Share Library**: Export and share your entire song collection

### Setlist Management

- **📝 Create Setlists**: Professional setlist creation with venue and date
- **🎯 Setlist Builder**: Advanced drag-and-drop setlist editor
- **🎪 Performance Details**: Add performance notes and segues between songs
- **📅 Venue & Date**: Organize setlists by performance details
- **🔄 Copy Setlists**: Duplicate existing setlists for similar gigs
- **📊 Readiness Tracking**: See how prepared you are for each performance
- **⏱️ Duration Estimates**: Automatic setlist timing calculations

### Enhanced User Interface

- **🎨 Material Design 3**: Modern, beautiful Android interface
- **🗓️ Date Picker**: Professional date selection for setlists
- **🖱️ Drag & Drop**: Long-press and drag to reorder setlist songs
- **✨ Smooth Animations**: Polished transitions and visual feedback
- **🎪 Visual Status**: Clear indicators for song readiness
- **📋 Instructional UI**: Helpful tips and guidance throughout the app

### Sharing & Export

- **📧 Email Sharing**: Send setlists directly to band members
- **📱 Text Sharing**: Share via SMS, WhatsApp, Slack, or any messaging app
- **📄 Professional Formatting**: Clean, readable export format
- **📊 Performance Analytics**: Include readiness stats and duration estimates
- **🎼 Complete Details**: Export with notes, segues, and venue information

## 🏗️ Technical Architecture

### Kotlin Multiplatform

- **🔧 Shared Business Logic**: Common domain layer across platforms
- **💾 SQLDelight Database**: Type-safe SQL with local storage
- **🏛️ Clean Architecture**: Separation of concerns with repository pattern
- **💉 Dependency Injection**: Koin for clean dependency management

### Android Implementation

- **🎨 Jetpack Compose**: Modern declarative UI framework
- **📱 Material Design 3**: Latest Android design system
- **🔄 Coroutines**: Asynchronous programming for smooth UX
- **💾 Local Database**: SQLite with SQLDelight for data persistence

### Testing

- **✅ Unit Tests**: Comprehensive test coverage for business logic
- **🧪 Repository Tests**: In-memory and SQLDelight implementations tested
- **🎯 Use Case Tests**: Domain layer fully tested with scenarios

## 📱 Screenshots & Demo

### Song Management

- **List View**: Clean song library with search and filters
- **Grouped View**: Songs organized by artist
- **Add Song Dialog**: Simple form for adding new songs
- **Status Tracking**: Visual indicators for song readiness

### Setlist Building

- **Setlist Overview**: Professional setlist cards with performance details
- **Drag & Drop Editor**: Intuitive reordering with visual feedback
- **Notes & Segues**: Detailed performance planning
- **Share Options**: Multiple ways to share with band members

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Kotlin 2.0.0+
- Gradle 8.0+

### Building the Project

```bash
# Clone the repository
git clone https://github.com/yourusername/bandbuddy.git
cd bandbuddy

# Build the Android app
./gradlew :androidApp:assembleDebug

# Run tests
./gradlew :shared:testDebugUnitTest
```

### Project Structure

```
bandbuddy/
├── androidApp/          # Android-specific implementation
│   ├── src/main/java/   # Android UI and platform code
│   └── build.gradle.kts # Android app configuration
├── shared/              # Kotlin Multiplatform shared code
│   ├── src/commonMain/  # Shared business logic
│   ├── src/commonTest/  # Shared tests
│   ├── src/androidMain/ # Android-specific shared code
│   └── build.gradle.kts # Shared module configuration
└── iosApp/              # iOS app (future implementation)
```

## 🛠️ Core Components

### Data Layer

- **📊 Data Models**: Song, Setlist, SetlistItem entities
- **🗄️ Repositories**: Abstract interfaces with multiple implementations
- **💾 SQLDelight**: Type-safe database queries and migrations
- **🧪 In-Memory**: Testing implementations for unit tests

### Domain Layer

- **🎵 Song Use Cases**: Add, update, delete, and organize songs
- **📝 Setlist Use Cases**: Create, edit, copy, and manage setlists
- **🔄 Business Logic**: Song status management and setlist operations

### Presentation Layer

- **🎨 Compose UI**: Modern Android interface components
- **🔄 State Management**: Reactive UI with Compose state
- **📱 Navigation**: Bottom navigation between main features
- **🎪 Interactive Elements**: Drag & drop, dialogs, and animations

## 🎯 Use Cases

### For Solo Musicians

- **📚 Song Catalog**: Keep track of your repertoire
- **🎭 Performance Planning**: Organize songs for different venues
- **📈 Progress Tracking**: Monitor which songs you've mastered
- **⏱️ Time Management**: Plan set lengths for different gig types

### For Bands

- **👥 Collaboration**: Share setlists with band members
- **🎪 Gig Preparation**: Ensure everyone knows the playlist
- **📊 Readiness Assessment**: See which songs need more practice
- **📧 Communication**: Easy sharing via email or messaging apps

### For Music Teachers

- **📚 Student Repertoire**: Track student song collections
- **📈 Progress Monitoring**: Monitor learning status across songs
- **🎯 Lesson Planning**: Organize songs for different skill levels
- **📤 Assignment Sharing**: Share practice lists with students

## 🔮 Future Enhancements

### Planned Features

- **☁️ Cloud Sync**: Backup and sync across devices
- **📱 iOS App**: Native iOS implementation
- **🎵 Audio Integration**: Link to streaming services or local files
- **📊 Analytics Dashboard**: Performance history and statistics
- **🎪 Collaboration Tools**: Real-time band planning features
- **📝 PDF Export**: Professional PDF setlist generation

### Technical Improvements

- **🔄 Automatic Backups**: Cloud storage integration
- **🌐 Web Version**: Browser-based access
- **🎵 Music Recognition**: Import from streaming playlists
- **📱 Offline Mode**: Full functionality without internet

## 🤝 Contributing

We welcome contributions! Please feel free to submit issues, feature requests, or pull requests.

### Development Setup

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add documentation for public APIs
- Include unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **🎨 Material Design**: Google's design system for Android
- **🔧 Kotlin Multiplatform**: JetBrains' cross-platform solution
- **💾 SQLDelight**: Square's type-safe SQL library
- **💉 Koin**: Lightweight dependency injection framework

---

**Built with ❤️ for musicians by musicians** 🎸🎵

*Ready to organize your music? Download BandBuddy and take your performances to the next level!*