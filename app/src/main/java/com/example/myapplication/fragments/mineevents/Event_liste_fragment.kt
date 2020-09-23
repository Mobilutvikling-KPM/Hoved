package com.example.myapplication.event

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.DataSource
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.fragments.Communicator
import kotlinx.android.synthetic.main.event_liste.*

/**
 * Event fragment som viser ett enkelt event og dens
 */

class Event_liste_fragment : Fragment(), OnEventItemClickListener {

    private lateinit var eventAdapter: EventRecyclerAdapter
    private lateinit var communicator: Communicator
    private lateinit var katKnapp: MenuItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.event_liste, container, false)
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