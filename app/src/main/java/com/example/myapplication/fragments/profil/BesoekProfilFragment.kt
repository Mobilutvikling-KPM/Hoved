package com.example.myapplication.fragments.profil

import RecyclerView.RecyclerView.Moduls.DataCallbackSingleValue
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Person
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.viewmodels.KommentarViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_event.view.*
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [BesoekProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BesoekProfilFragment : Fragment() {

    private lateinit var personViewModel: PersonViewModel
    private lateinit var sendtBundle: Person
    private var person: Person = Person()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendtBundle = arguments?.getParcelable<Person>("Person")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profil, container, false)
        val viewModelFactory = ViewModelFactory(0)

        view.bilde_profil_item.visibility = View.GONE
        view.bli_venn.visibility = View.GONE
        view.strek.visibility = View.GONE
        view.bio.visibility = View.GONE
        personViewModel = ViewModelProvider(this, viewModelFactory).get(PersonViewModel::class.java)

        personViewModel.getEnkeltPerson().observe(viewLifecycleOwner, Observer{
            view.pnavn.text = it.brukernavn
        view.palder.text = "Alder: " + it.alder
        view.pBosted.text = "Bosted: " +it.bosted
        view.biotext.text = it.bio


            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_baseline_comment_24)

            Glide.with(this@BesoekProfilFragment)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(it.profilBilde) //hvilket bilde som skal loades
                .into(view.bilde_profil_item) //Hvor vi ønsker å loade bildet inn i

            view.bilde_profil_item.visibility = View.VISIBLE
            view.bli_venn.visibility = View.VISIBLE
            view.strek.visibility = View.VISIBLE
            view.bio.visibility = View.VISIBLE

            view.profil_progress.visibility = View.GONE

        })

        view.bli_venn.setOnClickListener {

        }

        personViewModel.søkEtterPerson(sendtBundle.personID)

        view.redigerKnapp.visibility = View.GONE
        view.slettKnapp.visibility = View.GONE
        view.LOLKNAPP.visibility = View.GONE
        // Inflate the layout for this fragment
        return view
    }

//    override fun onValueRead(verdi: Person) {
//        Log.i("lala", "PERSON ER HENTET FRA CALLBACK " + verdi.brukernavn)
//        person = verdi
//        //pnavn.text = person.brukernavn
////        view.pnavn.text = person.brukernavn
////        view.palder.text = "Alder: " + person.alder
////        view.pBosted.text = "Bosted: " + person.bosted
////        view.biotext.text = person.bio
//    }


}