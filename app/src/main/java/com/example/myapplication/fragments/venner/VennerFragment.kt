package com.example.myapplication.fragments.venner

import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.PersonRecyclerAdapter
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.size
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R

import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.event_liste.view.*
import kotlinx.android.synthetic.main.fragment_profil.view.*
import kotlinx.android.synthetic.main.fragment_venner.*
import kotlinx.android.synthetic.main.fragment_venner.view.*

/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Ett fragment som viser en liste med alle personer som innlogget bruker følger
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
        val viewModelFactory = ViewModelFactory(1,"",null)

        view.ingenvennerTV.visibility = View.GONE
        view.recyclerviewfriendsbackgroundimage.visibility = View.GONE

        //Sender inn viewModel
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)

        //Observerer endringer i event listen
        personViewModel.getPersoner().observe(viewLifecycleOwner, Observer {
            personAdapter.submitList(personViewModel.getPersoner().value!!)
            personAdapter.notifyDataSetChanged()
            if (personViewModel.getPersoner().value!!.isNotEmpty()) {
                ingenvennerTV.visibility = View.GONE
                recyclerviewfriendsbackgroundimage.visibility = View.GONE
            } else {
                ingenvennerTV.visibility = View.VISIBLE
                recyclerviewfriendsbackgroundimage.visibility = View.VISIBLE
            }
        })

        //Hent person som bruker følger
        if(loginViewModel.getBruker() != null)
        personViewModel.finnVenner(loginViewModel.getBruker()!!.uid)

        //observerer updating, og gjør noe når listen er ferdig lastet inn
        personViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
            //Show og hide progress bar if isUpdating false osv.
            if(it) {
                view.venner_liste_ProgressBar.visibility = View.VISIBLE
                view.ingenvennerTV.visibility = View.GONE
                view.recyclerviewfriendsbackgroundimage.visibility = View.GONE
            }
            else {  view.venner_liste_ProgressBar.visibility = View.GONE
                    view.ingenvennerTV.visibility = View.VISIBLE
                    view.recyclerviewfriendsbackgroundimage.visibility = View.VISIBLE
            }
        })
        // Inflate the layout for this fragment
        return view

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
        navController = Navigation.findNavController(view) //referanse til navGraph
        observeAuthenticationState()
        initRecyclerView()
        addDataSet()
    }

    /**
     * Observerer AuthenticationState som kommer fra firebase
     */
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

    /**
     * hent dataen fra Datasource klassen og putt den inn i adapteren
     */
    private fun addDataSet() {
        personAdapter.submitList(personViewModel.getPersoner().value!!);
    }

    /**
     * Initierer og kobler recycleView til activityMain
     */
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

    /**
     * Sender bruker til eventet som har blitt klikket på
     * @param item person som har blitt trykket på
     * @param position index til den personen
     */
    override fun onItemClick(item: Person, position: Int) {
        val bundle = bundleOf("Person" to item)
        navController!!.navigate(R.id.action_vennerFragment2_to_besoekProfilFragment, bundle)
    }
}