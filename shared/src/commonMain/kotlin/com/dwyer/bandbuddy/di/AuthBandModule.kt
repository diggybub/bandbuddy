package com.dwyer.bandbuddy.di

import com.dwyer.bandbuddy.data.AuthRepository
import com.dwyer.bandbuddy.data.BandRepository
import com.dwyer.bandbuddy.data.InMemoryAuthRepository
import com.dwyer.bandbuddy.data.InMemoryBandRepository
import com.dwyer.bandbuddy.ui.AuthViewModel
import com.dwyer.bandbuddy.ui.ProfileViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val authBandModule: Module = module {
    single<InMemoryAuthRepository> { InMemoryAuthRepository() }
    single<AuthRepository> { get<InMemoryAuthRepository>() }
    single<BandRepository> { InMemoryBandRepository(get<InMemoryAuthRepository>()) }

    // ViewModels
    factory { AuthViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get()) }
}
