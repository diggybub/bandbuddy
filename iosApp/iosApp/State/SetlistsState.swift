import Foundation
import SwiftUI
import shared

// MARK: - Setlists State
@MainActor
class SetlistsState: ObservableObject {
    @Published var setlists: [Setlist] = []
    @Published var isLoading = false
    @Published var showingSetlistBuilder = false
    @Published var selectedSetlistForBuilder: Setlist? = nil
    
    private let setlistUseCase = DependencyContainer.setlistUseCase
    
    func loadSetlists() {
        isLoading = true
        Task {
            do {
                let loadedSetlists = try await setlistUseCase.getAllSetlists()
                setlists = loadedSetlists
                isLoading = false
            } catch {
                print("Error loading setlists: \(error)")
                isLoading = false
            }
        }
    }
    
    func createSetlist(name: String, venue: String, date: Date) {
        Task {
            do {
                // Convert Swift Date to LocalDate
                let calendar = Calendar.current
                let dateComponents = calendar.dateComponents([.year, .month, .day], from: date)
                let localDate = Kotlinx_datetimeLocalDate(
                    year: Int32(dateComponents.year ?? 2024),
                    monthNumber: Int32(dateComponents.month ?? 1),
                    dayOfMonth: Int32(dateComponents.day ?? 1)
                )
                
                let newSetlist = try await setlistUseCase.createSetlist(name: name, date: localDate, venue: venue)
                selectedSetlistForBuilder = newSetlist
                showingSetlistBuilder = true
                await loadSetlists()
            } catch {
                print("Error creating setlist: \(error)")
            }
        }
    }
    
    func editSetlist(_ setlist: Setlist) {
        selectedSetlistForBuilder = setlist
        showingSetlistBuilder = true
    }
    
    func copySetlist(_ setlist: Setlist) {
        Task {
            do {
                // Create a new setlist with copied name
                let calendar = Calendar.current
                let dateComponents = calendar.dateComponents([.year, .month, .day], from: Date())
                let localDate = Kotlinx_datetimeLocalDate(
                    year: Int32(dateComponents.year ?? 2024),
                    monthNumber: Int32(dateComponents.month ?? 1),
                    dayOfMonth: Int32(dateComponents.day ?? 1)
                )
                
                let newSetlist = try await setlistUseCase.createSetlist(
                    name: "\(setlist.name) (Copy)",
                    date: localDate,
                    venue: setlist.venue
                )
                
                // Copy all items from the original setlist
                for (index, item) in setlist.items.enumerated() {
                    try await setlistUseCase.addSongToSetlist(
                        setlistId: newSetlist.id,
                        songId: item.songId,
                        notes: item.notes,
                        segue: item.segue
                    )
                }
                
                await loadSetlists()
            } catch {
                print("Error copying setlist: \(error)")
            }
        }
    }
    
    func deleteSetlist(_ setlist: Setlist) {
        Task {
            do {
                try await setlistUseCase.deleteSetlist(setlistId: setlist.id)
                await loadSetlists()
            } catch {
                print("Error deleting setlist: \(error)")
            }
        }
    }
    
    func shareSetlist(_ setlist: Setlist) {
        Task {
            do {
                let fullSetlist = try await setlistUseCase.getSetlistById(id: setlist.id)
                let songs = try await DependencyContainer.songUseCase.getAllSongs()
                
                var shareText = String(repeating: "=", count: 50) + "\n"
                shareText += "SETLIST: \(setlist.name)\n"
                shareText += String(repeating: "=", count: 50) + "\n\n"
                shareText += "Venue: \(setlist.venue)\n"
                shareText += "Date: \(setlist.date)\n\n"
                
                if let fullSetlist = fullSetlist {
                    for (index, item) in fullSetlist.items.enumerated() {
                        // Find the song for this item
                        if let song = songs.first(where: { $0.id == item.songId }) {
                            shareText += "\(index + 1). \(song.title) - \(song.artist)\n"
                            if !item.notes.isEmpty {
                                shareText += "   Notes: \(item.notes)\n"
                            }
                            if !item.segue.isEmpty {
                                shareText += "   Segue: \(item.segue)\n"
                            }
                            shareText += "\n"
                        }
                    }
                }
                
                shareText += String(repeating: "-", count: 50) + "\n"
                shareText += "Total Songs: \(setlist.items.count)\n"
                shareText += "Estimated Duration: ~\(Int(Double(setlist.items.count) * 3.5)) minutes\n"
                
                // Share using UIActivityViewController
                let activityVC = UIActivityViewController(activityItems: [shareText], applicationActivities: nil)
                
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                   let window = windowScene.windows.first {
                    window.rootViewController?.present(activityVC, animated: true)
                }
            } catch {
                print("Error sharing setlist: \(error)")
            }
        }
    }
}