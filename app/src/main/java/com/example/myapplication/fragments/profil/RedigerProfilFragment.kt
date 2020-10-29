package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.Moduls.Person

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myapplication.R
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import com.example.myapplication.viewmodels.isLoading
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_rediger_profil.view.*
import kotlinx.android.synthetic.main.fragment_rediger_profil.*
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [RedigerProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedigerProfilFragment : Fragment(), isLoading {


//    var filePath: Uri? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
//    private var user: FirebaseUser? = null
//    private var uuid: String? = ""
    private var imageURI: Uri? = null


    private var loginViewModel: LoginViewModel  = LoginViewModel()
    private lateinit var personViewModel: PersonViewModel
    var navController: NavController? = null
    lateinit var sendtBundle: Person
    var progressBar: ProgressDialog? = null




    companion object {
        val REQUEST_CODE = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_rediger_profil, container, false)

        sendtBundle = arguments?.getParcelable<Person>("Person")!!
        progressBar = ProgressDialog(context)
        progressBar!!.setMessage("Oppdaterer profil...")


        //legg inn verdiene som skal endres

        view.utfyll_navn.setText(sendtBundle.brukernavn)
        view.utfyll_alder.setText(sendtBundle.alder)
        view.utfyll_bio.setText(sendtBundle.bio)
        view.utfyll_bosted.setText(sendtBundle.bosted)

        //view.utfylling_bilde. -- BILDEADDRESSE NÅR DET ER PÅ PLASS!!!!

        // Inflate the layout for this fragment
        val viewModelFactory = ViewModelFactory(1, "",this@RedigerProfilFragment)

        //Sender inn viewModel
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)


        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

        velg_bilde_collection.setOnClickListener {
            openGalleryForImage()
        }




        view.button_registrer.setOnClickListener{
        Log.i("lala", "REgistrer profil blir trykket på")

            val person = Person(
                sendtBundle.personID, //genereres automatisk
                view.utfyll_navn.text.toString(),
                view.utfyll_alder.text.toString(),
                view.utfyll_bosted.text.toString(),
                view.utfyll_bio.text.toString(),
                ""
            ) //LEGG TIL BILDEADRESSE HER!!
            personViewModel.leggTilPerson(person)
            personViewModel.lastOppBilde(imageURI, loginViewModel.getBruker()!!.uid)

            progressBar!!.show()


        }


    }
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null && data.data != null) {
            imageURI = data.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageURI)
                utfylling_bilde!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun loadingFinished(id:String) {
        progressBar!!.dismiss()
        navController!!.navigateUp()
    }

}