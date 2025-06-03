package com.dwyer.bandbuddy.di

import org.koin.dsl.module

val appModule = module {
    includes(persistentRepositoryModule, useCaseModule)
}

// All modules for easy access
val allModules = listOf(
    persistentRepositoryModule,
    useCaseModule
)

// Basic modules (for testing)
val basicModules = listOf(
    repositoryModule,
    useCaseModule
)
