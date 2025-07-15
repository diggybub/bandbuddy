# Shared UI Architecture

This directory contains the shared ViewModels and UI logic that can be used by both Android and iOS
apps.

## ViewModels (Shared)

### AuthViewModel

- Handles user authentication (sign in, sign up)
- Manages current user state
- Can be injected via Koin in both platforms

### ProfileViewModel

- Manages user profile data and band information
- Handles profile updates (name, band name)
- Manages logout functionality
- Can be injected via Koin in both platforms

## Usage in Platform-Specific Code

### Android

```kotlin
@Composable
fun MyScreen() {
    val authViewModel: AuthViewModel = koin.get()
    val profileViewModel: ProfileViewModel = koin.get()
    
    // Use ViewModels with Compose UI
}
```

### iOS (Future)

```swift
// ViewModels can be injected and used in SwiftUI
```

## Architecture Benefits

- Business logic is shared between platforms
- UI state management is consistent
- Authentication flows work identically on both platforms
- Easy to maintain and test

## Data Flow

1. **Data Layer**: Repositories (InMemoryAuthRepository, InMemoryBandRepository)
2. **Domain Layer**: ViewModels (AuthViewModel, ProfileViewModel)
3. **UI Layer**: Platform-specific Compose/SwiftUI screens

All authentication and profile management logic is now **platform-agnostic**!