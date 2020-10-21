package com.example.myapplication.fragments.venner

import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.PersonRecyclerAdapter
import RecyclerView.RecyclerView.TopSpacingItemDecoration
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

import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_venner.*
import kotlinx.android.synthetic.main.fragment_venner.view.*

/**
 * A simple [Fragment] subclass.
 */
class VennerFragment : Fragment(), PersonRecyclerAdapter.OnPersonItemClickListener {

    private lateinit var personAdapter: PersonRecyclerAdapter
    private lateinit var personViewModel: PersonViewModel

    private var loginViewModel = LoginViewModel()

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
            personAdapter.submitList(personViewModel.getPersoner().value!!)
            personAdapter.notifyDataSetChanged()
        })

        if(loginViewModel.getBruker() != null)
        personViewModel.finnVenner(loginViewModel.getBruker()!!.uid)

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
        observeAuthenticationState()
        initRecyclerView()
        addDataSet()
    }

    private fun observeAuthenticationState() {

        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            // TODO 1. Use the authenticationState variable you just added
            // in LoginViewModel and change the UI accordingly.
            when (authenticationState) {
                // TODO 2.  If the user is logged in,
                // you can customize the welcome message they see by
                // utilizing the getFactWithPersonalization() function provided
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {

                }

                else -> {
                    navController!!.navigate(R.id.loginFragment2)
                }
            }
        })
    }

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
        val bundle = bundleOf("Person" to item)
        navController!!.navigate(R.id.action_vennerFragment2_to_besoekProfilFragment, bundle)
    }
}