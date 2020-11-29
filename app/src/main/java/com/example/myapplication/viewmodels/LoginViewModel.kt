package com.example.myapplication.viewmodels

import com.example.myapplication.Moduls.Person
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

import com.example.myapplication.fragments.Firebase.FirebaseUserLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 * ViewModel for login. Kommuniserer med firebase authentication
 */
class LoginViewModel : ViewModel() {

    val profil: Person = Person()

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED

        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    /**
     * Henter bruker som er innlogget
     * @return innlogget bruker
     */
    fun getBruker(): FirebaseUser?{
        return FirebaseAuth.getInstance().currentUser
    }

}