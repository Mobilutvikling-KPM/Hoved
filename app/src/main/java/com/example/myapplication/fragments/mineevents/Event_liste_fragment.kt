package com.example.myapplication.event

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.DataSource
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup


import android.util.Log
import androidx.core.os.bundleOf

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.myapplication.R
import com.example.myapplication.fragments.Communicator
import com.example.myapplication.viewmodels.EventViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import kotlinx.android.synthetic.main.event_liste.*
import kotlinx.android.synthetic.main.event_liste.view.*
import java.util.prefs.AbstractPreferences

/**
 * Event fragment som viser ett enkelt event og dens
 */

class Event_liste_fragment : Fragment(), OnEventItemClickListener {


    private lateinit var eventAdapter: EventRecyclerAdapter
    var navController: NavController? = null
    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.event_liste, container, false)

        val viewModelFactory = ViewModelFactory(1)

        eventViewModel = ViewModelProvider(this,viewModelFactory).get(EventViewModel::class.java)
        eventViewModel.getEvents().observe(viewLifecycleOwner, Observer {
            eventAdapter.notifyDataSetChanged()
        })

        eventViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
            Log.i("lala", "UPDATING!")
            if(it) {
                view.event_liste_ProgressBar.visibility = View.VISIBLE
            } else{  view.event_liste_ProgressBar.visibility = View.GONE}
        })

        view.knapp_åpne_kategori.setOnClickListener{
        showFilterDialog()
        }

        return view
    }

    private fun showFilterDialog() {
        val context: Context = requireContext()
        val dialog = MaterialDialog(context)
            .noAutoDismiss()
            .customView(R.layout.layout_filter)
        dialog.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph
        initRecyclerView()
        addDataSet()
    }


    //hent dataen fra Datasource klassen og putt den inn i adapteren
    private fun addDataSet(){
       // val data = DataSource.createDataset()

        eventAdapter.submitList(eventViewModel.getEvents().value!!);
    }


    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView(){
        //Apply skjønner contexten selv.
        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter(this@Event_liste_fragment)
            adapter = eventAdapter
        }
    }

    override fun onItemClick(item: Event, position: Int) {

            val bundle = bundleOf("Event" to item)
           navController!!.navigate(R.id.action_event_liste_fragment2_to_eventFragment2, bundle)

    }

}