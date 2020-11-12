package com.example.myapplication.event


import RecyclerView.RecyclerView.EventRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.OnEventItemClickListener
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
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
import kotlinx.android.synthetic.main.event_utfyllingskjema.*
import java.util.*
import kotlin.collections.ArrayList


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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.event_liste, container, false)

        val viewModelFactory = ViewModelFactory(1, "", null)

        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)
        eventViewModel.getEvents().observe(viewLifecycleOwner, Observer {
            if (eventListe.size == 0)
                eventListe.addAll(it)
            eventAdapter.notifyDataSetChanged()
        })

        eventViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {

            if (it) {
                view.event_liste_ProgressBar.visibility = View.VISIBLE
            } else {
                view.event_liste_ProgressBar.visibility = View.GONE
            }
        })

        view.knapp_åpne_kategori.setOnClickListener {
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
    private fun showFilterDialog() {
        val context: Context = requireContext()

        var dialog = MaterialDialog(context)
            .noAutoDismiss()
            .customView(R.layout.layout_filter_kategori)
        var dato = dialog.findViewById<TextView>(R.id.kategori_dato)
        var spinner = dialog.findViewById<Spinner>(R.id.kategori_spinner)

        dialog.findViewById<Spinner>(R.id.kategori_spinner).setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(spinner.selectedItemPosition != 0)
                kategoriValg =
                    dialog.findViewById<Spinner>(R.id.kategori_spinner).selectedItem.toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        dialog.findViewById<TextView>(R.id.kategori_dato).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Log.i("lala","Jeg blir forandra!!")
            }
            override fun afterTextChanged(p0: Editable?) {
                datoen = dialog.findViewById<TextView>(R.id.kategori_dato).text.toString()
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
                byNavn = dialog.findViewById<EditText>(R.id.kategori_byNavn).text.toString()
            }
        })
        dato.setOnClickListener {
            dato.error = null
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            context.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in textbox
                        dato.text = "" + dayOfMonth + "." + monthOfYear + "." + year
                    }, year, month, day
                )
            }.show()
        }

        //set verdier dersom de allerede er blitt valgt
        if(kategoriValg != "") {
            var spinner = dialog.findViewById<Spinner>(R.id.kategori_spinner)

            var ant: Int = spinner.getCount()
            for (i in 0 until ant){
                if (spinner.getItemAtPosition(i).toString().equals(kategoriValg)){
                    dialog.findViewById<Spinner>(R.id.kategori_spinner).setSelection(i)
                }
            }
        }

        if(datoen != "")
        dialog.findViewById<TextView>(R.id.kategori_dato).text = datoen
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

        filtrertListe.clear()
        filtrertListe.addAll(eventListe)

        eventAdapter.submitList(filtrertListe)
        eventAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph
        initRecyclerView()
        addDataSet()
    }


    //hent dataen fra Datasource klassen og putt den inn i adapteren
    private fun addDataSet(){
       // val data = DataSource.createDataset()
       // val data = DataSource.createDataset()

        eventAdapter.submitList(eventViewModel.getEvents().value!!);
    }


    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView(){
        //Apply skjønner contexten selv.
        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
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
