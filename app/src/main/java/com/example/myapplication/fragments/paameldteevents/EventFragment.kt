package com.example.myapplication.event

import RecyclerView.RecyclerView.KommentarRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Kommentar
import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.Moduls.Påmeld
import RecyclerView.RecyclerView.OnKommentarItemClickListener
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
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_event.view.*
import kotlinx.android.synthetic.main.fragment_profil.view.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Event fragment som viser ett enkelt event og dens
 */
class EventFragment : Fragment(), OnKommentarItemClickListener {

    //    private lateinit var binding: FragmentEventBinding

    private val loginViewModel: LoginViewModel = LoginViewModel()
    private lateinit var personViewModel: PersonViewModel
    private lateinit var kommentarAdapter: KommentarRecyclerAdapter
    private lateinit var kommentarViewModel: KommentarViewModel
    private var eventViewModel: EventViewModel = EventViewModel(1,"",null)
   // private lateinit var eventViewModel: EventViewModel
    lateinit var sendtBundle: Event
    var navController: NavController? = null
    var innloggetProfil: Person? = null
    var erPåmeldt = false

    //kalender
    private var calendar: Calendar = Calendar.getInstance();
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendtBundle = arguments?.getParcelable<Event>("Event")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


//        //DataBinding i ett fragment -- SKIFT TIL DATABINDING SENERE
//        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_event, container, false)
//
//        binding.modelEvent = model
//
//        binding.apply {
//            model?.tittel = "Dette er test nr2"
//            invalidateAll()
//        }
        //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_image_24)

        val view = inflater.inflate(R.layout.fragment_event, container, false)

        //Lager en viewModel med argumenter
        val viewModelFactory = ViewModelFactory(0, sendtBundle.eventID,null)

        //Sender inn viewModel
        kommentarViewModel =
            ViewModelProvider(this, viewModelFactory).get(KommentarViewModel::class.java)
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)
       // eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        //Observerer endringer i event listen
        kommentarViewModel.getKommentarer().observe(viewLifecycleOwner, Observer {
            kommentarAdapter.submitList(kommentarViewModel.getKommentarer().value!!)
            kommentarAdapter.notifyDataSetChanged()
        })

        //observerer endring i data, og blir trigget dersom det skjer noe
        kommentarViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {

            //Show og hide progress bar if isUpdating false osv.
//            view.recycler_view_kommentar.smoothScrollToPosition((kommentarViewModel.getKommentarer().value?.size
//                ?: 0) -1)
        })

        //observerer endring i data, og blir trigget dersom det skjer noe
//        kommentarViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
//            //Show og hide progress bar if isUpdating false osv.
//            view.recycler_view_nyttEvent.smoothScrollToPosition((kommentarViewModel.getKommentarer().value?.size
//                ?: 0) -1)
//        })

