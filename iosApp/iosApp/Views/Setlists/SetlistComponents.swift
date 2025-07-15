import SwiftUI
import shared

// MARK: - Setlist Row Component
struct SetlistRow: View {
    let setlist: Setlist
    let onEdit: () -> Void
    let onCopy: () -> Void
    let onDelete: () -> Void
    let onShare: () -> Void
    @State private var showingOptions = false
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(setlist.name)
                    .font(.headline)
                Text(setlist.venue)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Text(setlist.date.description)
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                HStack(spacing: 16) {
                    Text("\(setlist.items.count) songs")
                        .font(.caption)
                        .foregroundColor(.blue)
                    if !setlist.items.isEmpty {
                        Text("~\(Int(Double(setlist.items.count) * 3.5))min")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
            
            Spacer()
            
            Button("⋮") {
                showingOptions = true
            }
        }
        .contentShape(Rectangle())
        .onTapGesture {
            onEdit()
        }
        .actionSheet(isPresented: $showingOptions) {
            ActionSheet(title: Text(setlist.name), buttons: [
                .default(Text("Edit")) { onEdit() },
                .default(Text("Share")) { onShare() },
                .default(Text("Copy")) { onCopy() },
                .destructive(Text("Delete")) { onDelete() },
                .cancel()
            ])
        }
    }
}

// MARK: - Create Setlist View
struct CreateSetlistView: View {
    @State private var name = ""
    @State private var venue = ""
    @State private var date = Date()
    @Environment(\.presentationMode) var presentationMode
    let onCreate: (String, String, Date) -> Void
    
    var body: some View {
        NavigationView {
            Form {
                Section {
                    TextField("", text: $name)
                        .placeholder(when: name.isEmpty) {
                            Text("Setlist Name").foregroundColor(.gray)
                        }
                    TextField("", text: $venue)
                        .placeholder(when: venue.isEmpty) {
                            Text("Venue").foregroundColor(.gray)
                        }
                    DatePicker("Date", selection: $date, displayedComponents: .date)
                }
            }
            .navigationTitle("New Setlist")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Create") {
                        onCreate(name, venue, date)
                    }
                    .disabled(name.isEmpty || venue.isEmpty)
                }
            }
        }
    }
}

// MARK: - Setlist Builder View
struct SetlistBuilderView: View {
    let setlist: Setlist
    let onDismiss: () -> Void
    
    @StateObject private var builderState = SetlistBuilderState()
    @State private var showingSongPicker = false
    @State private var selectedItem: SetlistItem? = nil
    @State private var showingItemEditor = false
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Header with setlist info
                VStack(spacing: 8) {
                    Text(setlist.name)
                        .font(.title2)
                        .fontWeight(.bold)
                    HStack {
                        Text(setlist.venue)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        Spacer()
                        Text(setlist.date.description)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    
                    HStack(spacing: 16) {
                        Text("\(builderState.setlistItems.count) songs")
                            .font(.caption)
                            .foregroundColor(.blue)
                        if !builderState.setlistItems.isEmpty {
                            Text("~\(Int(Double(builderState.setlistItems.count) * 3.5))min")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                
                // Action buttons
                HStack(spacing: 12) {
                    Button("Add Song") {
                        showingSongPicker = true
                    }
                    .buttonStyle(.borderedProminent)
                    
                    Button("Random Order") {
                        builderState.randomizeOrder()
                    }
                    .buttonStyle(.bordered)
                    .disabled(builderState.setlistItems.count < 2)
                    
                    Button("Share") {
                        builderState.shareSetlist(setlist)
                    }
                    .buttonStyle(.bordered)
                    .disabled(builderState.setlistItems.isEmpty)
                }
                .padding()
                
                // Setlist items
                if builderState.isLoading {
                    Spacer()
                    ProgressView()
                    Spacer()
                } else if builderState.setlistItems.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Text("No songs in setlist")
                            .font(.title3)
                        Text("Add songs to build your setlist")
                            .foregroundColor(.secondary)
                        Button("Add First Song") {
                            showingSongPicker = true
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    Spacer()
                } else {
                    List {
                        ForEach(builderState.setlistItems.indices, id: \.self) { index in
                            SetlistItemRow(
                                item: builderState.setlistItems[index],
                                song: builderState.availableSongs.first(where: { $0.id == builderState.setlistItems[index].songId }),
                                position: index + 1,
                                onEdit: { item in
                                    selectedItem = item
                                    showingItemEditor = true
                                },
                                onDelete: { item in
                                    builderState.removeItem(item)
                                }
                            )
                        }
                        .onMove { from, to in
                            builderState.moveItems(from: from, to: to)
                        }
                    }
                    .listStyle(PlainListStyle())
                }
            }
            .navigationTitle("Setlist Builder")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Done") {
                        onDismiss()
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    EditButton()
                }
            }
            .sheet(isPresented: $showingSongPicker) {
                SongPickerView(setlistId: setlist.id) { song in
                    builderState.addSongToSetlist(song: song, setlistId: setlist.id)
                    showingSongPicker = false
                }
            }
            .sheet(isPresented: $showingItemEditor) {
                if let item = selectedItem {
                    let song = builderState.availableSongs.first(where: { $0.id == item.songId })
                    SetlistItemEditorView(item: item, song: song) { updatedItem in
                        builderState.updateItem(updatedItem)
                        showingItemEditor = false
                        selectedItem = nil
                    }
                }
            }
            .onAppear {
                builderState.loadSetlistItems(setlistId: setlist.id)
            }
        }
    }
}

