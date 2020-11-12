package com.example.myapplication



import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.myapplication.fragments.Communicator
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.core.Context
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.event_liste.*
import java.io.InputStream

class MainActivity : AppCompatActivity(), Communicator {

    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) // Fikser at keyboard vindu går over content isteden for å pushe det opp.

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        /*
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {
            //Do somehting er i portrett
        }
*/
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
