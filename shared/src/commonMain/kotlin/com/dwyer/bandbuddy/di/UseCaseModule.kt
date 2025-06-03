package com.dwyer.bandbuddy.di

import com.dwyer.bandbuddy.domain.SongUseCase
import com.dwyer.bandbuddy.domain.SetlistUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { SongUseCase(get()) }
    single { SetlistUseCase(get(), get()) }
}