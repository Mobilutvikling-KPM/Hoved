package com.example.myapplication.fragments.nyttevent

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.fragments.Communicator
import com.example.myapplication.midlertidig
import com.example.myapplication.viewmodels.EventViewModel
import kotlinx.android.synthetic.main.fragment_mine_event.*
import kotlinx.android.synthetic.main.fragment_nytt_event.*
import kotlinx.android.synthetic.main.fragment_nytt_event.view.*


/**
 * A simple [Fragment] subclass.
 */
class NyttEventFragment : Fragment(), OnEventItemClickListener {

    private lateinit var eventAdapter: EventRecyclerAdapter
    private lateinit var eventViewModel: EventViewModel

    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_nytt_event, container, false)

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        eventViewModel.getEvents().observe(viewLifecycleOwner, Observer {
            eventAdapter.notifyDataSetChanged()
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

        view.floating_action_button.setOnClickListener {

            navController!!.navigate(R.id.action_nyttEventFragment_to_event_utfyllingsskjema)
        }

        initRecyclerView()
       addDataSet()
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
            eventAdapter = EventRecyclerAdapter(this@NyttEventFragment)
            adapter = eventAdapter
        }

    }

    override fun onItemClick(item: Event, position: Int) {
        val bundle = bundleOf("Event" to item)
        navController!!.navigate(R.id.action_nyttEventFragment_to_eventFragment22, bundle)
    }
}