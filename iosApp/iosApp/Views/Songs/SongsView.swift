import SwiftUI
import shared

// MARK: - Songs View
struct SongsView: View {
    @ObservedObject var songsState: SongsState
    @State private var showingAddDialog = false
    @State private var searchText = ""
    @State private var selectedStatus: SongStatus? = nil
    @State private var viewMode: ViewMode = .list
    
    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                // Search bar
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.secondary)
                    TextField("Search songs...", text: $searchText)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                }
                .padding(.horizontal)
                
                // Filter chips
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        FilterChip(title: "All", isSelected: selectedStatus == nil) {
                            selectedStatus = nil
                        }
                        FilterChip(title: "Known (\(songsState.songs.filter { $0.status == SongStatus.known }.count))", 
                                 isSelected: selectedStatus == SongStatus.known) {
                            selectedStatus = SongStatus.known
                        }
                        FilterChip(title: "To Learn (\(songsState.songs.filter { $0.status == SongStatus.toLearn }.count))", 
                                 isSelected: selectedStatus == SongStatus.toLearn) {
                            selectedStatus = SongStatus.toLearn
                        }
                    }
                    .padding(.horizontal)
                }
                
                // Action buttons
                HStack(spacing: 12) {
                    Button("Mark All Known") {
                        songsState.markAllKnown()
                    }
                    .buttonStyle(.borderedProminent)
                    .disabled(filteredSongs.filter { $0.status == SongStatus.toLearn }.isEmpty)
                    
                    Button("Quick Setlist") {
                        songsState.generateQuickSetlist()
                    }
                    .buttonStyle(.bordered)
                    .disabled(filteredSongs.filter { $0.status == SongStatus.known }.count < 3)
                }
                .padding(.horizontal)
                
                // Songs list
                if songsState.isLoading {
                    Spacer()
                    ProgressView()
                    Spacer()
                } else if filteredSongs.isEmpty && !searchText.isEmpty {
                    Spacer()
                    VStack {
                        Text("No songs found for \"\(searchText)\"")
                        Button("Clear Search") {
                            searchText = ""
                        }
                    }
                    Spacer()
                } else if songsState.songs.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Text("No songs yet. Add your first song!")
                        Button("Add Sample Songs") {
                            songsState.addSampleSongs()
                        }
                        .buttonStyle(.bordered)
                    }
                    Spacer()
                } else {
                    List {
                        if viewMode == .grouped {
                            ForEach(groupedSongs.keys.sorted(), id: \.self) { artist in
                                Section(header: Text(artist).font(.headline)) {
                                    ForEach(groupedSongs[artist] ?? [], id: \.id) { song in
                                        SongRow(song: song) { newStatus in
                                            songsState.updateSongStatus(song.id, status: newStatus)
                                        }
                                    }
                                }
                            }
                        } else {
                            ForEach(filteredSongs, id: \.id) { song in
                                SongRow(song: song) { newStatus in
                                    songsState.updateSongStatus(song.id, status: newStatus)
                                }
                            }
                        }
                    }
                }
            }
            .navigationTitle("Songs (\(filteredSongs.count))")
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(viewMode == .list ? "📋" : "🎯") {
                        viewMode = viewMode == .list ? .grouped : .list
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    HStack {
                        Button("📤") {
                            songsState.shareSongLibrary()
                        }
                        .disabled(songsState.songs.isEmpty)
                        
                        Button("+") {
                            showingAddDialog = true
                        }
                    }
                }
            }
            .sheet(isPresented: $showingAddDialog) {
                AddSongView { title, artist in
                    songsState.addSong(title: title, artist: artist)
                    showingAddDialog = false
                }
            }
            .onAppear {
                songsState.loadSongs()
            }
        }
    }
    
    private var filteredSongs: [Song] {
        songsState.songs.filter { song in
            let matchesSearch = searchText.isEmpty ||
                song.title.localizedCaseInsensitiveContains(searchText) ||
                song.artist.localizedCaseInsensitiveContains(searchText)
            let matchesStatus = selectedStatus == nil || song.status == selectedStatus
            return matchesSearch && matchesStatus
        }
    }
    
    private var groupedSongs: [String: [Song]] {
        Dictionary(grouping: filteredSongs) { $0.artist }
    }
}

// MARK: - View Mode Enum
enum ViewMode {
    case list, grouped
}