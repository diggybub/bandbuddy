import SwiftUI

// MARK: - Main Content View
struct ContentView: View {
    @State private var isAuthenticated = false
    
    var body: some View {
        Group {
            if isAuthenticated {
                MainView(onLogout: {
                    isAuthenticated = false
                })
            } else {
                AuthView(onAuthenticated: {
                    isAuthenticated = true
                })
            }
        }
    }
}

// MARK: - Preview
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
