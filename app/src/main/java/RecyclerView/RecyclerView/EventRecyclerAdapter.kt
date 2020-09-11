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

class EventRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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
            }
        }
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
        val event_author = itemView.event_author

        fun bind(event: Event){
            event_title.setText(event.title)
            event_author.setText(event.username)

            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(event.image) //hvilket bilde som skal loades
                .into(event_image) //Hvor vi ønsker å loade bildet inn i
        }
    }
}