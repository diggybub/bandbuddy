import Foundation
import SwiftUI
import shared

// MARK: - Authentication State
@MainActor
class AuthState: ObservableObject {
    @Published var isAuthenticated = false
    @Published var loading = false
    @Published var statusMessage = ""

    private let authViewModel: AuthViewModel = AuthViewModel(
        auth: DependencyContainer.authRepository, 
        band: DependencyContainer.bandRepository
    )

    func signIn(email: String, password: String) {
        loading = true
        statusMessage = ""
        Task {
            do {
                try await authViewModel.signIn(email: email, password: password)
                let user = authViewModel.currentUser.value
                await MainActor.run {
                    if user != nil {
                        isAuthenticated = true
                    } else {
                        statusMessage = "Login failed"
                    }
                    loading = false
                }
            } catch {
                await MainActor.run {
                    statusMessage = "Login error: \(error.localizedDescription)"
                    loading = false
                }
            }
        }
    }

    func signUp(email: String, password: String, displayName: String, bandName: String?) {
        loading = true
        statusMessage = ""
        Task {
            do {
                try await authViewModel.signUp(email: email, password: password, displayName: displayName, bandName: bandName)
                let user = authViewModel.currentUser.value
                await MainActor.run {
                    if user != nil {
                        isAuthenticated = true
                    } else {
                        statusMessage = "Signup failed"
                    }
                    loading = false
                }
            } catch {
                await MainActor.run {
                    statusMessage = "Signup error: \(error.localizedDescription)"
                    loading = false
                }
            }
        }
    }

    func clearStatus() {
        statusMessage = ""
    }
}