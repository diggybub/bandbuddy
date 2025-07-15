@file:JvmName("KoinKt")
package com.dwyer.bandbuddy

import com.dwyer.bandbuddy.di.initKoin
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

object KoinBridge {
    @JvmStatic
    fun doInitKoin() = initKoin()
}
