package com.example.myapplication.fragments.nyttevent

import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Person
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.viewmodels.EventViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.event_utfyllingskjema.view.*

class Event_utfyllingsskjema: Fragment() {
    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.event_utfyllingskjema, container, false)

        //Lager en viewModel med argumenter
        val viewModelFactory = ViewModelFactory(1)

        //Sender inn viewModel
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.lag_event_button.setOnClickListener{

            //husk å Legge til lovlige felt test osv. !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            val event = Event("",
                view.event_utfyll_tittel.text.toString(),
                view.event_utfyll_Beskrivelse.text.toString(),
                "",
                view.event_utfyll_dato.text.toString(),
                view.event_utfyll_klokke.text.toString(),
                view.event_utfyll_sted.text.toString(),
                Person(
                    "PA",
                    "Maria S. Akselsen",
                    "45",
                    "@String/input",
                    "https://www.maximimages.com/stock-photo/beautiful-asian-woman-closeup-of-face-profile-MXI31426.jpg"
                ),
                view.utfylling_spinner.selectedItem.toString(),
                "0",
                "0",
                1
            )

            eventViewModel.leggTilEvent(event)
        }
    }
}