package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.Moduls.Person

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myapplication.R
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
class RedigerProfilFragment : Fragment() {


    var filePath: Uri? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var user: FirebaseUser? = null
    private var uuid: String? = ""


    private var loginViewModel: LoginViewModel  = LoginViewModel()
    private lateinit var personViewModel: PersonViewModel
    var navController: NavController? = null
    lateinit var sendtBundle: Person

    companion object {
        val REQUEST_CODE = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        user = FirebaseAuth.getInstance().currentUser
        uuid = user?.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_rediger_profil, container, false)

        sendtBundle = arguments?.getParcelable<Person>("Person")!!



        //legg inn verdiene som skal endres

        view.utfyll_navn.setText(sendtBundle.brukernavn)
        view.utfyll_alder.setText(sendtBundle.alder)
        view.utfyll_bio.setText(sendtBundle.bio)
        view.utfyll_bosted.setText(sendtBundle.bosted)

        //view.utfylling_bilde. -- BILDEADDRESSE NÅR DET ER PÅ PLASS!!!!

        // Inflate the layout for this fragment
        val viewModelFactory = ViewModelFactory(1, "")

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
            uploadFile()

            navController!!.navigateUp()
        }


    }
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun uploadFile() {

        if(filePath != null) {

            val imageRef = storageReference!!.child("images/"+uuid.toString())
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    //Toast.makeText(context, "Profil oppdatert", Toast.LENGTH_SHORT).show()
                    //navController!!.navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Kunne ikke legge inn bilde", Toast.LENGTH_SHORT).show()
                }
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null && data.data != null) {
            //utfylling_bilde.setImageURI(data?.data) // handle chosen image
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                utfylling_bilde!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}