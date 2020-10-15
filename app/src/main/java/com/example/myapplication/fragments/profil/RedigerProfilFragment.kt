package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.Moduls.Person
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.myapplication.R
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_rediger_profil.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [RedigerProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedigerProfilFragment : Fragment() {

    private lateinit var personViewModel: PersonViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewModelFactory = ViewModelFactory(1)

        //Sender inn viewModel
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)

        return inflater.inflate(R.layout.fragment_rediger_profil, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.button_registrer.setOnClickListener{

            val person = Person("",
                                view.utfyll_navn.text.toString(),
                                view.utfyll_alder.text.toString(),
                                view.utfyll_bosted.toString(),
                                view.utfyll_bio.text.toString(),
                        "")
            personViewModel.leggTilPerson(person)
        }
    }
}