package com.example.myapplication


import android.os.Bundle

import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.fragments.Communicator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Communicator {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) // Fikser at keyboard vindu går over content isteden for å pushe det opp.


        val navController: NavController = findNavController(R.id.nav_host_fragment)
        //NavigationUI.setupActionBarWithNavController(this, navController)
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
