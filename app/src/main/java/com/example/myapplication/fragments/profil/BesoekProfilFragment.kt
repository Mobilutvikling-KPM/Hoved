package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.BottomSpacingItemDecoration
import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Folg
import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.OnEventItemClickListener
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.viewmodels.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_mine_eventer.*
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [BesoekProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BesoekProfilFragment : Fragment(), OnEventItemClickListener {

    private var loginViewModel: LoginViewModel = LoginViewModel()
    private lateinit var eventAdapter: EventRecyclerAdapter
    private lateinit var eventViewModel: EventViewModel
    private lateinit var personViewModel: PersonViewModel
    private lateinit var sendtBundle: Person
    var navController: NavController? = null
    private var person: Person = Person()
    val user = loginViewModel.getBruker()
    var erBekjent = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendtBundle = arguments?.getParcelable<Person>("Person")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        view.bilde_profil_item.visibility = View.GONE
        view.bli_venn.visibility = View.GONE
        view.strek.visibility = View.GONE
        view.bio.visibility = View.GONE
        // view.LoggUtKnapp.visibility = View.GONE
        view.buttonLayout.visibility = View.GONE

        val viewModelFactory = ViewModelFactory(0, "", null)
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        personViewModel.getEnkeltPerson().observe(viewLifecycleOwner, Observer {
            view.pnavn.text = it.brukernavn
            view.palder.text = "Alder: " + it.alder
            view.pBosted.text = "Bosted: " + it.bosted
            view.biotext.text = it.bio

            person = Person(it.personID, it.brukernavn, it.alder, it.bosted, it.bio, it.profilBilde)
            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)

            Glide.with(this@BesoekProfilFragment)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(it.profilBilde) //hvilket bilde som skal loades
                .into(view.bilde_profil_item) //Hvor vi ønsker å loade bildet inn i

            view.bilde_profil_item.visibility = View.VISIBLE
            view.bli_venn.visibility = View.VISIBLE
            view.strek.visibility = View.VISIBLE
            view.bio.visibility = View.VISIBLE

            view.profil_progress.visibility = View.GONE
            view.venner_lagdeEvent_tittel.text = "" + sendtBundle.brukernavn + "'s Eventer"

        })


        personViewModel.søkEtterPerson(sendtBundle.personID)



        personViewModel.getErBekjent().observe(viewLifecycleOwner, Observer {
            if (it) {
                view.bli_venn.text = "Slutt å følg"
                erBekjent = true
            } else {
                view.bli_venn.text = "Følg"
                erBekjent = false
            }

        })

        personViewModel.finnUtOmVenn(loginViewModel.getBruker()!!.uid, sendtBundle.personID)


        eventViewModel.getLagdeEvents().observe(viewLifecycleOwner, Observer {
            if (loginViewModel.getBruker() != null) {
                var arr: ArrayList<Event> = ArrayList()

                for (ev: Event in (eventViewModel.getLagdeEvents().value as ArrayList<Event>?)!!) {
                    ev.viewType = 3
                    arr.add(ev)
                }
                eventAdapter.submitList(arr);
            }

            eventAdapter.notifyDataSetChanged()
        })

        if (loginViewModel.getBruker() != null)
            eventViewModel.finnLagdeEvents(sendtBundle.personID)

        //  view.redigerKnapp.visibility = View.GONE
        // view.slettKnapp.visibility = View.GONE
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

        initRecyclerView()
        addDataSet()

        //Følg denne brukeren
        view.bli_venn.setOnClickListener {
            if (user != null) {
                if (!erBekjent) {
                   val folg = user?.uid?.let { it1 -> Folg(it1, person.personID) }
                    personViewModel.bliVenn(folg)

                } else {
                    personViewModel.sluttÅFølg(
                        loginViewModel.getBruker()!!.uid,
                        sendtBundle.personID
                    )

                }
            }
        }
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        venners_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(10)
            val bottomSpacingItemDecoration= BottomSpacingItemDecoration(15)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter(this@BesoekProfilFragment, null)
            adapter = eventAdapter
        }

    }

    //hent data fra viewModel og skriv dem inn i recyclerview
    private fun addDataSet() {
        if (loginViewModel.getBruker() != null)
            eventAdapter.submitList(eventViewModel.getLagdeEvents().value!!);
    }

    override fun onItemClick(item: Event, position: Int) {
        val bundle = bundleOf("Event" to item)
        navController!!.navigate(R.id.action_besoekProfilFragment_to_eventFragment2, bundle)
    }

}