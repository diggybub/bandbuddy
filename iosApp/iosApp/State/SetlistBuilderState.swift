import Foundation
import SwiftUI
import shared

// MARK: - Setlist Builder State
@MainActor
class SetlistBuilderState: ObservableObject {
    @Published var setlistItems: [SetlistItem] = []
    @Published var isLoading = false
    @Published var availableSongs: [Song] = []
    
    private let setlistUseCase = DependencyContainer.setlistUseCase
    private let songUseCase = DependencyContainer.songUseCase
    
    func loadSetlistItems(setlistId: String) {
        isLoading = true
        Task {
            do {
                let fullSetlist = try await setlistUseCase.getSetlistById(id: setlistId)
                let songs = try await songUseCase.getAllSongs()
                setlistItems = fullSetlist?.items.sorted { $0.order < $1.order } ?? []
                availableSongs = songs
                isLoading = false
            } catch {
                print("Error loading setlist items: \(error)")
                isLoading = false
            }
        }
    }
    
    func addSongToSetlist(song: Song, setlistId: String) {
        Task {
            do {
                _ = try await setlistUseCase.addSongToSetlist(
                    setlistId: setlistId,
                    songId: song.id,
                    notes: "",
                    segue: ""
                )
                await loadSetlistItems(setlistId: setlistId)
            } catch {
                print("Error adding song to setlist: \(error)")
            }
        }
    }
    
    func removeItem(_ item: SetlistItem) {
        Task {
            do {
                try await setlistUseCase.removeSetlistItem(itemId: item.id)
                await loadSetlistItems(setlistId: item.setlistId)
            } catch {
                print("Error removing item: \(error)")
            }
        }
    }
    
    func updateItem(_ item: SetlistItem) {
        Task {
            do {
                _ = try await setlistUseCase.updateSetlistItem(
                    itemId: item.id,
                    notes: item.notes,
                    segue: item.segue
                )
                await loadSetlistItems(setlistId: item.setlistId)
            } catch {
                print("Error updating item: \(error)")
            }
        }
    }
    
    func moveItems(from: IndexSet, to: Int) {
        var newItems = setlistItems
        newItems.move(fromOffsets: from, toOffset: to)
        
        // Update the local list immediately for UI responsiveness
        setlistItems = newItems
        
        // Update order in the backend
        Task {
            do {
                for (index, item) in newItems.enumerated() {
                    try await setlistUseCase.reorderSetlistItem(itemId: item.id, newOrder: Int32(index + 1))
                }
            } catch {
                print("Error reordering items: \(error)")
                // Reload to get the correct state if reordering failed
                await loadSetlistItems(setlistId: newItems.first?.setlistId ?? "")
            }
        }
    }
    
    func randomizeOrder() {
        let shuffledItems = setlistItems.shuffled()
        setlistItems = shuffledItems
        
        // Update order in the backend
        Task {
            do {
                for (index, item) in shuffledItems.enumerated() {
                    try await setlistUseCase.reorderSetlistItem(itemId: item.id, newOrder: Int32(index + 1))
                }
            } catch {
                print("Error randomizing order: \(error)")
                // Reload to get the correct state if reordering failed
                await loadSetlistItems(setlistId: shuffledItems.first?.setlistId ?? "")
            }
        }
    }
    
    func shareSetlist(_ setlist: Setlist) {
        var shareText = String(repeating: "=", count: 50) + "\n"
        shareText += "SETLIST: \(setlist.name)\n"
        shareText += String(repeating: "=", count: 50) + "\n\n"
        shareText += "Venue: \(setlist.venue)\n"
        shareText += "Date: \(setlist.date)\n\n"
        
        for (index, item) in setlistItems.enumerated() {
            // Find the song for this item
            if let song = availableSongs.first(where: { $0.id == item.songId }) {
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
        
        shareText += String(repeating: "-", count: 50) + "\n"
        shareText += "Total Songs: \(setlistItems.count)\n"
        shareText += "Estimated Duration: ~\(Int(Double(setlistItems.count) * 3.5)) minutes\n"
        
        // Share using UIActivityViewController
        let activityVC = UIActivityViewController(activityItems: [shareText], applicationActivities: nil)
        
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let window = windowScene.windows.first {
            window.rootViewController?.present(activityVC, animated: true)
        }
    }
}