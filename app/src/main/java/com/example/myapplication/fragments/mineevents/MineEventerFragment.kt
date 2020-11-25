package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.OnKnappItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.marginTop
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
import kotlinx.android.synthetic.main.fragment_mine_eventer.*
import kotlinx.android.synthetic.main.fragment_mine_eventer.view.*
import kotlinx.android.synthetic.main.fragment_paameldte_event.view.*


/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Ett fragment som viser en liste over alle events som den innloggede brukeren har opprettet
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

        // Endringer for Landscape
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val params1 = view.ingeneventerTV.layoutParams as ViewGroup.MarginLayoutParams
            params1.setMargins(0, 480 ,0, 0, )
            val params2 = view.recyclerviewmineeventsbackgroundimage.layoutParams as ViewGroup.MarginLayoutParams
            params2.setMargins(0, 250 ,0, 0, )
        }

        //Lager en viewModel med argumenter
        if(loginViewModel.getBruker() != null)
        viewModelFactory = ViewModelFactory(2,loginViewModel.getBruker()!!.uid,null)
        else viewModelFactory = ViewModelFactory(2,"",null)

        //Sender inn viewModel
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        //Observerer endringer i event listen
        if(loginViewModel.getBruker() != null)
        eventViewModel.getLagdeEvents().observe(viewLifecycleOwner, Observer {
            if(loginViewModel.getBruker() != null) {
                if(eventAdapter.itemCount == 0) {
                    eventAdapter.clear(); //Sletter i tilfelle kopi
                    eventAdapter.submitList(eventViewModel.getLagdeEvents().value!!);
                    eventAdapter.notifyDataSetChanged()
                }
            }

        // Fjerner bakgrunn om det er noe i listen - ellers viser det
            if (eventViewModel.getLagdeEvents().value!!.isEmpty()) {
                ingeneventerTV.visibility = View.VISIBLE
                recyclerviewmineeventsbackgroundimage.visibility = View.VISIBLE
            }
        })

        if(loginViewModel.getBruker() != null)
        eventViewModel.finnLagdeEvents(loginViewModel.getBruker()!!.uid)

        //observerer endring i data, og blir trigget dersom det skjer noe
        eventViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
            //Show og hide progress bar if isUpdating false osv.
            if (it) {
                view.progress_bar.visibility = View.VISIBLE
                view.ingeneventerTV.visibility = View.GONE
                view.recyclerviewmineeventsbackgroundimage.visibility = View.GONE
            } else {
                view.progress_bar.visibility = View.GONE
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = Navigation.findNavController(view) //referanse til navGraph
        observeAuthenticationState()
        view.floating_action_button.setOnClickListener {
            navController!!.navigate(R.id.action_nyttEventFragment_to_event_utfyllingsskjema)
        }
        observeAuthenticationState()
        initRecyclerView()
       addDataSet()
    }

    /**
     * Observerer AuthenticationState som kommer fra firebase
     */
    private fun observeAuthenticationState() {

        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->

            when (authenticationState) {
                //om bruker er innlogget
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {

                }
                // om bruker ikke er innlogget
                else -> {
                    navController!!.navigate(R.id.loginFragment2)
                }
            }
        })
    }

    /**
     *  Dersom bruker trykker på slett knapp åpnes det ett dialog vindu med valg
     *  @param event eventet som skal slettes
     */
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


    /**
     * hent dataen fra Datasource klassen og putt den inn i adapteren
     */
    private fun addDataSet() {
        if(loginViewModel.getBruker() != null)
        eventAdapter.submitList(eventViewModel.getLagdeEvents().value!!);
    }


    /**
     * Initierer og kobler recycleView til activityMain
     */
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