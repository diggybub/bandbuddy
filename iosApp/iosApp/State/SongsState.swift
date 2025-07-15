import Foundation
import SwiftUI
import shared

// MARK: - Songs State
@MainActor
class SongsState: ObservableObject {
    @Published var songs: [Song] = []
    @Published var isLoading = false
    
    private let songUseCase = DependencyContainer.songUseCase
    
    func loadSongs() {
        isLoading = true
        Task {
            do {
                let loadedSongs = try await songUseCase.getAllSongs()
                songs = loadedSongs
                isLoading = false
            } catch {
                print("Error loading songs: \(error)")
                isLoading = false
            }
        }
    }
    
    func addSong(title: String, artist: String) {
        Task {
            do {
                try await songUseCase.addSong(title: title, artist: artist, status: SongStatus.toLearn)
                await loadSongs()
            } catch {
                print("Error adding song: \(error)")
            }
        }
    }
    
    func updateSongStatus(_ songId: String, status: SongStatus) {
        Task {
            do {
                try await songUseCase.updateSongStatus(songId: songId, status: status)
                await loadSongs()
            } catch {
                print("Error updating song status: \(error)")
            }
        }
    }
    
    func markAllKnown() {
        Task {
            do {
                for song in songs.filter({ $0.status == SongStatus.toLearn }) {
                    try await songUseCase.updateSongStatus(songId: song.id, status: SongStatus.known)
                }
                await loadSongs()
            } catch {
                print("Error marking songs as known: \(error)")
            }
        }
    }
    
    func addSampleSongs() {
        Task {
            do {
                try await songUseCase.addSong(title: "Stairway to Heaven", artist: "Led Zeppelin", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "Black Dog", artist: "Led Zeppelin", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "Bohemian Rhapsody", artist: "Queen", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "We Will Rock You", artist: "Queen", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "Sweet Child O' Mine", artist: "Guns N' Roses", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "Paradise City", artist: "Guns N' Roses", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "Hotel California", artist: "Eagles", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "Take It Easy", artist: "Eagles", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "Smells Like Teen Spirit", artist: "Nirvana", status: SongStatus.toLearn)
                try await songUseCase.addSong(title: "Come As You Are", artist: "Nirvana", status: SongStatus.toLearn)
                await loadSongs()
            } catch {
                print("Error adding sample songs: \(error)")
            }
        }
    }
    
    func generateQuickSetlist() {
        Task {
            do {
                let knownSongs = songs.filter { $0.status == SongStatus.known }
                let randomSongs = knownSongs.shuffled().prefix(8)
                
                let calendar = Calendar.current
                let dateComponents = calendar.dateComponents([.year, .month, .day], from: Date())
                let localDate = Kotlinx_datetimeLocalDate(
                    year: Int32(dateComponents.year ?? 2024),
                    monthNumber: Int32(dateComponents.month ?? 1),
                    dayOfMonth: Int32(dateComponents.day ?? 1)
                )
                
                let setlistName = "Quick Setlist \(DateFormatter().string(from: Date()))"
                let newSetlist = try await DependencyContainer.setlistUseCase.createSetlist(
                    name: setlistName,
                    date: localDate,
                    venue: "Generated Setlist"
                )
                
                // Add songs to the setlist
                for (index, song) in randomSongs.enumerated() {
                    try await DependencyContainer.setlistUseCase.addSongToSetlist(
                        setlistId: newSetlist.id,
                        songId: song.id,
                        notes: "",
                        segue: ""
                    )
                }
                
                print("Generated quick setlist: \(setlistName) with \(randomSongs.count) songs")
            } catch {
                print("Error generating quick setlist: \(error)")
            }
        }
    }
    
    func shareSongLibrary() {
        let groupedSongs = Dictionary(grouping: songs.sorted { $0.artist < $1.artist }) { $0.artist }
        
        var shareText = String(repeating: "=", count: 50) + "\n"
        shareText += "SONG LIBRARY\n"
        shareText += String(repeating: "=", count: 50) + "\n\n"
        
        for (artist, artistSongs) in groupedSongs.sorted(by: { $0.key < $1.key }) {
            shareText += "\(artist):\n"
            for song in artistSongs.sorted(by: { $0.title < $1.title }) {
                let status = song.status == SongStatus.known ? "[READY]" : "[LEARN]"
                shareText += "  • \(song.title) \(status)\n"
            }
            shareText += "\n"
        }
        
        let knownCount = songs.filter { $0.status == SongStatus.known }.count
        let totalCount = songs.count
        let readyPercentage = totalCount > 0 ? (knownCount * 100) / totalCount : 0
        
        shareText += String(repeating: "-", count: 50) + "\n"
        shareText += "SUMMARY\n"
        shareText += String(repeating: "-", count: 50) + "\n"
        shareText += "Total Songs: \(totalCount)\n"
        shareText += "Ready Songs: \(knownCount)\n"
        shareText += "Readiness: \(readyPercentage)%\n"
        shareText += "Artists: \(groupedSongs.count)\n"
        
        // Share using UIActivityViewController
        let activityVC = UIActivityViewController(activityItems: [shareText], applicationActivities: nil)
        
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let window = windowScene.windows.first {
            window.rootViewController?.present(activityVC, animated: true)
        }
    }
}