//        eventViewModel.getEnkeltEvent().observe(viewLifecycleOwner, Observer {
//            kommentarAdapter.notifyDataSetChanged()
//        })


        //Skriv inn info om forfatter av event når det er blitt lastet inn
        personViewModel.getEnkeltPerson().observe(viewLifecycleOwner, Observer {

            view.brukernavn_event.text = it.brukernavn

            Glide.with(this@EventFragment)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(it.profilBilde) //hvilket bilde som skal loades
                .into(view.bilde_profil_event) //Hvor vi ønsker å loade bildet inn i
        })

        personViewModel.hentInnloggetProfil(loginViewModel.getBruker()!!.uid,false)

        eventViewModel.getErPåmeldt().observe(viewLifecycleOwner, Observer {
            if(it){
                view.button_bliMed.text = "Meld av"
                erPåmeldt = true
            } else {
                view.button_bliMed.text = "Bli med"
                erPåmeldt = false
            }

        })

        eventViewModel.finnUtOmPåmeldt(loginViewModel.getBruker()!!.uid, sendtBundle.eventID)

        personViewModel.getInnloggetProfil().observe(viewLifecycleOwner, Observer {

            innloggetProfil = it

            Glide.with(this@EventFragment)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(it.profilBilde) //hvilket bilde som skal loades
                .into(view.kommentar_profil_bilde) //Hvor vi ønsker å loade bildet inn i
        })

        personViewModel.søkEtterPerson(sendtBundle.forfatter)

        //løsning uten databinding og modelview
        view.tittel.text = sendtBundle.title
        view.dato_og_tid.text = sendtBundle.dato
        view.klokkeSlett.text = " klokken " + sendtBundle.klokke
        view.by.text = sendtBundle.sted
        view.beskrivelse.text = sendtBundle.body
        view.event_kategori.text = sendtBundle.kategori
        view.button_se_andre_påmeldte.text = sendtBundle.antPåmeldte + " påmeldte"
        var bildeAdresse = sendtBundle.image


        Glide.with(this@EventFragment)
            .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
            .load(bildeAdresse) //hvilket bilde som skal loades
            .into(view.frontBilde) //Hvor vi ønsker å loade bildet inn i

        setPlaceholderGoogleMap(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //placeholder logget inn person
        view.button_post_comment.setOnClickListener {

            if (loginViewModel.getBruker() != null) {
                dateFormat = SimpleDateFormat("dd/MM/yyyy")
                date = dateFormat.format(calendar.getTime())

//                var arr = sendtBundle.kommentarListe
//                arr.add(
//                    Kommentar(
//                        innloggetProfil!!,
//                        date,
//                        view.kommentar_edit_tekst.text.toString()
//                    )
//                )
                //eventViewModel.leggTilKommentar(sendtBundle.eventID, arr)

                kommentarViewModel.leggTilKommentar(
                    Kommentar(
                        sendtBundle.eventID,
                        innloggetProfil!!,
                                date,
                        view.kommentar_edit_tekst.text.toString()
                    )
                )
            }

        }

        view.button_bliMed.setOnClickListener {
            if(loginViewModel.getBruker() != null){

                if(!erPåmeldt) {
//                    val event = Event(
//                        sendtBundle.eventID,
//                        sendtBundle.title,
//                        sendtBundle.body,
//                        sendtBundle.image,
//                        sendtBundle.dato,
//                        sendtBundle.klokke,
//                        sendtBundle.sted,
//                        sendtBundle.forfatter,
//                        sendtBundle.kategori,
//                        sendtBundle.antPåmeldte,
//                        sendtBundle.antKommentar,
//                        1
//                    )

                    val påmeld = Påmeld(loginViewModel.getBruker()!!.uid, sendtBundle.eventID)
                    eventViewModel.påmeldEvent(påmeld, erPåmeldt)

                    var tall = lagPåmeldteString(view.button_se_andre_påmeldte.text.toString().length,view.button_se_andre_påmeldte.text.toString())
                    view.button_se_andre_påmeldte.text = "" + tall + " påmeldte"
                } else {


                    eventViewModel.avsluttPåmeldt(loginViewModel.getBruker()!!.uid,sendtBundle.eventID, erPåmeldt)
                    var tall = lagPåmeldteString(view.button_se_andre_påmeldte.text.toString().length,view.button_se_andre_påmeldte.text.toString())
                    view.button_se_andre_påmeldte.text = "" + tall + " påmeldte"
                }

            }
        }

        navController = Navigation.findNavController(view) //referanse til navGraph

        view.brukernavn_event.setOnClickListener {
            val bundle = bundleOf("Person" to personViewModel.getEnkeltPerson().value)
            navController!!.navigate(R.id.action_eventFragment2_to_besoekProfilFragment, bundle)
        }

        initRecyclerView()
        addDataSet()
    }


    private fun addDataSet() {
//        var arr: ArrayList<Person> =  kommentarViewModel.getKommentarPerson().value!!
//        for(person: Person in arr)
//        Log.i("lala","addDataSet " + person.brukernavn )
       kommentarAdapter.submitList(kommentarViewModel.getKommentarer().value!!);
    }

    private fun lagPåmeldteString(length: Int, tekst:String ): Int{
        var endOfString = 1
        if (length > 10)
            endOfString = 2

        var reformat =
            tekst.substring(0, endOfString)
        var tall = reformat.toInt()

        Log.i("lala","Tallet inn stringformat: " + tall)
        if(!erPåmeldt){
            tall = tall - 1
        } else tall = tall + 1

        return tall
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        recycler_view_kommentar.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            kommentarAdapter = KommentarRecyclerAdapter(this@EventFragment)
            adapter = kommentarAdapter
        }
    }

    //PLACEHOLDER GOOGLE MAPS IMAGE
    fun setPlaceholderGoogleMap(view: View) {
        //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_baseline_comment_24)

        Glide.with(this@EventFragment)
            .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
            .load("https://leafletjs.com/examples/quick-start/thumbnail.png") //hvilket bilde som skal loades
            .into(view.google_placeholder_image) //Hvor vi ønsker å loade bildet inn i
    }

    override fun onItemClick(item: Kommentar, position: Int) {

        //Gjør om til personID
        val bundle = bundleOf("Person" to item.person)
        navController!!.navigate(R.id.action_eventFragment2_to_besoekProfilFragment, bundle)

    }








}