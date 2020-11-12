package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.Moduls.Person
import android.app.Activity
import android.app.AppOpsManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import com.example.myapplication.viewmodels.isLoading
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_rediger_profil.*
import kotlinx.android.synthetic.main.fragment_rediger_profil.view.*
import java.io.File
import java.io.IOException


/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Ett fragment som lar brukeren fylle ut ett skjema som oppdaterer profil info
 */

private const val FILE_NAME ="photo.jpg"

class RedigerProfilFragment : Fragment(), isLoading {

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var imageURI: Uri? = null
    private lateinit var photoFile: File


    private var loginViewModel: LoginViewModel  = LoginViewModel()
    private lateinit var personViewModel: PersonViewModel
    var navController: NavController? = null
    lateinit var sendtBundle: Person
    var progressBar: ProgressDialog? = null

    companion object {
        val REQUEST_CODE = 100
        val REQUEST_IMAGE_CAPTURE = 1
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


        //legg inn verdiene som skal oppdateres
        view.utfyll_navn.setText(sendtBundle.brukernavn)
        view.utfyll_alder.setText(sendtBundle.alder)
        view.utfyll_bio.setText(sendtBundle.bio)
        view.utfyll_bosted.setText(sendtBundle.bosted)

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_baseline_group_24)
            .error(R.drawable.ic_baseline_group_24)

        if(!sendtBundle.profilBilde.equals("")) {
            Glide.with(this@RedigerProfilFragment)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(sendtBundle.profilBilde) //hvilket bilde som skal loades
                .into(view.rProfil_utfylling_bilde) //Hvor vi ønsker å loade bildet inn i

        }

        // Inflate the layout for this fragment
        val viewModelFactory = ViewModelFactory(1, "", this@RedigerProfilFragment)

        //Sender inn viewModel
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

        //Åpne galleri
        velg_bilde_collection.setOnClickListener {
            openGalleryForImage()
        }
        //Åpne kamera
        ta_bilde_kamera.setOnClickListener {
            dispatchTakePictureIntent()
        }

        //Registrer bruker med validering
        view.button_registrer.setOnClickListener{
            if (utfyll_navn.text.toString().isEmpty()) {
                utfyll_navn.error = "Du må velge brukernavn!"
            }
            if ( ! utfyll_alder.text.toString().isDigitsOnly()) {
                utfyll_alder.error = "Du må skrive nummer!"
            }

            else if (! utfyll_navn.text.toString().isEmpty() && utfyll_alder.text.toString().isDigitsOnly()){

                val person = Person(
                    sendtBundle.personID, //genereres automatisk
                    view.utfyll_navn.text.toString(),
                    view.utfyll_alder.text.toString(),
                    view.utfyll_bosted.text.toString(),
                    view.utfyll_bio.text.toString(),
                    sendtBundle.profilBilde
                ) //LEGG TIL BILDEADRESSE HER!!
                personViewModel.leggTilPerson(person)


                if(imageURI == null){
                    loadingFinished("");
                }else{
                    personViewModel.lastOppBilde(imageURI, loginViewModel.getBruker()!!.uid)
                    progressBar!!.show()
                }


            }
        }
    }

    /**
     * Initialiserer ett åpne galleri intent
     */
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    /**
     * Initialiserer kamera intent
     */
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
        val fileProvider = FileProvider.getUriForFile(
            this.requireContext(),
            "com.example.myapplication.fileprovider",
            photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        //if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        //}
    }

    /**
     * Oppretter en fil
     * @param fileName filen som skal opprettes
     * @return returnerer en fil
     */
    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
   }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageURI = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageURI)
                rProfil_utfylling_bilde!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //val takenImage = data?.extras?.get("data") as Bitmap
            val myUri = Uri.fromFile(File(photoFile.absolutePath))
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageURI = myUri
            view?.rProfil_utfylling_bilde?.setImageBitmap(takenImage)
        }
    }

    /**
     * Når infoen er ferdig opplastet i datbasen naviger tilbake
     * @param id
     */
    override fun loadingFinished(id: String) {
        progressBar!!.dismiss()
        navController!!.navigateUp()
    }

}