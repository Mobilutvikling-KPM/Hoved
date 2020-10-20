package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Kommentar
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

class PaameldteEventFragment : Fragment(), OnEventItemClickListener {

    private var calendar: Calendar = Calendar.getInstance();
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var date: String
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
                dateFormat = SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());
//        var arr: ArrayList<Kommentar> = ArrayList()
//                    arr.add(Kommentar(
//                        "-MJ7NjgdeI26SdLz-7Me",
//                Person(
//                    "-MJfZwZdE3vbcO2qIMvA",
//                    "Henriette Pedersen",
//                    "24",
//                    "Bø",
//                    "@String/input",
//                    "https://i.pinimg.com/originals/f7/60/87/f76087d518532f3a0c6b027d18e1212a.jpg"
//                ),
//                date,
//                "Dette er en dummy kommentar med random tekst, Yo let's go!"
//            ))
//        arr.add(Kommentar(
//            "-MJ7NjgdeI26SdLz-7Me",
//                Person(
//                    "-MJf_NohvXNs5VjToRd_",
//                    "Chris Jack",
//                    "30",
//                    "Langesund",
//                    "@String/input",
//                    "https://i.pinimg.com/originals/35/d2/eb/35d2ebe20571c03d8f257ae725a780aa.jpg"
//                ),date,
//                "Hey, ho, hey, ho, lets'a go for merry so"
//            )
//        )
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
              "-MJf_NohvXNs5VjToRd_",
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
               "-MJf_XPDXcKzoTs01wnB",
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