package com.example.myapplication.fragments.profil

import com.example.myapplication.RecyclerView.EventRecyclerAdapter
import com.example.myapplication.Moduls.Event
import com.example.myapplication.Moduls.Folg
import com.example.myapplication.Moduls.Person
import com.example.myapplication.RecyclerView.OnEventItemClickListener
import com.example.myapplication.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.view.*


/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Ett fragment som viser profilen til en bruker besøker
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

        //Sett uønskede elementer til gone
        view.bilde_profil_item.visibility = View.GONE
        view.bli_venn.visibility = View.GONE
        view.strek.visibility = View.GONE
        view.bio.visibility = View.GONE
        view.buttonLayout.visibility = View.GONE

        val viewModelFactory = ViewModelFactory(0, "", null)
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        //Finn personen vi leter etter og putt inn i view
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

            //Når ferdig lastet inn hvis elementer
            view.bilde_profil_item.visibility = View.VISIBLE
            view.bli_venn.visibility = View.VISIBLE
            view.strek.visibility = View.VISIBLE
            view.bio.visibility = View.VISIBLE

            view.profil_progress.visibility = View.GONE
            view.venner_lagdeEvent_tittel.text = "" + sendtBundle.brukernavn + "'s Eventer"

        })


        //Finn personen vi besøker
        personViewModel.søkEtterPerson(sendtBundle.personID)


        //Skjekker om personen vi besøker er en vi følger.
        personViewModel.getErBekjent().observe(viewLifecycleOwner, Observer {
            if (it) {
                view.bli_venn.text = "Slutt å følg"
                erBekjent = true
            } else {
                view.bli_venn.text = "Følg"
                erBekjent = false
            }

        })

        //Utfører databasekjekk om vi følger
        personViewModel.finnUtOmVenn(loginViewModel.getBruker()!!.uid, sendtBundle.personID)

        //Finn eventene til den som besøkes og submit i recyclerview
        eventViewModel.getLagdeEvents().observe(viewLifecycleOwner, Observer {
            if (eventViewModel.getLagdeEvents().value!!.isEmpty()) {

            }
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

        initRecyclerView()
        addDataSet()

        //Følg denne brukeren, eller slutt å følg om bekjente(Følger fra før)
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

    /**
     * Initierer og kobler recycleView til fragment
     */
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        venners_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(10)
            addItemDecoration(topSpacingDecoration)
            eventAdapter = EventRecyclerAdapter(this@BesoekProfilFragment, null)
            adapter = eventAdapter
        }

    }

    /**
     * hent dataen fra Datasource klassen og putt den inn i adapteren
     */
    private fun addDataSet() {
        if (loginViewModel.getBruker() != null)
            eventAdapter.submitList(eventViewModel.getLagdeEvents().value!!);
    }

    /**
     * Sender bruker til event som har blitt klikket på
     * @param item eventet som er trykket på
     * @param position index til det trykkede eventet
     */
    override fun onItemClick(item: Event, position: Int) {
        val bundle = bundleOf("Event" to item)
        navController!!.navigate(R.id.action_besoekProfilFragment_to_eventFragment2, bundle)
    }

}