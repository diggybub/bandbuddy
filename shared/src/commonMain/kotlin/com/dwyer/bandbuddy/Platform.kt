package com.dwyer.bandbuddy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
