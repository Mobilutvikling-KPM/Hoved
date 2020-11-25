package com.example.myapplication.fragments.paameldteevents

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.OnKnappItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.content.res.Configuration
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
import kotlinx.android.synthetic.main.fragment_paameldte_event.*
import kotlinx.android.synthetic.main.fragment_paameldte_event.view.*


/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Ett fragment som viser en liste med alle eventer som bruker har meldt seg på
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
        // Inflate layout for denne fragmenten
        val view = inflater.inflate(R.layout.fragment_paameldte_event, container, false)

        // Endringer for Landscape
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val params1 = view.ingenpaameldteeventerTV.layoutParams as ViewGroup.MarginLayoutParams
            params1.setMargins(0, 480 ,0, 0, )
            val params2 = view.recyclerviewpåmeldteeventsbackgroundimage.layoutParams as ViewGroup.MarginLayoutParams
            params2.setMargins(0, 230 ,0, 0, )
        }

        val viewModelFactory = ViewModelFactory(0, "",null)
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        //observerer listen over påmeldte eventsbs
            eventViewModel.getPåmeldteEvents().observe(viewLifecycleOwner, Observer {

                if(eventAdapter.itemCount == 0) {
                    eventAdapter.clear();
                    eventAdapter.submitList(eventViewModel.getPåmeldteEvents().value!!)
                    eventAdapter.notifyDataSetChanged()
                }

                if (eventViewModel.getPåmeldteEvents().value!!.isEmpty()) {
                        view.ingenpaameldteeventerTV.visibility = View.VISIBLE
                        view.recyclerviewpåmeldteeventsbackgroundimage.visibility = View.VISIBLE
                    }
            })

            if(loginViewModel.getBruker() != null) {
            eventViewModel.finnPåmeldteEvents(0, loginViewModel.getBruker()!!.uid)
        }

        //Endre  elementer basert på om det lastes inn eller ikke
        eventViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
            if(it) {
                view.påmeldt_liste_ProgressBar.visibility = View.VISIBLE
                view.ingenpaameldteeventerTV.visibility = View.GONE
                view.recyclerviewpåmeldteeventsbackgroundimage.visibility = View.GONE
            } else {
                view.påmeldt_liste_ProgressBar.visibility = View.GONE
            }
        })

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
        navController = Navigation.findNavController(view) //referanse til navGraph
        observeAuthenticationState()
        initRecyclerView()
        addDataSet()


    }


    /**
     * Observerer AuthenticationState som kommer fra firebase om bruker ikke er logget inn send til loggin
     */
    private fun observeAuthenticationState() {

        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->

            when (authenticationState) {

                LoginViewModel.AuthenticationState.AUTHENTICATED -> {

                    }
                // Om bruker ikke er logget inn
                else -> {
                    navController!!.navigate(R.id.loginFragment2)
                    }
                }
            })
    }

    /**
     * hent dataen fra Datasource klassen og putt den inn i adapteren
     */
    private fun addDataSet(){

        if(loginViewModel.getBruker() != null)
        eventAdapter.submitList(eventViewModel.getPåmeldteEvents().value!!)
    }

    /**
     * Initierer og kobler recycleView til fragmentet
     */
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
    }

    override fun onRedigerClick(item: Event, position: Int) {
    }


}