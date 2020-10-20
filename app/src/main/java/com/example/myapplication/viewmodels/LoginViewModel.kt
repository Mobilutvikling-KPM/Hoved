package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.Person
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

import com.example.myapplication.fragments.Firebase.FirebaseUserLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {

    val profil: Person = Person()

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
//            Log.i("lala", "Bruker er logget inn " + user.uid)
//            innloggetBruker = user
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun getBruker(): FirebaseUser?{

        return FirebaseAuth.getInstance().currentUser
    }
}