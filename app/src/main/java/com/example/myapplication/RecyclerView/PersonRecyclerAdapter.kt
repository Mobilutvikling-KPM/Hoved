package com.example.myapplication.RecyclerView

import com.example.myapplication.Moduls.Person
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import kotlinx.android.synthetic.main.person_liste_item.view.*


/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * RecyclerAdapter for personer. Tilpasser dataen til recyclerview
 *
 * @property clickListener callback interface når event har blitt trykket på
 */
class PersonRecyclerAdapter(var clickListener: OnPersonItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Person> = ArrayList()

    /**
     * Lager en viewholder basert på viewType
     * @param parent
     * @param viewType viewTypen som avgjører type layout som skal brukes
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PersonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.person_liste_item, parent, false)
        )

    }

    /**
     * Binder hver enkelt holder til view
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            //bind dataen til viewholderen som er i synet
            is PersonViewHolder -> {
                holder.bind(items.get(position))
                holder.initialize(items.get(position), clickListener)
            }


        }
    }

    /**
     * Poster en liste som skal rendres i recyclerview
     * @param personListe listen som skal rendres
     */
    fun submitList(personListe: List<Person>) {
        items = personListe
    }

    /**
     * forteller hvor mange items det er i lista
     * @return recycler størrelse
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Bygger viewholder til personListen som holder på all infoen som hver enkel item skal ha.
     */
    class PersonViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val person_navn = itemView.brukernavn_item
        val person_bilde = itemView.bilde_profil_item

        /**
         * Fyller view med data
         * @param person personen som skal rendres
         */
        fun bind(person: Person) {
            person_navn.setText(person.brukernavn)

            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(person.profilBilde) //hvilket bilde som skal loades
                .into(person_bilde) //Hvor vi ønsker å loade bildet inn i
        }

        /**
         * Initilialiser person-item
         * @param item personen som skal rendres
         * @param action onClick på hver enkelt person-item
         */
        fun initialize(item: Person, action: OnPersonItemClickListener) {
            person_navn.text = item.brukernavn

            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }
        }
    }

    //Click listener på alle items
    interface OnPersonItemClickListener {
        fun onItemClick(item: Person, position: Int)
    }

}

