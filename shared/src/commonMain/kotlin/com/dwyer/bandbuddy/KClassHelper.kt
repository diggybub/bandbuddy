package com.dwyer.bandbuddy

import com.dwyer.bandbuddy.ui.AuthViewModel
import com.dwyer.bandbuddy.ui.ProfileViewModel
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * Helper object to expose KClass references for Swift interop with Koin DI
 */
object KClassHelper {
    @JvmStatic
    fun getAuthViewModelKClass(): KClass<AuthViewModel> = AuthViewModel::class

    @JvmStatic
    fun getProfileViewModelKClass(): KClass<ProfileViewModel> = ProfileViewModel::class
}