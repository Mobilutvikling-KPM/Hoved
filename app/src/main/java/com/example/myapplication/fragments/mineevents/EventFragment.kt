package com.example.myapplication.event

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEventBinding
import com.example.myapplication.fragments.mineevents.ModelEvent
import kotlinx.android.synthetic.main.fragment_event.view.*
import kotlinx.android.synthetic.main.layout_event_list_item.view.*


/**
 * Event fragment som viser ett enkelt event og dens
 */
class EventFragment : Fragment() {

//    private lateinit var binding: FragmentEventBinding
    private lateinit var model: ModelEvent
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
        var bildeAdresse= arguments?.getString("image")

        //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_baseline_comment_24)

        Glide.with(this@EventFragment)
            .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
            .load(bildeAdresse) //hvilket bilde som skal loades
            .into(view.frontBilde) //Hvor vi ønsker å loade bildet inn i

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }




}