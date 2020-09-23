package RecyclerView.RecyclerView

import RecyclerView.RecyclerView.Moduls.Event
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import kotlinx.android.synthetic.main.layout_event_list_item.view.*

class EventRecyclerAdapter(var clickListener: OnEventItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<Event> = ArrayList()

    //Sender ut hver individuelle viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EventViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_event_list_item, parent, false)
        )
    }

    //binder hver enkelt view til dataen
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            //bind dataen til viewholderen som er i synet
            is EventViewHolder ->{
                holder.bind(items.get(position))
                holder.initialize(items.get(position),clickListener)
            }
        }

        //Hva skal skje når en item har blitt klikket på
//        holder.itemView.setOnClickListener{view ->
//            view.findNavController().navigate(R.id.action_event_liste_fragment_to_eventFragment)
//            Log.i("TEST", "Dette er en test ----------------------------->" )
//            //ALLE ANDRE FRAGMENTER ENN Event_liste_fragment klikker. Tror det har noe å gjøre med at recycleradapter er definert i main activity
//        }
    }

    fun submitList(eventListe: List<Event>){
            items = eventListe
    }

    //forteller hvor mange items er i lista
    override fun getItemCount(): Int {
        return items.size
    }

    //Bygger viewholder som holder på all infoen som hver enkel item skal ha.
    class EventViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val event_image = itemView.event_image
        val event_title = itemView.event_title
        val event_tid = itemView.event_tid
        val event_sted = itemView.event_sted
        val event_antPåmeldt = itemView.ant_påmeldte_tall
        val event_anKommentar = itemView.antall_kommentar_tall

        fun bind(event: Event){
            event_title.setText(event.title)
            event_tid.setText(event.dato)
            event_sted.setText(", " + event.sted)
            event_antPåmeldt.setText(event.antPåmeldte)
            event_anKommentar.setText(event.antKommentar)


            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(event.image) //hvilket bilde som skal loades
                .into(event_image) //Hvor vi ønsker å loade bildet inn i
        }

        //click listener initiliasiering
        fun initialize(item: Event, action:OnEventItemClickListener){
            event_title.text = item.title
            event_sted.text = item.sted
            event_tid.text = item.dato
            event_antPåmeldt.text = item.antPåmeldte
            event_anKommentar.text = item.antKommentar
            //Og bilde?

            itemView.setOnClickListener{
                action.onItemClick(item, adapterPosition)
               // itemView.findNavController().navigate(R.id.action_event_liste_fragment_to_eventFragment)
            }
        }
    }
}

//Click listener på alle items
interface OnEventItemClickListener{
    fun onItemClick(item: Event, position: Int)
}

