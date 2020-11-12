package RecyclerView.RecyclerView

import RecyclerView.RecyclerView.Moduls.Event
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import kotlinx.android.synthetic.main.administrer_event_liste_item.view.*
import kotlinx.android.synthetic.main.layout_event_list_item.view.*

/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * RecyclerAdapter for eventer. Tilpasser dataen til recyclerview
 *
 * @property clickListener callback interface når event har blitt trykket på
 * @property knappClickListener callback interface når bruker trykker på rediger eller slett event knapp
 */
class EventRecyclerAdapter(var clickListener: OnEventItemClickListener, var knappClickListener: OnKnappItemClickListener?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        const val VIEW_TYPE_HOVEDLISTE = 1
        const val VIEW_TYPE_ADMINLISTE = 2
    }

    private var items: ArrayList<Event> = ArrayList()

    /**
     * Lager en viewholder basert på viewType
     * @param parent
     * @param viewType viewTypen som avgjører type layout som skal brukes
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == VIEW_TYPE_HOVEDLISTE) {
            return EventViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_event_list_item, parent, false)
            )
        } else if(viewType == VIEW_TYPE_ADMINLISTE){
            return EventAdminViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.administrer_event_liste_item, parent, false))
        }else{
            return EventPåmeldViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.administrer2_event_liste_item, parent, false))
        }
    }

    /**
     * Binder hver enkelt holder til view
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            //bind dataen til viewholderen som er i synet
            is EventViewHolder ->{
                holder.bind(items.get(position))
                holder.initialize(items.get(position),clickListener)
            }

            is EventAdminViewHolder ->{
                holder.bind(items.get(position))
                holder.initialize(items.get(position),clickListener, knappClickListener)
        }
            is EventPåmeldViewHolder ->{
                holder.bind(items.get(position))
                holder.initialize(items.get(position),clickListener)
            }
        }
    }

    /**
     * Poster en liste som skal rendres i recyclerview
     * @param eventListe listen som skal rendres
     */
    fun submitList(eventListe: List<Event>){
        items = eventListe as ArrayList<Event>
    }


    /**
     * forteller hvor mange items det er i lista
     * @return recycler størrelse
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * gir viewType til en spesefik item
     * @return returnrer viewType
     */
    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }


    /**
     * Bygger viewholder til hovedlisten som holder på all infoen som hver enkel item skal ha.
     */
    class EventViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val event_image = itemView.event_image
        val event_title = itemView.event_title
        val event_tid = itemView.event_tid
        val event_sted = itemView.event_sted
        val event_antPåmeldt = itemView.ant_påmeldte_tall
        val event_anKommentar = itemView.antall_kommentar_tall

        /**
         * Fyller view med data
         * @param event eventet som skal rendres
         */
        fun bind(event: Event){
            event_title.setText(event.title)
            event_tid.setText(event.dato)
            event_sted.setText(", " + event.sted)
            event_antPåmeldt.setText(event.antPåmeldte)
            event_anKommentar.setText(event.antKommentar)

            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(event.image) //hvilket bilde som skal loades
                .into(event_image) //Hvor vi ønsker å loade bildet inn i
        }


        /**
         * Initilialiser event-item
        * @param item eventet som skal rendres
        * @param action onClick på hver enkelt event
        */
        fun initialize(item: Event, action:OnEventItemClickListener){
            event_title.text = item.title
            event_sted.text = item.sted
            event_tid.text = item.dato
            event_antPåmeldt.text = item.antPåmeldte
            event_anKommentar.text = item.antKommentar

            itemView.setOnClickListener{
                action.onItemClick(item, adapterPosition)
            }
        }
    }

    /**
     *  En view holder som skriver ut info mine eventer listen
     */
    class EventAdminViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val event_image = itemView.bilde_admin_liste
        val event_title = itemView.tittel_admin_liste
        val event_tid = itemView.dato_admin_liste
        val slett_knapp = itemView.button_delete
        val rediger_knapp = itemView.button_rediger

        val event_antPåmeldt = itemView.ant_påmeldte_tall_admin_liste
        val event_anKommentar = itemView.antall_kommentar_tall_admin_liste

        /**
         * Fyller view med data
         * @param event eventet som skal rendres
         */
        fun bind(event: Event){
            event_title.setText(event.title)
            event_tid.setText(event.dato)

            event_antPåmeldt.setText(event.antPåmeldte)
            event_anKommentar.setText(event.antKommentar)


            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(event.image) //hvilket bilde som skal loades
                .into(event_image) //Hvor vi ønsker å loade bildet inn i
        }

        /**
         * Initilialiser event-item
         * @param item eventet som skal rendres
         * @param action onClick på hver enkelt event
         * @param action2 onClick for knapper inn i event item
         */
        fun initialize(item: Event, action:OnEventItemClickListener, action2:OnKnappItemClickListener?){
            event_title.text = item.title

            event_tid.text = item.dato
            event_antPåmeldt.text = item.antPåmeldte
            event_anKommentar.text = item.antKommentar
            //Og bilde?

            itemView.setOnClickListener{
                action.onItemClick(item, adapterPosition)
            }

            slett_knapp.setOnClickListener({
                action2!!.onSlettClick(item,adapterPosition)
            })

            rediger_knapp.setOnClickListener {
                action2!!.onRedigerClick(item,adapterPosition)
            }



        }
    }

    //En view holder som skriver ut info mine eventer listen
    class EventPåmeldViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val event_image = itemView.bilde_admin_liste
        val event_title = itemView.tittel_admin_liste
        val event_tid = itemView.dato_admin_liste

        val event_antPåmeldt = itemView.ant_påmeldte_tall_admin_liste
        val event_anKommentar = itemView.antall_kommentar_tall_admin_liste

        /**
         * Fyller view med data
         * @param event eventet som skal rendres
         */
        fun bind(event: Event){
            event_title.setText(event.title)
            event_tid.setText(event.dato)

            event_antPåmeldt.setText(event.antPåmeldte)
            event_anKommentar.setText(event.antKommentar)


            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(event.image) //hvilket bilde som skal loades
                .into(event_image) //Hvor vi ønsker å loade bildet inn i
        }

        /**
         * Initilialiser event-item
         * @param item eventet som skal rendres
         * @param action onClick på hver enkelt event
         */
        fun initialize(item: Event, action:OnEventItemClickListener){
            event_title.text = item.title

            event_tid.text = item.dato
            event_antPåmeldt.text = item.antPåmeldte
            event_anKommentar.text = item.antKommentar
            //Og bilde?

            itemView.setOnClickListener{
                action.onItemClick(item, adapterPosition)
            }

        } //Slutt på initilize
    } //Slutt på AdminViewholder2

}

//Click listener på alle items
interface OnEventItemClickListener{
    fun onItemClick(item: Event, position: Int)
}

interface OnKnappItemClickListener{

    fun onSlettClick(item:Event, position: Int)

    fun onRedigerClick(item:Event, position: Int)
}



