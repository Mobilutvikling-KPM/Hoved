package com.example.myapplication.event

import RecyclerView.RecyclerView.KommentarRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Kommentar
import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.Moduls.Påmeld
import RecyclerView.RecyclerView.OnKommentarItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.content.Context
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
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


        //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_image_24)

        val requestOptionsProfil = RequestOptions()
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .error(R.drawable.ic_baseline_account_circle_24)

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

        //Skriv inn info om forfatter av event når det er blitt lastet inn
        personViewModel.getEnkeltPerson().observe(viewLifecycleOwner, Observer {

            view.brukernavn_event.text = it.brukernavn

            Glide.with(this@EventFragment)
                .applyDefaultRequestOptions(requestOptionsProfil) // putt inn requestOption
                .load(it.profilBilde) //hvilket bilde som skal loades
                .into(view.bilde_profil_event) //Hvor vi ønsker å loade bildet inn i
        })

        if(loginViewModel.getBruker() != null)
            personViewModel.hentInnloggetProfil(loginViewModel.getBruker()!!.uid,false)

        eventViewModel.getErPåmeldt().observe(viewLifecycleOwner, Observer {
            if(it){
                view.button_bliMed.setBackgroundColor(Color.WHITE)
                view.button_bliMed.setTextColor(Color.rgb(51, 0, 204))
                view.button_bliMed.text = "Meld av"
                erPåmeldt = true
            } else {
                view.button_bliMed.setBackgroundColor(Color.rgb(51, 0, 204))
                view.button_bliMed.setTextColor(Color.WHITE)
                view.button_bliMed.text = "Bli med"
                erPåmeldt = false
            }


            if(sendtBundle.forfatter == innloggetProfil?.personID) {
                //view.button_bliMed.text = ""
                view.button_bliMed.visibility = View.GONE
                //view.button_bliMed.setBackgroundColor(Color.WHITE)
            }
        })


        if(loginViewModel.getBruker() != null)
            eventViewModel.finnUtOmPåmeldt(loginViewModel.getBruker()!!.uid, sendtBundle.eventID)

        personViewModel.getInnloggetProfil().observe(viewLifecycleOwner, Observer {

            innloggetProfil = it

            Glide.with(this@EventFragment)
                .applyDefaultRequestOptions(requestOptionsProfil) // putt inn requestOption
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //recycler_view_kommentar.suppressLayout(true)
        //recycler_view_kommentar.setNestedScrollingEnabled(false);
        //placeholder logget inn person
        view.button_post_comment.setOnClickListener {
            view.hideKeyboard()
            kommentar_edit_tekst.clearFocus()
            if (loginViewModel.getBruker() != null) {
                if (!view.kommentar_edit_tekst.text.toString().equals("")) {
                    dateFormat = SimpleDateFormat("dd/MM/yyyy")
                    date = dateFormat.format(calendar.getTime())

                    kommentarViewModel.leggTilKommentar(
                        Kommentar(
                            sendtBundle.eventID,
                            innloggetProfil!!,
                            date,
                            view.kommentar_edit_tekst.text.toString()
                        )
                    )
                    eventViewModel.økAntKommentarer(sendtBundle.eventID)
                    kommentar_edit_tekst.setText("")
                } else
                    view.kommentar_edit_tekst.error = "Kommentarfeltet kan ikke være tomt"

            } else showLoginDialog("Du må logge deg på for å legge ut kommentar")
        }


        view.button_bliMed.setOnClickListener {
            if(loginViewModel.getBruker() != null){

                if(sendtBundle.forfatter != innloggetProfil?.personID){
                    if(!erPåmeldt) {

                        var tall = lagPåmeldteString(view.button_se_andre_påmeldte.text.toString().length,view.button_se_andre_påmeldte.text.toString())
                        view.button_se_andre_påmeldte.text = "" + tall + " påmeldte"

                        val påmeld = Påmeld(loginViewModel.getBruker()!!.uid, sendtBundle.eventID)

                        eventViewModel.påmeldEvent(påmeld, erPåmeldt)

                    } else {

                        var tall = lagPåmeldteString(view.button_se_andre_påmeldte.text.toString().length,view.button_se_andre_påmeldte.text.toString())
                        view.button_se_andre_påmeldte.text = "" + tall + " påmeldte"

                        eventViewModel.avsluttPåmeldt(loginViewModel.getBruker()!!.uid,sendtBundle.eventID, erPåmeldt)
                    }
                }
            } else showLoginDialog("Du må logge deg på for å bli med på ett event")
        }

        navController = Navigation.findNavController(view) //referanse til navGraph

        view.brukernavn_event.setOnClickListener {
            if(innloggetProfil != null) {

                if( sendtBundle.forfatter != innloggetProfil!!.personID) {
                    val bundle = bundleOf("Person" to personViewModel.getEnkeltPerson().value)
                    navController!!.navigate(
                        R.id.action_eventFragment2_to_besoekProfilFragment,
                        bundle
                    )
                } else navController!!.navigate(R.id.profilFragment2)
            } else showLoginDialog("Du må logge deg på for å besøke en profil")
        }

        initRecyclerView()
        addDataSet()
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showLoginDialog(handling: String) {
        AlertDialog.Builder(context)
            .setTitle(handling)
            .setMessage(R.string.dialog_box_message_login) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, which ->
                navController!!.navigate(R.id.loginFragment2)
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(R.string.dialog_box_n,null )
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
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

        // Log.i("lala","påmeldt? " + erPåmeldt)

        var reformat =
            tekst.substring(0, endOfString)
        var tall = reformat.toInt()

        if(!erPåmeldt){
            tall = tall + 1
        } else tall = tall - 1

        return tall
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        var layoutMng:LinearLayoutManager = LinearLayoutManager(context)
        layoutMng.setReverseLayout(true)
        recycler_view_kommentar.apply {
            layoutManager = layoutMng
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)

            kommentarAdapter = KommentarRecyclerAdapter(this@EventFragment)
            adapter = kommentarAdapter
        }
    }


    override fun onItemClick(item: Kommentar, position: Int) {

        //Gjør om til personID
        if(innloggetProfil != null) {
            if( item.person.personID != innloggetProfil!!.personID){
                val bundle = bundleOf("Person" to item.person)
                navController!!.navigate(R.id.action_eventFragment2_to_besoekProfilFragment, bundle)
            } else {
                navController!!.navigate(R.id.profilFragment2)
            }
        }else showLoginDialog("Du må logge deg på for å besøke profil")
    }








}