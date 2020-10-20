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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.viewmodels.EventViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_mine_eventer.*
import kotlinx.android.synthetic.main.fragment_mine_eventer.view.*
import kotlinx.android.synthetic.main.fragment_mine_eventer.view.recycler_view_nyttEvent
import kotlinx.android.synthetic.main.fragment_venner.*
import kotlinx.android.synthetic.main.fragment_venner.view.*

/**
 * A simple [Fragment] subclass.
 */
class VennerFragment : Fragment(), PersonRecyclerAdapter.OnPersonItemClickListener {

    private lateinit var personAdapter: PersonRecyclerAdapter
    private lateinit var personViewModel: PersonViewModel
    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_venner, container, false)
        //Lager en viewModel med argumenter
        val viewModelFactory = ViewModelFactory(1,"")

        //Sender inn viewModel
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)

        //Observerer endringer i event listen
        personViewModel.getPersoner().observe(viewLifecycleOwner, Observer {
            personAdapter.notifyDataSetChanged()
        })

        //observerer endring i data, og blir trigget dersom det skjer noe
        personViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
            //Show og hide progress bar if isUpdating false osv.
            view.recycler_view_venner.smoothScrollToPosition((personViewModel.getPersoner().value?.size
                ?: 0) -1)
        })
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph
        initRecyclerView()
        addDataSet()
    }

    //DUMMY DATA
    private fun addDataSet() {
        personAdapter.submitList(personViewModel.getPersoner().value!!);
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