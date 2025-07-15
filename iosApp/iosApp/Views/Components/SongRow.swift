import SwiftUI
import shared

// MARK: - Song Row Component
struct SongRow: View {
    let song: Song
    let onStatusChange: (SongStatus) -> Void
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(song.title)
                    .font(.headline)
                Text(song.artist)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Button(song.status == SongStatus.known ? "Known" : "To Learn") {
                onStatusChange(song.status == SongStatus.known ? SongStatus.toLearn : SongStatus.known)
            }
            .buttonStyle(.bordered)
            .foregroundColor(song.status == SongStatus.known ? .green : .orange)
        }
        .padding(.vertical, 4)
    }
}