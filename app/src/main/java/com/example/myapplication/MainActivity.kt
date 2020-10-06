package com.example.myapplication


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View

import android.os.Build

import android.view.WindowManager

import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.event.EventFragment
import com.example.myapplication.event.Event_liste_fragment
import com.example.myapplication.fragments.Communicator
import com.example.myapplication.fragments.hjem.HjemFragment
import com.example.myapplication.fragments.kategori.KategoriFragment
import com.example.myapplication.fragments.mineevents.MineEventFragment
import com.example.myapplication.fragments.nyttevent.Event_utfyllingsskjema
import com.example.myapplication.fragments.nyttevent.NyttEventFragment
import com.example.myapplication.fragments.profil.ProfilFragment
import com.example.myapplication.fragments.profil.RedigerProfilFragment
import com.example.myapplication.fragments.venner.VennerFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_traverse_app.*
import kotlinx.android.synthetic.main.event_liste.*
import java.util.prefs.AbstractPreferences

class MainActivity : AppCompatActivity(), Communicator {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) // Fikser at keyboard vindu går over content isteden for å pushe det opp.


        val navController: NavController = findNavController(R.id.nav_host_fragment)
        bottomNav.setupWithNavController(navController)
     }

    override fun sendDataKomm(tittel: String, beskrivelse: String, image: String, dato: String, sted: String, antPåmeldte: String, antKommentar: String) {
//        val bundle = Bundle()
//        bundle.putString("tittel",tittel)
//        bundle.putString("beskrivelse",beskrivelse)
//        bundle.putString("image",image)
//        bundle.putString("dato",dato)
//        bundle.putString("sted",sted)
//        bundle.putString("antPåmeldte",antPåmeldte)
//        bundle.putString("antKommentar",antKommentar)
//
//        val transaction = this.supportFragmentManager.beginTransaction()
//        val eventFragment = EventFragment()
//        eventFragment.arguments = bundle
//        transaction.replace(R.id.container,eventFragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
    }

}
