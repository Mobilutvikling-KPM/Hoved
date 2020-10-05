package com.example.myapplication.fragments.venner

import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.DataSourcePerson
import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.PersonRecyclerAdapter
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_nytt_event.*
import kotlinx.android.synthetic.main.fragment_venner.*

/**
 * A simple [Fragment] subclass.
 */
class VennerFragment : Fragment(), PersonRecyclerAdapter.OnPersonItemClickListener {

    private lateinit var personAdapter: PersonRecyclerAdapter
    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_venner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph
        initRecyclerView()
        addDataSet()
    }

    //DUMMY DATA
    private fun addDataSet() {
         val data = DataSourcePerson.createDataset()
        personAdapter.submitList(data);
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        recycler_view_venner.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            personAdapter = PersonRecyclerAdapter(this@VennerFragment)
            adapter = personAdapter
        }

    }

    override fun onItemClick(item: Person, position: Int) {
        navController!!.navigate(R.id.action_vennerFragment2_to_besoekProfilFragment)
    }
}