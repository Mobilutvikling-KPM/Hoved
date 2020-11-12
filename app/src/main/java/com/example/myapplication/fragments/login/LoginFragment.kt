package com.example.myapplication.fragments.login

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.fragments.profil.ProfilFragment
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_profil.*


/**
 *
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Hjelpe-Kilde: https://developer.android.com/codelabs/advanced-android-kotlin-training-login#0
 * Ett fragment som viser en liste med alle events i databasen. Kan filtrere søket
 */
class LoginFragment : Fragment() {

    val viewModelLogin = LoginViewModel()
    var navController: NavController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
        navController = Navigation.findNavController(view) //referanse til navGraph
    }

    /**
     * Start logg-inn fra firebase
     */
    private fun launchSignInFlow() {

        // Innloggingsmuligheter :
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()

        )

        // Lager aktiviteten
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            ProfilFragment.SIGN_IN_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Bruker har logget inn suksessfult
                val bruker = FirebaseAuth.getInstance().currentUser!!
                val personViewModel: PersonViewModel = PersonViewModel(1,bruker.uid,null)
                personViewModel.opprettBruker(bruker)
                Log.i(ContentValues.TAG, "Bruker har suksessfult logget inn ${FirebaseAuth.getInstance().currentUser?.displayName}!")
            } else {
                //Innlogging feilet
                Log.i(ContentValues.TAG, "Innlogging feiliet ${response?.error?.errorCode}")
            }
        }
    }

    /**
     * Observerer AuthenticationState som kommer fra firebase
     */
    private fun observeAuthenticationState() {

        viewModelLogin.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                //Om bruker er logget inn
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                        navController!!.popBackStack(R.id.event_liste_fragment2, true)
                        navController!!.navigate(R.id.event_liste_fragment2)
                    }

                else -> {
                    // Om bruker ikke er logget inn
                    login_knapp.setOnClickListener {
                        launchSignInFlow()
                    }
                }
            }
        })
    }

    companion object {
        val SIGN_IN_REQUEST_CODE = 123
    }

}