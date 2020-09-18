package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.event.Event_liste_fragment
import com.example.myapplication.fragments.hjem.HjemFragment
import com.example.myapplication.fragments.kategori.KategoriFragment
import com.example.myapplication.fragments.mineevents.MineEventFragment
import com.example.myapplication.fragments.nyttevent.NyttEventFragment
import com.example.myapplication.fragments.profil.ProfilFragment
import com.example.myapplication.fragments.venner.VennerFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profil.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //redigerKnapp.setOnClickListener {view?.findNavController()?.navigate(R.id.action_event_liste_fragment_to_eventFragment)  }

        //val kategoriFragment = KategoriFragment()
        val eventListeFragment = Event_liste_fragment()
        val hjemFragment = HjemFragment()
        val mineEventFragment = MineEventFragment()
        val nyttEventFragment = NyttEventFragment()
        val profilFragment = ProfilFragment()
        val vennerFragment = VennerFragment()

        makeCurrentFragment(eventListeFragment)

        toolbar.setOnMenuItemClickListener() {
            when(it.itemId) {
               // R.id.kategori -> makeCurrentFragment(kategoriFragment)
            }
            true
        }

        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home -> makeCurrentFragment(eventListeFragment)
                R.id.myevents -> makeCurrentFragment(mineEventFragment)
                R.id.newevent -> makeCurrentFragment(nyttEventFragment)
                R.id.friends -> makeCurrentFragment(vennerFragment)
                R.id.profile -> makeCurrentFragment(profilFragment)
            }
            true
        }
    }
    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }
}