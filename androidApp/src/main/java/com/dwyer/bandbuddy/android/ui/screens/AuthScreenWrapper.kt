package com.dwyer.bandbuddy.android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.dwyer.bandbuddy.ui.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@Composable
fun AuthScreenWrapper(
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val koin = getKoin()
    val authViewModel: AuthViewModel = koin.get()

    val currentUser by authViewModel.currentUser.collectAsState()
    val status by authViewModel.status.collectAsState()
    val loading by authViewModel.loading.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    if (currentUser != null) {
        onAuthenticated()
        return
    }

    AuthScreen(
        statusMessage = status,
        loading = loading,
        onLogin = { email, password ->
            coroutineScope.launch {
                authViewModel.signIn(email, password)
            }
        },
        onSignUp = { email, password, displayName, bandName ->
            coroutineScope.launch {
                authViewModel.signUp(email, password, displayName, bandName)
            }
        },
        modifier = modifier
    )
}
