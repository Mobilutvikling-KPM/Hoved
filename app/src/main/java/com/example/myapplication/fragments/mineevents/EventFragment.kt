package com.example.myapplication.event

import RecyclerView.RecyclerView.KommentarRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.DataSourcePerson
import RecyclerView.RecyclerView.Moduls.Kommentar
import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.PersonRecyclerAdapter
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.example.myapplication.R
import com.example.myapplication.fragments.mineevents.ModelEvent
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_event.view.*
import kotlinx.android.synthetic.main.fragment_venner.*
import kotlinx.android.synthetic.main.layout_event_list_item.view.*


/**
 * Event fragment som viser ett enkelt event og dens
 */
class EventFragment : Fragment() {

    //    private lateinit var binding: FragmentEventBinding
    private lateinit var model: ModelEvent
    private lateinit var kommentarAdapter: KommentarRecyclerAdapter
    var testBeskjed: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val view = inflater.inflate(R.layout.fragment_event, container, false)

        //løsning uten databinding og modelview
        view.tittel.text = arguments?.getString("tittel")
        view.dato_og_tid.text = arguments?.getString("dato")
        view.by.text = arguments?.getString("sted")
        view.beskrivelse.text = arguments?.getString("beskrivelse")
        view.dato_og_tid.text = arguments?.getString("dato")
        view.button_se_andre_påmeldte.text = arguments?.getString("antPåmeldte") + " påmeldte"
        var bildeAdresse = arguments?.getString("image")


        //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_baseline_comment_24)

        Glide.with(this@EventFragment)
            .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
            .load(bildeAdresse) //hvilket bilde som skal loades
            .into(view.frontBilde) //Hvor vi ønsker å loade bildet inn i

        setPlaceholderGoogleMap(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        addDataSet()
    }

    //DUMMY DATA
    private fun addDataSet() {
        val data = DataSourcePerson.createDataset()
        var kommListe: ArrayList<Kommentar> = ArrayList()
        kommListe.add(
            Kommentar(
                data.get(0),
                "Dette er en dummy kommentar med random tekst, Yo let's go!")
        )
        kommListe.add(
            Kommentar(
                data.get(3),
                "Hey, ho, hey, ho, lets'a go for merry so"
            )
        )
        kommListe.add(
            Kommentar(
                data.get(2),
                "Lorem ipsum, Dummy tekst, dummy tekst" +
                        "Test. TEST!"
            )
        )
        kommentarAdapter.submitList(kommListe);
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        recycler_view_kommentar.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            kommentarAdapter = KommentarRecyclerAdapter()
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



}