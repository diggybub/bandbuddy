import SwiftUI

// MARK: - Main Tab View
struct MainView: View {
    @StateObject private var profileState = ProfileState()
    @StateObject private var songsState = SongsState()
    @StateObject private var setlistsState = SetlistsState()
    @State private var selectedTab = 0
    
    let onLogout: () -> Void
    
    var body: some View {
        TabView(selection: $selectedTab) {
            SongsView(songsState: songsState)
                .tabItem {
                    Image(systemName: "music.note")
                    Text("Songs")
                }
                .tag(0)
            
            SetlistsView(setlistsState: setlistsState)
                .tabItem {
                    Image(systemName: "music.note.list")
                    Text("Setlists")
                }
                .tag(1)
            
            ProfileView(profileState: profileState, onLogout: onLogout)
                .tabItem {
                    Image(systemName: "person.circle")
                    Text("Profile")
                }
                .tag(2)
        }
    }
}