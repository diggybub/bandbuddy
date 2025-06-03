package com.dwyer.bandbuddy.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(allModules)
}

// For testing or when you need to restart Koin
fun resetKoin() {
    stopKoin()
}