// MARK: - Setlist Item Row Component
struct SetlistItemRow: View {
    let item: SetlistItem
    let song: Song?
    let position: Int
    let onEdit: (SetlistItem) -> Void
    let onDelete: (SetlistItem) -> Void
    @State private var showingOptions = false
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text("\(position).")
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .frame(width: 20, alignment: .leading)
                    Text(song?.title ?? "Unknown Song")
                        .font(.headline)
                }
                
                HStack {
                    Text("   \(song?.artist ?? "Unknown Artist")")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                
                if !item.notes.isEmpty {
                    HStack {
                        Text("   Notes: \(item.notes)")
                            .font(.caption)
                            .foregroundColor(.blue)
                    }
                }
                
                if !item.segue.isEmpty {
                    HStack {
                        Text("   Segue: \(item.segue)")
                            .font(.caption)
                            .foregroundColor(.green)
                    }
                }
            }
            
            Spacer()
            
            Button("⋮") {
                showingOptions = true
            }
        }
        .contentShape(Rectangle())
        .onTapGesture {
            onEdit(item)
        }
        .actionSheet(isPresented: $showingOptions) {
            ActionSheet(title: Text(song?.title ?? "Unknown Song"), buttons: [
                .default(Text("Edit")) { onEdit(item) },
                .destructive(Text("Remove")) { onDelete(item) },
                .cancel()
            ])
        }
    }
}

// MARK: - Song Picker View
struct SongPickerView: View {
    let setlistId: String
    let onSongSelected: (Song) -> Void
    
    @StateObject private var songsState = SongsState()
    @State private var searchText = ""
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            VStack {
                // Search bar
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.secondary)
                    TextField("Search songs...", text: $searchText)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                }
                .padding()
                
                if songsState.songs.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Text("No songs available")
                        Text("Add songs to your library first")
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                } else {
                    List(filteredSongs, id: \.id) { song in
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(song.title)
                                    .font(.headline)
                                Text(song.artist)
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            if song.status == SongStatus.known {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundColor(.green)
                            }
                        }
                        .contentShape(Rectangle())
                        .onTapGesture {
                            onSongSelected(song)
                        }
                    }
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
            }
            .onAppear {
                songsState.loadSongs()
            }
        }
    }
    
    private var filteredSongs: [Song] {
        if searchText.isEmpty {
            return songsState.songs
        } else {
            return songsState.songs.filter { song in
                song.title.localizedCaseInsensitiveContains(searchText) ||
                song.artist.localizedCaseInsensitiveContains(searchText)
            }
        }
    }
}

// MARK: - Setlist Item Editor View
struct SetlistItemEditorView: View {
    let item: SetlistItem
    let song: Song?
    let onSave: (SetlistItem) -> Void
    
    @State private var notes: String
    @State private var segue: String
    @Environment(\.presentationMode) var presentationMode
    
    init(item: SetlistItem, song: Song?, onSave: @escaping (SetlistItem) -> Void) {
        self.item = item
        self.song = song
        self.onSave = onSave
        self._notes = State(initialValue: item.notes)
        self._segue = State(initialValue: item.segue)
    }
    
    var body: some View {
        NavigationView {
            Form {
                Section {
                    VStack(alignment: .leading, spacing: 8) {
                        Text(song?.title ?? "Unknown Song")
                            .font(.headline)
                        Text(song?.artist ?? "Unknown Artist")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                } header: {
                    Text("Song")
                }
                
                Section {
                    TextField("Performance notes...", text: $notes, axis: .vertical)
                        .lineLimit(3...6)
                } header: {
                    Text("Notes")
                } footer: {
                    Text("Add any performance notes or reminders")
                }
                
                Section {
                    TextField("Transition to next song...", text: $segue, axis: .vertical)
                        .lineLimit(2...4)
                } header: {
                    Text("Segue")
                } footer: {
                    Text("How to transition into the next song")
                }
            }
            .navigationTitle("Edit Song")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Save") {
                        let updatedItem = SetlistItem(
                            id: item.id,
                            setlistId: item.setlistId,
                            songId: item.songId,
                            order: item.order,
                            notes: notes,
                            segue: segue
                        )
                        onSave(updatedItem)
                    }
                }
            }
        }
    }
}
