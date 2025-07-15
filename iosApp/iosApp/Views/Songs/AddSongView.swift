import SwiftUI

// MARK: - Add Song View
struct AddSongView: View {
    @State private var title = ""
    @State private var artist = ""
    @Environment(\.presentationMode) var presentationMode
    let onAdd: (String, String) -> Void
    
    var body: some View {
        NavigationView {
            Form {
                Section {
                    TextField("Song Title", text: $title)
                    TextField("Artist", text: $artist)
                }
            }
            .navigationTitle("Add Song")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Add") {
                        onAdd(title, artist)
                    }
                    .disabled(title.isEmpty || artist.isEmpty)
                }
            }
        }
    }
}