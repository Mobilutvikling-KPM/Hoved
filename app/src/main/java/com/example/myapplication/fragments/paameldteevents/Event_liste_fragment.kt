package com.example.myapplication.event


import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.myapplication.R
import com.example.myapplication.viewmodels.EventViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.event_liste.*
import kotlinx.android.synthetic.main.event_liste.view.*


/**
 * Event fragment som viser ett enkelt event og dens
 */

class Event_liste_fragment : Fragment(), OnEventItemClickListener {


    private lateinit var eventAdapter: EventRecyclerAdapter
    var navController: NavController? = null
    private lateinit var eventViewModel: EventViewModel
    var eventListe = ArrayList<Event>()
    var filtrertListe = ArrayList<Event>() //Listen som tar vare på det filtrerte resultatet

    //SøkeVerdier
    var søkeTekst: String = ""
    var byNavn: String = ""
    var kategoriValg: String = ""
    var datoen: String = ""
    var dag = 0
    var måned = 0
    var år = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("lala","onCREATE")
//        if(this::eventAdapter.isInitialized) {
//            eventAdapter.notifyDataSetChanged()
//            Log.i("lala","onCREATE")
//        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("lala","onCreateVIew")

        val view = inflater.inflate(R.layout.event_liste, container, false)

        val viewModelFactory = ViewModelFactory(1, "", null)

        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)
        eventViewModel.getEvents().observe(viewLifecycleOwner, Observer {
            Log.i("lala","get Events SIze: " + it.size)
            if (eventListe.size == 0)
                eventListe.addAll(it)
            eventAdapter.submitList(it)
            eventAdapter.notifyDataSetChanged()
        })

        eventViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {

            if (it) {
                view.event_liste_ProgressBar.visibility = View.VISIBLE
            } else {
                view.event_liste_ProgressBar.visibility = View.GONE
            }
        })

        view.knapp_åpne_kategori.setOnClickListener{
        showFilterDialog()
        }


        view.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filtrertListe.clear()
                if (newText == null || newText.length == 0) {
                    søkeTekst = ""
                    filterSearch()
                } else {
                    var filterMønster: String = newText.toString().toLowerCase().trim()
                    søkeTekst = filterMønster
                }

                filterSearch()
                return false
            }
        })


        return view
    }

    /**
     * Åpner en dialogbox med søkevalg som kan filtrere eventlisten
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showFilterDialog() {
        val context: Context = requireContext()

       var dialog = MaterialDialog(context)
             .noAutoDismiss()
            .customView(R.layout.layout_filter)
        var dato = dialog.findViewById<DatePicker>(R.id.datePicker_kat)
        var spinner = dialog.findViewById<Spinner>(R.id.dialogspinner)
        dialog.findViewById<Spinner>(R.id.dialogspinner).setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                kategoriValg =
                    dialog.findViewById<Spinner>(R.id.dialogspinner).selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        })

        dialog.findViewById<DatePicker>(R.id.datePicker_kat).setOnDateChangedListener(object :
            DatePicker.OnDateChangedListener {
            override fun onDateChanged(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                dag = dato.dayOfMonth
                måned = dato.month
                år = dato.year

                datoen = "" + dato.dayOfMonth + "." + dato.month + "." + dato.year
            }

        })

        dialog.findViewById<EditText>(R.id.kategori_byNavn).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Log.i("lala","Jeg blir forandra!!")
            }

            override fun afterTextChanged(p0: Editable?) {
                byNavn = dialog.findViewById<TextView>(R.id.kategori_byNavn).text.toString()
            }
        }

        )

        //set verdier dersom de allerede er blitt valgt
        if(kategoriValg != "" ) {
            var spinner = dialog.findViewById<Spinner>(R.id.dialogspinner)

            var ant: Int = spinner.getCount()
            for (i in 0 until ant){
                if (spinner.getItemAtPosition(i).toString().equals(kategoriValg)){
                    dialog.findViewById<Spinner>(R.id.dialogspinner).setSelection(i)
                }
            }
        }

        if(dag != 0)
        dialog.findViewById<DatePicker>(R.id.datePicker_kat).updateDate(år, måned, dag)
        if(byNavn != "")
        dialog.findViewById<TextView>(R.id.kategori_byNavn).text = byNavn
//            .show()
        dialog.findViewById<Button>(R.id.positive_button).setOnClickListener({

            filterSearch()

            dialog.hide()
        })

        dialog.findViewById<Button>(R.id.negative_button).setOnClickListener({
            removeFilter()
            dialog.hide()
        })

        dialog.show()

    }


    private fun filterSearch(){
        filtrertListe.clear()

        var filterMønster: String = søkeTekst.toLowerCase().trim()

        for (item: Event in eventListe){

            if( item.sted.toLowerCase().contains(byNavn.toLowerCase()) && item.title.toLowerCase().contains(
                    filterMønster
                )
                && item.kategori.toLowerCase().contains(kategoriValg.toLowerCase()) && item.dato.toLowerCase().contains(
                    datoen.toLowerCase()
                )) {

                filtrertListe.add(item)
            }

        }

        eventAdapter.submitList(filtrertListe)
        eventAdapter.notifyDataSetChanged()
    }

    private fun removeFilter(){
        //SøkeVerdier
        søkeTekst= ""
        byNavn = ""
        kategoriValg = ""
        datoen = ""
        dag = 0
        måned = 0
        år = 0

        filtrertListe.clear()
        filtrertListe.addAll(eventListe)

        eventAdapter.submitList(filtrertListe)
        eventAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("lala","onVIEWCreated")
        navController = Navigation.findNavController(view) //referanse til navGraph

            initRecyclerView()
            addDataSet()
    }


    //hent dataen fra Datasource klassen og putt den inn i adapteren
    private fun addDataSet(){
       // val data = DataSource.createDataset()
       // val data = DataSource.createDataset()
        Log.i("lala","SIze: " + eventViewModel.getEvents().value!!.size)
        if(eventViewModel.getEvents().value!!.size == 0)
        eventAdapter.submitList(eventViewModel.getEvents().value!!);
        eventAdapter.notifyDataSetChanged()
    }


    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView(){
        var spanCount: Int
        //Sjekker om mobilen er i landskapsmodus eller ikke
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3
        } else {
            spanCount = 2
        }
        //Apply skjønner contexten selv.
        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            //val bottomSpacingItemDecoration = BottomSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            //addItemDecoration(bottomSpacingItemDecoration)
            eventAdapter = EventRecyclerAdapter(this@Event_liste_fragment, null)
            adapter = eventAdapter
        }
    }

    override fun onItemClick(item: Event, position: Int) {

            val bundle = bundleOf("Event" to item)
           navController!!.navigate(R.id.action_event_liste_fragment2_to_eventFragment2, bundle)

    }

}
