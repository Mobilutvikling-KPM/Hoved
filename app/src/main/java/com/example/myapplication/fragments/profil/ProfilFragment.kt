package com.example.myapplication.fragments.profil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_profil.view.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ProfilFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profil, container, false)
        view.redigerKnapp.setOnClickListener(){

        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}