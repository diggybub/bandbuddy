import SwiftUI

@main
struct iOSApp: App {
    init() {
        KoinHelper.start()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}