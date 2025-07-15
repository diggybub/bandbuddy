import SwiftUI

// MARK: - Setlists View
struct SetlistsView: View {
    @ObservedObject var setlistsState: SetlistsState
    @State private var showingCreateDialog = false
    
    var body: some View {
        NavigationView {
            VStack {
                if setlistsState.isLoading {
                    Spacer()
                    ProgressView()
                    Spacer()
                } else if setlistsState.setlists.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Text("No setlists yet")
                            .font(.title2)
                        Text("Create your first setlist to get started")
                            .foregroundColor(.secondary)
                        Button("Create First Setlist") {
                            showingCreateDialog = true
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    Spacer()
                } else {
                    List {
                        ForEach(setlistsState.setlists, id: \.id) { setlist in
                            SetlistRow(setlist: setlist,
                                     onEdit: { setlistsState.editSetlist(setlist) },
                                     onCopy: { setlistsState.copySetlist(setlist) },
                                     onDelete: { setlistsState.deleteSetlist(setlist) },
                                     onShare: { setlistsState.shareSetlist(setlist) })
                        }
                    }
                }
            }
            .navigationTitle("Setlists (\(setlistsState.setlists.count))")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("+") {
                        showingCreateDialog = true
                    }
                }
            }
            .sheet(isPresented: $showingCreateDialog) {
                CreateSetlistView { name, venue, date in
                    setlistsState.createSetlist(name: name, venue: venue, date: date)
                    showingCreateDialog = false
                }
            }
            .fullScreenCover(isPresented: $setlistsState.showingSetlistBuilder) {
                if let setlist = setlistsState.selectedSetlistForBuilder {
                    SetlistBuilderView(setlist: setlist) {
                        setlistsState.showingSetlistBuilder = false
                    }
                } else {
                    EmptyView()
                }
            }
            .onAppear {
                setlistsState.loadSetlists()
            }
        }
    }
}