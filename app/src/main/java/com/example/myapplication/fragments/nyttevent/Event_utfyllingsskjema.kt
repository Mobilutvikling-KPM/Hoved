package com.example.myapplication.fragments.nyttevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.midlertidig
import kotlinx.android.synthetic.main.event_liste.view.*
import kotlinx.android.synthetic.main.event_utfyllingskjema.view.*

class Event_utfyllingsskjema: Fragment() {


    private lateinit var skjemamidlertidig: midlertidig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.event_utfyllingskjema, container, false)

        return view
    }
}