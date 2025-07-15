package com.dwyer.bandbuddy

import com.dwyer.bandbuddy.data.AuthRepository
import com.dwyer.bandbuddy.data.BandRepository

suspend fun authBandPlayground(auth: AuthRepository, band: BandRepository) {
    println("--- Begin Auth/Band Playground ---")
    // 1. Sign up a user
    val signup = auth.signUp("alice@email.com", "password", "Alice")
    val user = signup.getOrNull()
    println("Signup: $signup")

    // 2. Log in as Alice
    val login = auth.signIn("alice@email.com", "password")
    println("Login: $login")

    // 3. Alice creates a band
    if (user != null) {
        val bandCreate = band.createBand("Funky Band", user.id)
        println("Band Created: $bandCreate")
        val bandId = bandCreate.getOrNull()?.id

        // 4. Alice invites Bob
        if (bandId != null) {
            val inviteBob = band.inviteToBand(bandId, "bob@email.com", user.id)
            println("Invite Sent: $inviteBob")
        }
    }

    // 5. Bob signs up
    val bobSignup = auth.signUp("bob@email.com", "securepass", "Bob")
    val bob = bobSignup.getOrNull()
    println("Bob Signup: $bobSignup")

    // 6. Bob checks invites and accepts
    if (bob != null) {
        val bobInvites = band.getUserInvites(bob.email)
        println("Bob's Invites: $bobInvites")
        if (bobInvites.isNotEmpty()) {
            val accept = band.respondToInvite(bobInvites[0].id, true)
            println("Bob Accepts Invite: $accept")
            // Bob is now in the band
            val members = band.getBandMembers(bobInvites[0].bandId)
            println("Band Members: $members")
        }
    }
    println("--- End Auth/Band Playground ---")
}
