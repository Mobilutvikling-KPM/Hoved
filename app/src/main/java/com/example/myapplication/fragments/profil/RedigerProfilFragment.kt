package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.Moduls.Event
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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.myapplication.R
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_rediger_profil.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [RedigerProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedigerProfilFragment : Fragment() {

    private var loginViewModel: LoginViewModel  = LoginViewModel()
    private lateinit var personViewModel: PersonViewModel
    var navController: NavController? = null
    lateinit var sendtBundle: Person

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_rediger_profil, container, false)
        sendtBundle = arguments?.getParcelable<Person>("Person")!!

        //legg inn verdiene som skal endres
        view.utfyll_navn.setText( sendtBundle.brukernavn)
        view.utfyll_alder.setText(sendtBundle.alder)
        view.utfyll_bio.setText(sendtBundle.bio)
        view.utfyll_bosted.setText(sendtBundle.bosted)
        //view.utfylling_bilde. -- BILDEADDRESSE NÅR DET ER PÅ PLASS!!!!

        // Inflate the layout for this fragment
        val viewModelFactory = ViewModelFactory(1,"")

        //Sender inn viewModel
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

        view.button_registrer.setOnClickListener{

            val person = Person("", //genereres automatisk
                                view.utfyll_navn.text.toString(),
                                view.utfyll_alder.text.toString(),
                                view.utfyll_bosted.text.toString(),
                                view.utfyll_bio.text.toString(),
                        "") //LEGG TIL BILDEADRESSE HER!!
            personViewModel.leggTilPerson(person, loginViewModel.getBruker()!!)

            navController!!.navigateUp()
        }
    }
}