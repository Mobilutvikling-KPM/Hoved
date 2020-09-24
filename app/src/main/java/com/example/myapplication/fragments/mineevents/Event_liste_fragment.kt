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
import com.example.myapplication.R

import android.util.Log

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.myapplication.R
import com.example.myapplication.fragments.Communicator
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
    private lateinit var communicator: Communicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.event_liste, container, false)

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

        initRecyclerView()
        addDataSet()
    }
      //  navController = Navigation.findNavController(view)

    //hent dataen fra Datasource klassen og putt den inn i adapteren
    private fun addDataSet(){
        val data = DataSource.createDataset()
        eventAdapter.submitList(data);
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
        communicator = activity as Communicator
        communicator.sendDataKomm(item.title, item.body,item.image,item.dato, item.sted, item.antPåmeldte, item.antKommentar)
        //En intent som åpner en ny aktivitet og sender med data fra gjenstanden som er valgt
//        val intent = Intent(activity, TraverseAppActivity::class.java)
//        intent.putExtra("TITTEL", item.title)
//        startActivity(intent)


    }

}