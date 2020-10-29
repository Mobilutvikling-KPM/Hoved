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
 * Profil fragment som rendrer innlogget brukers profilside
 */

class ProfilFragment : Fragment() {

    private var loginViewModel: LoginViewModel = LoginViewModel()
    private lateinit var personViewModel: PersonViewModel
    val viewModelLogin = LoginViewModel()

    var navController: NavController? = null
    val bruker = viewModelLogin.getBruker()
    var filePath: Uri? = null

    private var user: FirebaseUser? = null
    private var uuid: String? = ""

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    //private var user: FirebaseUser? = null
   // private var uuid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = FirebaseAuth.getInstance().currentUser
        uuid = user?.uid
        storage = FirebaseStorage.getInstance()
        //storageReference = storage!!.reference.child("images").child(uuid!!)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_profil, container, false)


        val imageView = view.bilde_profil_item

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
                .error(R.drawable.ic_baseline_comment_24)

            Glide.with(this@ProfilFragment)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(it.profilBilde) //hvilket bilde som skal loades
                .into(view.bilde_profil_item) //Hvor vi ønsker å loade bildet inn i


            view.bilde_profil_item.visibility = View.VISIBLE
            view.strek.visibility = View.VISIBLE
            view.bio.visibility = View.VISIBLE
            view.redigerKnapp.visibility = View.VISIBLE
            view.slettKnapp.visibility = View.VISIBLE
            view.strek2.visibility = View.VISIBLE

            view.profil_progress.visibility = View.GONE
        })


        if (bruker != null) {
            personViewModel.søkEtterPerson(bruker!!.uid)
        }


        view.redigerKnapp.setOnClickListener() {
            var person: Person? = personViewModel.getEnkeltPerson().value
            val bundle = bundleOf("Person" to person)
            navController!!.navigate(R.id.action_profilFragment2_to_redigerProfilFragment, bundle)
        }

        view.slettKnapp.setOnClickListener() {
            showDeleteDialog()
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
            if (user != null) {
                FirebaseAuth.getInstance().signOut()
            }
        }


    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account.
        // If users choose to register with their email,
        // they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
            // This is where you can provide more ways for users to register and
            // sign in.
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_REQUEST_CODE
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
        if (requestCode == IMAGE_SIGN_IN_CODE) {

        }
    }

    private fun observeAuthenticationState() {




        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            // TODO 1. Use the authenticationState variable you just added
            // in LoginViewModel and change the UI accordingly.
            when (authenticationState) {
                // TODO 2.  If the user is logged in,
                // you can customize the welcome message they see by
                // utilizing the getFactWithPersonalization() function provided
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {


                }

                else -> {
                    navController!!.navigate(R.id.loginFragment2)
                }
            }
        })
    }
    /*private fun getUserProfile() {
        // [START get_user_profile]
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid

            Log.i(TAG, "Bucket: "+storageReference)

/*
            Log.i(TAG, "UserName "+name)
            Log.i(TAG, "UserEmail "+email)
            Log.i(TAG, "UserPhoto "+photoUrl)
            Log.i(TAG, "UserVerifiedEmail "+emailVerified)
            Log.i(TAG, "UserUID "+uid)
*/
        }
        // [END get_user_profile]
    }*/

    companion object {
        val SIGN_IN_REQUEST_CODE = 123
        val IMAGE_SIGN_IN_CODE = 438
    }
}
