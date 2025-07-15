import Foundation
import shared

@objc class KoinHelper: NSObject {
    @objc static func start() {
        KoinBridge().doInitKoin()
    }
}
