package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.Moduls.Event

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myapplication.R
import com.example.myapplication.viewmodels.EventViewModel
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.event_utfyllingskjema.view.*
import kotlinx.android.synthetic.main.fragment_event.view.*

class Event_utfyllingsskjema: Fragment() {
    private lateinit var eventViewModel: EventViewModel
    private val loginViewModel: LoginViewModel = LoginViewModel()
    var navController: NavController? = null
     var sendtBundle: Event? = null

    //Bilde henting i storage
    private var imageURI: Uri? = null
    private val requestKode = 438
   // private var mStorageRef: StorageReference? = null

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

        //Lager en viewModel med argumenter
        val viewModelFactory = ViewModelFactory(0,"")

        //Sender inn viewModel
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        if(sendtBundle != null){
            view.event_utfyll_tittel.setText(sendtBundle!!.title)
            view.event_utfyll_Beskrivelse.setText(sendtBundle!!.body)
            view.event_utfyll_dato.setText(sendtBundle!!.dato)
            view.event_utfyll_klokke.setText(sendtBundle!!.klokke)
            //view.event_kategori.
            view.event_utfyll_sted.setText(sendtBundle!!.sted)
           // view.utfylling_bilde.setImageResource(sendtBundle!!.image) BILDEADRESSE
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
            val event = Event(eventID,
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

            if(sendtBundle == null)
            eventViewModel.leggTilEvent(event, imageURI)
            else eventViewModel.updateEvent(event)

            val bundle = bundleOf("Event" to event)
            navController!!.navigate(R.id.eventFragment2, bundle)
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

        //      progressBar.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == requestKode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageURI = data.data
            Toast.makeText(context,"Opplaster...",Toast.LENGTH_SHORT).show()
        }
    }
}