package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.OnKnappItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.fragment_mine_eventer.*
import kotlinx.android.synthetic.main.fragment_mine_eventer.view.*


/**
 * A simple [Fragment] subclass.
 */
class MineEventerFragment : Fragment(), OnEventItemClickListener, OnKnappItemClickListener {

    private lateinit var eventAdapter: EventRecyclerAdapter
    private lateinit var eventViewModel: EventViewModel

    private var loginViewModel:LoginViewModel = LoginViewModel()
    private lateinit var viewModelFactory: ViewModelFactory

    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mine_eventer, container, false)

        //Lager en viewModel med argumenter
        if(loginViewModel.getBruker() != null)
        viewModelFactory = ViewModelFactory(2,loginViewModel.getBruker()!!.uid)
        else viewModelFactory = ViewModelFactory(2,"")

        //Sender inn viewModel
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        //Observerer endringer i event listen
        if(loginViewModel.getBruker() != null)
        eventViewModel.getLagdeEvents().observe(viewLifecycleOwner, Observer {
            if(loginViewModel.getBruker() != null)
                eventAdapter.submitList(eventViewModel.getLagdeEvents().value!!);
            eventAdapter.notifyDataSetChanged()
        })

        if(loginViewModel.getBruker() != null)
        eventViewModel.finnLagdeEvents(loginViewModel.getBruker()!!.uid)

        //observerer endring i data, og blir trigget dersom det skjer noe
        eventViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
            //Show og hide progress bar if isUpdating false osv.
//            view.recycler_view_nyttEvent.smoothScrollToPosition((eventViewModel.getEvents().value?.size
//                ?: 0) -1)
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAuthenticationState()
        navController = Navigation.findNavController(view) //referanse til navGraph
        observeAuthenticationState()

        view.floating_action_button.setOnClickListener {

            navController!!.navigate(R.id.action_nyttEventFragment_to_event_utfyllingsskjema)
        }
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


    private fun showDeleteDialog(event: Event) {
        AlertDialog.Builder(context)
            .setTitle("Slett Event")
            .setMessage("Er du sikker på at du vil slette dette eventet?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, which ->
                eventViewModel.slettEvent(event)
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no,null )
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }


    //hent data fra viewModel og skriv dem inn i recyclerview
    private fun addDataSet() {
        if(loginViewModel.getBruker() != null)
        eventAdapter.submitList(eventViewModel.getLagdeEvents().value!!);
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        recycler_view_nyttEvent.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter(this@MineEventerFragment,this@MineEventerFragment)
            adapter = eventAdapter
        }

    }

    override fun onItemClick(item: Event, position: Int) {
        val bundle = bundleOf("Event" to item)
        navController!!.navigate(R.id.action_nyttEventFragment_to_eventFragment22, bundle)
    }


    override fun onSlettClick(item: Event, position: Int) {
        showDeleteDialog(item)
    }

    override fun onRedigerClick(item: Event, position: Int) {
        val bundle = bundleOf("Event" to item)
        navController!!.navigate(R.id.event_utfyllingsskjema, bundle)
    }
}