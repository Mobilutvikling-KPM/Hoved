package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R

import com.example.myapplication.viewmodels.LoginViewModel
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_paameldte_event.*
import kotlinx.android.synthetic.main.fragment_profil.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

class PaameldteEventFragment : Fragment(), OnEventItemClickListener {

    val loginViewModel = LoginViewModel()
    private lateinit var eventAdapter: EventRecyclerAdapter
    var navController: NavController? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_paameldte_event, container, false)
/*
        navController = findNavController()
            val user = FirebaseAuth.getInstance().currentUser

            if (loginViewModel.authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                navController!!.navigate(R.id.mineEventFragment)
            } else {
                navController!!.navigate(R.id.loginFragment2)
            }
*/
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph
        observeAuthenticationState()
        initRecyclerView()
        addDataSet()
    }

    private fun observeAuthenticationState() {

        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            // TODO 1. Use the authenticationState variable you just added
            // in LoginViewModel and change the UI accordingly.
            when (authenticationState) {
                // TODO 2.  If the user is logged in,
                // you can customize the welcome message they see by
                // utilizing the getFactWithPersonalization() function provided
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {

                    }

                else -> {
                    navController!!.navigate(R.id.loginFragment2)
                    }
                }
            })
    }

    //DUMMY DATA
    private fun addDataSet(){
       // val data = DataSource.createDataset()
        var liste:ArrayList<Event> = ArrayList()
        liste.add(
            Event(
                "C",
                "Grilling i hagen",
                "Ta med deg masse mat selv",
                "https://cnet1.cbsistatic.com/img/7KqpBDebOSEhF9ajWbSGwL8Zv-E=/420x236/2019/12/14/37b968ef-c55f-4a8b-a84d-4049cb94846d/smart-fire-5.jpg",
                "07-08-20",
                "17:00",
                "Skien",
                Person(
                    "PD",
                    "Roger Floger",
                    "27",
                    "Langesund",
                    "@String/input",
                    "https://media.gettyimages.com/photos/closeup-of-a-mans-head-profile-picture-id157192886")
                ,
                "Kos",
                "24",
                "39",
                EventRecyclerAdapter.VIEW_TYPE_ADMINLISTE
            )
        )
        liste.add(
            Event(
                "D",
                "Lan på Kroa",
                "It's dangerous to go alone. Grab a game!",
                "https://forebyggendetjenester.com/_files/200000178-a46d2a5669/700/Lambertseter%20Gaming%20Center2.png",
                "11-15-20",
                "18:00",
                "Bø",
                Person(
                    "PA",
                    "Maria S. Akselsen",
                    "45",
                    "Oslo",
                    "@String/input",
                    "https://www.maximimages.com/stock-photo/beautiful-asian-woman-closeup-of-face-profile-MXI31426.jpg"
                ),
                "Spill",
                "26",
                "4",
                EventRecyclerAdapter.VIEW_TYPE_ADMINLISTE
            ))
        eventAdapter.submitList(liste)
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView(){
        //Apply skjønner contexten selv.
        recycler_view_påmeldte.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter(this@PaameldteEventFragment)
            adapter = eventAdapter
        }

    }

    override fun onItemClick(item: Event, position: Int) {

        val bundle = bundleOf("Event" to item)
        navController!!.navigate(R.id.action_mineEventFragment_to_eventFragment22, bundle)

    }


}