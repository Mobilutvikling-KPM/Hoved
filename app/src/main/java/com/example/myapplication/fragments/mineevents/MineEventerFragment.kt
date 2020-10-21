package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
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
import kotlinx.android.synthetic.main.fragment_mine_eventer.*
import kotlinx.android.synthetic.main.fragment_mine_eventer.view.*


/**
 * A simple [Fragment] subclass.
 */
class MineEventerFragment : Fragment(), OnEventItemClickListener {


    private lateinit var eventAdapter: EventRecyclerAdapter
    private lateinit var eventViewModel: EventViewModel

    val loginViewModel = LoginViewModel()
    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mine_eventer, container, false)

        //Lager en viewModel med argumenter
        val viewModelFactory = ViewModelFactory(2,"")

        //Sender inn viewModel
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        //Observerer endringer i event listen
        eventViewModel.getEvents().observe(viewLifecycleOwner, Observer {
            eventAdapter.notifyDataSetChanged()
        })

        //observerer endring i data, og blir trigget dersom det skjer noe
//        eventViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
//            //Show og hide progress bar if isUpdating false osv.
//            view.recycler_view_nyttEvent.smoothScrollToPosition((eventViewModel.getEvents().value?.size
//                ?: 0) -1)
//        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = Navigation.findNavController(view) //referanse til navGraph

        view.floating_action_button.setOnClickListener {
            navController!!.navigate(R.id.action_nyttEventFragment_to_event_utfyllingsskjema)
        }
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

    //hent data fra viewModel og skriv dem inn i recyclerview
    private fun addDataSet() {
        eventAdapter.submitList(eventViewModel.getEvents().value!!);
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        recycler_view_nyttEvent.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter(this@MineEventerFragment)
            adapter = eventAdapter
        }

    }

    override fun onItemClick(item: Event, position: Int) {
        val bundle = bundleOf("Event" to item)
        navController!!.navigate(R.id.action_nyttEventFragment_to_eventFragment22, bundle)
    }
}