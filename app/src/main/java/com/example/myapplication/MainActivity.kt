package com.example.myapplication

import RecyclerView.RecyclerView.EventRecyclerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.fragments.hjem.HjemFragment
import com.example.myapplication.fragments.mineevents.MineEventFragment
import com.example.myapplication.fragments.nyttevent.NyttEventFragment
import com.example.myapplication.fragments.profil.ProfilFragment
import com.example.myapplication.fragments.venner.VennerFragment
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private lateinit var eventAdapter: EventRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hjemFragment = HjemFragment()
        val mineEventFragment = MineEventFragment()
        val nyttEventFragment = NyttEventFragment()
        val profilFragment = ProfilFragment()
        val vennerFragment = VennerFragment()

        makeCurrentFragment(hjemFragment)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home -> makeCurrentFragment(hjemFragment)
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