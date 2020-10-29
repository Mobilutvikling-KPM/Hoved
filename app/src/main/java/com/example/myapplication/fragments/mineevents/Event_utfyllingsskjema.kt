package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.Moduls.Event

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.event_utfyllingskjema.view.*
import kotlinx.android.synthetic.main.fragment_rediger_profil.*
import java.io.IOException

class Event_utfyllingsskjema: Fragment(), isLoading {

    private lateinit var eventViewModel: EventViewModel
    private val loginViewModel: LoginViewModel = LoginViewModel()
    var navController: NavController? = null
     var sendtBundle: Event? = null
    var nyEvent: Event? = null

    var progressBar: ProgressDialog? = null

    //Bilde henting i storage
    private var imageURI: Uri? = null
    private val requestKode = 438

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
        val viewModelFactory = ViewModelFactory(0,"",this@Event_utfyllingsskjema)

        //Sender inn viewModel
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        if(sendtBundle != null){
            view.event_utfyll_tittel.setText(sendtBundle!!.title)
            view.event_utfyll_Beskrivelse.setText(sendtBundle!!.body)
            view.event_utfyll_dato.setText(sendtBundle!!.dato)
            view.event_utfyll_klokke.setText(sendtBundle!!.klokke)
            //view.event_kategori.
            view.event_utfyll_sted.setText(sendtBundle!!.sted)


            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)

            Glide.with(this@Event_utfyllingsskjema)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(sendtBundle!!.image) //hvilket bilde som skal loades
                .into(view.utfylling_bilde) //Hvor vi ønsker å loade bildet inn i
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

        view.button_gallery.setOnClickListener {
            velgBilde()
        }

        view.lag_event_button.setOnClickListener{
            var eventID = ""

            if(sendtBundle != null)
                eventID = sendtBundle!!.eventID

            //husk å Legge til lovlige felt test osv. !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            nyEvent = Event(eventID,
                view.event_utfyll_tittel.text.toString(),
                view.event_utfyll_Beskrivelse.text.toString(),
                "",
                view.event_utfyll_dato.text.toString(),
                view.event_utfyll_klokke.text.toString(),
                view.event_utfyll_sted.text.toString(),
                loginViewModel.getBruker()!!.uid,
                view.utfylling_spinner.selectedItem.toString(),
                "0",
                "0",
                1
            )

            if(nyEvent != null) {
                if (sendtBundle == null)
                    eventViewModel.leggTilEvent(nyEvent!!, imageURI)
                else eventViewModel.updateEvent(nyEvent!!, imageURI)
            }

            progressBar!!.show()

        }
    }

    private fun velgBilde(){
//        val progressBar = ProgressDialog(context)
//        progressBar.setMessage("Bilde blir lastet opp")
//        progressBar.show()
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, requestKode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == requestKode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageURI = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageURI)
                utfylling_bilde!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun loadingFinished(id: String) {
        progressBar!!.dismiss()

        nyEvent!!.image = id
        val bundle = bundleOf("Event" to nyEvent)
        navController!!.navigate(R.id.eventFragment2, bundle)
    }
}