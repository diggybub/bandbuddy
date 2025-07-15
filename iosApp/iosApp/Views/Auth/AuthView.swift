import SwiftUI

// MARK: - Authentication View
struct AuthView: View {
    @StateObject private var authState = AuthState()
    @State private var isSignUp = false
    @State private var email = ""
    @State private var password = ""
    @State private var displayName = ""
    @State private var bandName = ""
    
    let onAuthenticated: () -> Void
    
    var body: some View {
        VStack(spacing: 20) {
            Text(isSignUp ? "Sign Up" : "Log In")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            VStack(spacing: 12) {
                TextField("Email", text: $email)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .autocapitalization(.none)
                
                SecureField("Password", text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                
                if isSignUp {
                    TextField("Display Name", text: $displayName)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    TextField("Band Name (optional)", text: $bandName)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                }
            }
            
            if !authState.statusMessage.isEmpty {
                Text(authState.statusMessage)
                    .foregroundColor(.red)
                    .font(.caption)
            }
            
            Button(action: {
                if isSignUp {
                    authState.signUp(email: email, password: password, displayName: displayName, bandName: bandName.isEmpty ? nil : bandName)
                } else {
                    authState.signIn(email: email, password: password)
                }
            }) {
                Text(isSignUp ? "Sign Up" : "Log In")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .disabled(authState.loading || email.isEmpty || password.isEmpty || (isSignUp && displayName.isEmpty))
            
            Button(action: {
                isSignUp.toggle()
                authState.clearStatus()
            }) {
                Text(isSignUp ? "Already have an account? Log In" : "No account? Sign Up")
            }
        }
        .padding(32)
        .onChange(of: authState.isAuthenticated) { authenticated in
            if authenticated {
                onAuthenticated()
            }
        }
    }
}