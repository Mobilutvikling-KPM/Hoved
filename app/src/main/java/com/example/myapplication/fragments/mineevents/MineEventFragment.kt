package com.example.myapplication.fragments.mineevents

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.DataSource
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.fragments.Communicator
import kotlinx.android.synthetic.main.fragment_mine_event.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MineEventFragment : Fragment(), OnEventItemClickListener {

    private lateinit var eventAdapter: EventRecyclerAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initRecyclerView()
        addDataSet()
    }

    //DUMMY DATA
    private fun addDataSet(){
       // val data = DataSource.createDataset()
        var liste:ArrayList<Event> = ArrayList()
        liste.add(
            Event("SøppelPlukking på stranda!",
                "Bli med da vel!!",
                "https://image.forskning.no/1209602.jpg?imageId=1209602&x=0&y=0&cropw=100&croph=85.903083700441&width=342&height=196",
                "18-10-20",
                "Porsgrunn",
                "10",
                "3",
            EventRecyclerAdapter.VIEW_TYPE_ADMINLISTE)
        )
        liste.add(Event(
            "It treff!",
            "Vi skal snakke om java og php",
            "https://c2.thejournal.ie/media/2015/08/computer-nerd-2-390x285.jpg",
            "21-09-19",
            "Bø",
            "15",
            "7",
            EventRecyclerAdapter.VIEW_TYPE_ADMINLISTE
        ))
        liste.add(Event(
            "Lan på Kroa",
            "It's dangerous to go alone. Grab a game!",
            "https://forebyggendetjenester.com/_files/200000178-a46d2a5669/700/Lambertseter%20Gaming%20Center2.png",
            "11-15-20",
            "Bø",
            "26",
            "4",
            EventRecyclerAdapter.VIEW_TYPE_ADMINLISTE
        ))
        eventAdapter.submitList(liste);
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView(){
        //Apply skjønner contexten selv.
        recycler_view_påmeldte.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter(this@MineEventFragment)
            adapter = eventAdapter
        }

    }

    override fun onItemClick(item: Event, position: Int) {
        //communicator = activity as Communicator
       // communicator.sendDataKomm(item.title, item.body,item.image,item.dato, item.sted, item.antPåmeldte, item.antKommentar)
        //En intent som åpner en ny aktivitet og sender med data fra gjenstanden som er valgt
//        val intent = Intent(activity, TraverseAppActivity::class.java)
//        intent.putExtra("TITTEL", item.title)
//        startActivity(intent)


    }

}