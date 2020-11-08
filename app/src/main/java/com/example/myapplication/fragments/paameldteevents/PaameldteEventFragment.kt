package com.example.myapplication.fragments.paameldteevents

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.OnKnappItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.viewmodels.EventViewModel

import com.example.myapplication.viewmodels.LoginViewModel

import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.event_liste.view.*
import kotlinx.android.synthetic.main.fragment_paameldte_event.*
import kotlinx.android.synthetic.main.fragment_paameldte_event.view.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

class PaameldteEventFragment : Fragment(), OnEventItemClickListener, OnKnappItemClickListener {

    val loginViewModel = LoginViewModel()
    private lateinit var eventViewModel :EventViewModel
    private lateinit var eventAdapter: EventRecyclerAdapter
    var navController: NavController? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_paameldte_event, container, false)

        val viewModelFactory = ViewModelFactory(0, "",null)
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)


            eventViewModel.getPåmeldteEvents().observe(viewLifecycleOwner, Observer {
                eventAdapter.submitList(eventViewModel.getPåmeldteEvents().value!!)
                eventAdapter.notifyDataSetChanged()
            })

            if(loginViewModel.getBruker() != null) {
            eventViewModel.finnPåmeldteEvents(0, loginViewModel.getBruker()!!.uid)
        }

        eventViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {

            if(it) {
                view.påmeldt_liste_ProgressBar.visibility = View.VISIBLE
            } else{  view.påmeldt_liste_ProgressBar.visibility = View.GONE}
        })
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


    private fun addDataSet(){

        if(loginViewModel.getBruker() != null)
        eventAdapter.submitList(eventViewModel.getPåmeldteEvents().value!!)
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView(){
        //Apply skjønner contexten selv.
        recycler_view_påmeldte.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter(this@PaameldteEventFragment,this@PaameldteEventFragment)
            adapter = eventAdapter
        }

    }

    override fun onItemClick(item: Event, position: Int) {

        val bundle = bundleOf("Event" to item)
        navController!!.navigate(R.id.action_mineEventFragment_to_eventFragment22, bundle)

    }

    override fun onSlettClick(item: Event, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onRedigerClick(item: Event, position: Int) {
        TODO("Not yet implemented")
    }


}