package com.example.myapplication.RecyclerView


import com.example.myapplication.Moduls.Kommentar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import kotlinx.android.synthetic.main.comment_liste_item.view.*



/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * RecyclerAdapter for kommentarer. Tilpasser dataen til recyclerview
 *
 * @property clickListener callback interface når event har blitt trykket på
 */
class KommentarRecyclerAdapter(var clickListener: OnKommentarItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Kommentar> = ArrayList()

    /**
     * Lager en viewholder basert på viewType
     * @param parent
     * @param viewType viewTypen som avgjører type layout som skal brukes
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return KommentarViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_liste_item, parent, false)
        )

    }

    /**
     * Binder hver enkelt holder til view
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            //bind dataen til viewholderen som er i synet
            is KommentarViewHolder -> {
                holder.bind(items.get(position))
                holder.initialize(items.get(position),clickListener)

            }

        }
    }

    /**
     * Poster en liste som skal rendres i recyclerview
     * @param kommentarListe listen som skal rendres
     */
    fun submitList(kommentarListe: List<Kommentar>) {
        items = kommentarListe
    }

    /**
     * forteller hvor mange items det er i lista
     * @return recycler størrelse
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Bygger viewholder til kommentarlisten som holder på all infoen som hver enkel item skal ha.
     */
    class KommentarViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val person_navn = itemView.brukernavn_kommentar
        val kommentar_Tekst = itemView.kommentar_tekst
        val person_bilde = itemView.bilde_profil_kommentar
        val dato = itemView.kommentar_dato

        /**
         * Fyller view med data
         * @param kommentar kommentaren som skal rendres
         */
        fun bind(kommentar: Kommentar) {
            person_navn.setText(kommentar.person.brukernavn)
            kommentar_Tekst.setText(kommentar.kommentarTekst)
            dato.setText(kommentar.date)

            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)


            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(kommentar.person.profilBilde) //hvilket bilde som skal loades
                .into(person_bilde) //Hvor vi ønsker å loade bildet inn i
        }

        /**
         * Initilialiser kommentar-item
         * @param item kommentar som skal rendres
         * @param action onClick på hver enkelt event
         */
        fun initialize(item: Kommentar, action: OnKommentarItemClickListener) {
            person_navn.text = item.person.brukernavn

            itemView.brukernavn_kommentar.setOnClickListener{
                action.onItemClick(item, adapterPosition)
            }


        }
    }


}

//Click listener på alle items
interface OnKommentarItemClickListener{
    fun onItemClick(item: Kommentar, position: Int)
}