package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.Moduls.Event
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.viewmodels.EventViewModel
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import com.example.myapplication.viewmodels.isLoading
import kotlinx.android.synthetic.main.event_utfyllingskjema.*
import kotlinx.android.synthetic.main.event_utfyllingskjema.event_utfyll_dato
import kotlinx.android.synthetic.main.event_utfyllingskjema.event_utfyll_klokke
import kotlinx.android.synthetic.main.event_utfyllingskjema.view.*
import java.io.File
import java.io.IOException
import java.util.*
import java.util.Calendar.HOUR_OF_DAY

/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Ett utfyllingsskjema hvor brukere kan lage ett event
 */

private const val FILE_NAME ="photo.jpg"

class Event_utfyllingsskjema: Fragment(), isLoading {



    private lateinit var eventViewModel: EventViewModel
    private val loginViewModel: LoginViewModel = LoginViewModel()
    var navController: NavController? = null
     var sendtBundle: Event? = null
    var nyEvent: Event? = null
    private lateinit var photoFile: File

    var progressBar: ProgressDialog? = null

    //Bilde henting i storage
    private var imageURI: Uri? = null

companion object {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_KODE = 438
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null)
        sendtBundle = arguments?.getParcelable<Event>("Event")!!
        else sendtBundle = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.event_utfyllingskjema, container, false)

        //Progess bar
        progressBar = ProgressDialog(context)
        progressBar!!.setMessage("Legger ut Event...")

        //Lager en viewModel med argumenter
        val viewModelFactory = ViewModelFactory(0, "", this@Event_utfyllingsskjema)

        //Sender inn viewModel
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        if(sendtBundle != null){
            view.event_utfyll_tittel.setText(sendtBundle!!.title)
            view.event_utfyll_Beskrivelse.setText(sendtBundle!!.body)
            view.event_utfyll_dato.setText(sendtBundle!!.dato)
            view.event_utfyll_klokke.setText(sendtBundle!!.klokke)
            view.event_utfyll_sted.setText(sendtBundle!!.sted)

            var spinner = view.utfylling_spinner
            var ant: Int = spinner.getCount()
            for (i in 0 until ant){
                if (spinner.getItemAtPosition(i).toString().equals(sendtBundle!!.kategori)){
                    view.utfylling_spinner.setSelection(i)
                }
            }

            if(!sendtBundle!!.image.equals("")) {
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.ic_baseline_image_24)

                Glide.with(this@Event_utfyllingsskjema)
                    .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                    .load(sendtBundle!!.image) //hvilket bilde som skal loades
                    .into(view.MineEvent_utfylling_bilde) //Hvor vi ønsker å loade bildet inn i
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

        view.button_gallery.setOnClickListener {
            velgBilde()
        }
        nyttEventKameraKnapp.setOnClickListener{
            dispatchTakePictureIntent()
        }
        event_utfyll_dato.setOnClickListener {
            event_utfyll_dato.error = null
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in textbox
                        event_utfyll_dato.text = "" + dayOfMonth + "." + monthOfYear + "." + year
                    }, year, month, day
                )
            }?.show()
        }

        event_utfyll_klokke.setOnClickListener{
            event_utfyll_klokke.text = ""
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                context,
                { timePicker, selectedHour, selectedMinute ->
                    event_utfyll_klokke.text = String.format("%02d:%02d", selectedHour, selectedMinute)
                },
                hour,
                minute,
                true
            ) //Yes 24 hour time
            mTimePicker.setTitle("Velg Klokkeslett")
            mTimePicker.show()
        }

        view.lag_event_button.setOnClickListener {
            if (event_utfyll_tittel.text.toString().isEmpty()) {
                event_utfyll_tittel.error = "Du må skrive tittel!"
            }
            if (event_utfyll_sted.text.toString().isEmpty()) {
                event_utfyll_sted.error = "Du må velge sted!"
            }
            if (event_utfyll_dato.text.toString().isEmpty()) {
                event_utfyll_dato.error = "du må velge dato!"
            }
            else if (!event_utfyll_tittel.text.toString().isEmpty() && !event_utfyll_sted.text.toString().isEmpty() && !event_utfyll_dato.text.toString().isEmpty()) {
                var eventID = ""
                var antPåmeldte = "0"
                var antKommentar= "0"
                var image = ""

                if (sendtBundle != null) {
                    eventID = sendtBundle!!.eventID
                    antPåmeldte = sendtBundle!!.antPåmeldte
                    antKommentar= sendtBundle!!.antKommentar
                    image = sendtBundle!!.image
                }

                //Henter spinner verdi dersom index ikke er 0. 0 = "velg kategori"
                var spinner = view.utfylling_spinner.selectedItem.toString()
                if(view.utfylling_spinner.selectedItemPosition == 0)
                    spinner = ""

                //Oppretter ett nytt event objekt
                nyEvent = Event(
                    eventID,
                    view.event_utfyll_tittel.text.toString(),
                    view.event_utfyll_Beskrivelse.text.toString(),
                    image,
                    view.event_utfyll_dato.text.toString(),
                    view.event_utfyll_klokke.text.toString(),
                    view.event_utfyll_sted.text.toString(),
                    loginViewModel.getBruker()!!.uid,
                    spinner,
                    antPåmeldte,
                    antKommentar,
                    1
                )

                if (nyEvent != null) {
                    if (sendtBundle == null)
                        eventViewModel.leggTilEvent(nyEvent!!, imageURI)
                    else eventViewModel.updateEvent(nyEvent!!, imageURI)
                }

                if(imageURI == null){
                        if(sendtBundle != null)
                            loadingFinished(sendtBundle!!.image)
                        else loadingFinished("")
                }else progressBar!!.show()
            }
        }
    }

    /**
     * Starter ett galleri intent
     */
    private fun velgBilde(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_KODE)
    }

    /**
     * Starter ett kamera intent
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
     * @param filename filen som skal opprettes
     * @return filen som er ferdig initialisert
     */
    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_KODE && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageURI = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageURI)
                MineEvent_utfylling_bilde!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //val takenImage = data?.extras?.get("data") as Bitmap
            val myUri = Uri.fromFile(File(photoFile.absolutePath))
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageURI = myUri
            MineEvent_utfylling_bilde.setImageBitmap(takenImage)
        }
    }

    /**
     * Når alt er ferdig lastet opp i databasen skal det navigeres til det nye eventet
     * @param id til bildet som er lastet opp
     */
    override fun loadingFinished(id: String) {
        progressBar!!.dismiss()
        nyEvent!!.image = id
        val bundle = bundleOf("Event" to nyEvent)
        navController!!.navigate(R.id.eventFragment2, bundle)
    }
}