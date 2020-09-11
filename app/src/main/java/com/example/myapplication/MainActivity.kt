package com.example.myapplication

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.DataSource
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.AdapterListUpdateCallback


class MainActivity : AppCompatActivity() {

private lateinit var eventAdapter: EventRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        addDataSet()
    }

    //hent dataen fra Datasource klassen og putt den inn i adapteren
    private fun addDataSet(){
        val data = DataSource.createDataset()
        eventAdapter.submitList(data);
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView(){
        //Apply skjønner contexten selv.
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter()
            adapter = eventAdapter

        }

    }

}