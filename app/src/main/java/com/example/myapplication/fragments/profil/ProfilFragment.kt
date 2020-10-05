package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.Moduls.Event
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_profil.view.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ProfilFragment : Fragment() {

    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_profil, container, false)
        view.redigerKnapp.setOnClickListener() {
            navController!!.navigate(R.id.action_profilFragment2_to_redigerProfilFragment)

        }
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

    }
    /*
    fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.container, redigerProfilFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }*/
}