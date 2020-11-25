package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.Moduls.Person
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_profil.view.*

/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Ett fragment som viser profilen til den innloggede brukeren
 */

class ProfilFragment : Fragment() {

    private var loginViewModel: LoginViewModel = LoginViewModel()
    private lateinit var personViewModel: PersonViewModel
    val viewModelLogin = LoginViewModel()

    var navController: NavController? = null
    val bruker = viewModelLogin.getBruker()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        //Skjul elementer frem til det er funnet data.
        view.bilde_profil_item.visibility = View.GONE
        view.bli_venn.visibility = View.GONE
        view.strek.visibility = View.GONE
        view.strek2.visibility = View.GONE
        view.bio.visibility = View.GONE
        view.bli_venn.visibility = View.GONE

        //view.redigerKnapp.visibility = View.GONE
        //view.slettKnapp.visibility = View.GONE

        val viewModelFactory = ViewModelFactory(1, "",null)
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)

        //når data er funnet for innlogget bruker
        personViewModel.getEnkeltPerson().observe(viewLifecycleOwner, Observer {
            view.pnavn.text = it.brukernavn
            view.palder.text = "Alder: " + it.alder
            view.pBosted.text = "Bosted: " + it.bosted
            view.biotext.text = it.bio

            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)

            Glide.with(this@ProfilFragment)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(it.profilBilde) //hvilket bilde som skal loades
                .into(view.bilde_profil_item) //Hvor vi ønsker å loade bildet inn i


            //Når bruker er lastet inn hvis alle elementer
            view.bilde_profil_item.visibility = View.VISIBLE
            view.strek.visibility = View.VISIBLE
            view.bio.visibility = View.VISIBLE
            view.redigerKnapp.visibility = View.VISIBLE
            view.strek2.visibility = View.VISIBLE

            view.profil_progress.visibility = View.GONE
        })

        //Søk etter bruker info dersom logget in
        if (bruker != null) {
            personViewModel.søkEtterPerson(bruker!!.uid)
        }

        //Naviger til rediger bruker fragment
        view.redigerKnapp.setOnClickListener() {
            var person: Person? = personViewModel.getEnkeltPerson().value
            val bundle = bundleOf("Person" to person)
            navController!!.navigate(R.id.action_profilFragment2_to_redigerProfilFragment, bundle)
        }
        return view
    }


    private fun showDeleteDialog() {
        AlertDialog.Builder(context)
            .setTitle("Slett bruker")
            .setMessage("Er du sikker på at du vil slette denne brukeren?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, which ->
                // Continue with delete operation
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view) //referanse til navGraph

        observeAuthenticationState()

        view.LoggUtKnapp.setOnClickListener {
            if (bruker != null) {
                FirebaseAuth.getInstance().signOut()
            }
        }


    }

    /**
     * Observerer AuthenticationState som kommer fra firebase om bruker ikke er logget inn send til loggin
     */
    private fun observeAuthenticationState() {
        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->

            when (authenticationState) {

                LoginViewModel.AuthenticationState.AUTHENTICATED -> {


                }
                //Dersom bruker ikke er logget inn
                else -> {
                    navController!!.navigate(R.id.loginFragment2)
                }
            }
        })
    }

    companion object {
        val SIGN_IN_REQUEST_CODE = 123
        val IMAGE_SIGN_IN_CODE = 438
    }
}
