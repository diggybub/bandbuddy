import SwiftUI

// MARK: - Profile View
struct ProfileView: View {
    @ObservedObject var profileState: ProfileState
    let onLogout: () -> Void
    
    var body: some View {
        VStack(spacing: 24) {
            Text("Profile")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            VStack(spacing: 16) {
                HStack {
                    VStack(alignment: .leading) {
                        Text("Name")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Text(profileState.userName)
                            .font(.body)
                    }
                    Spacer()
                }
                
                HStack {
                    VStack(alignment: .leading) {
                        Text("Band")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Text(profileState.bandName)
                            .font(.body)
                    }
                    Spacer()
                }
            }
            
            Spacer()
            
            Button(action: {
                profileState.signOut()
                onLogout()
            }) {
                Text("Log Out")
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.red)
                    .cornerRadius(8)
            }
        }
        .padding(32)
        .onAppear {
            profileState.loadProfile()
        }
    }
}