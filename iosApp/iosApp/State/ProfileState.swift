import Foundation
import SwiftUI
import shared

// MARK: - Profile State
@MainActor
class ProfileState: ObservableObject {
    @Published var userName = "Loading..."
    @Published var bandName = "No band"

    private let authViewModel: AuthViewModel = AuthViewModel(
        auth: DependencyContainer.authRepository, 
        band: DependencyContainer.bandRepository
    )
    private let profileViewModel: ProfileViewModel = ProfileViewModel(
        auth: DependencyContainer.authRepository, 
        band: DependencyContainer.bandRepository
    )

    func loadProfile() {
        let user = authViewModel.currentUser.value
        if let userObj = user as? User { 
            userName = userObj.displayName
        } else {
            userName = "Unknown"
        }
        Task {
            do {
                try await profileViewModel.refreshBandData()
                await MainActor.run {
                    bandName = "Band Info"
                }
            } catch {
                print("Error loading band data: \(error)")
            }
        }
    }

    func signOut() {
        Task {
            try await authViewModel.signOut()
        }
    }
}