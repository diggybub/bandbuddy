import Foundation
import shared

// MARK: - Dependency Container
struct DependencyContainer {
    
    // MARK: - Koin Instance
    static let koin = KoinBridge().doInitKoin().koin
    
    // MARK: - Shared Repositories
    static let authRepository = InMemoryAuthRepository()
    static let bandRepository = InMemoryBandRepository(authRepository: authRepository)
    static let songRepository: SongRepository = InMemorySongRepository()
    static let setlistRepository: SetlistRepository = InMemorySetlistRepository()
    static let setlistItemRepository: SetlistItemRepository = InMemorySetlistItemRepository()
    
    // MARK: - Use Cases
    static let songUseCase = SongUseCase(songRepository: songRepository)
    static let setlistUseCase = SetlistUseCase(
        setlistRepository: setlistRepository, 
        setlistItemRepository: setlistItemRepository
    )
}