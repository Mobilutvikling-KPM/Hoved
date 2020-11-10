package RecyclerView.RecyclerView

import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.Moduls.Event
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import kotlinx.android.synthetic.main.administrer_event_liste_item.view.*
import kotlinx.android.synthetic.main.layout_event_list_item.view.*
import kotlinx.android.synthetic.main.person_liste_item.view.*

class PersonRecyclerAdapter(var clickListener: OnPersonItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Person> = ArrayList()

    //Sender ut hver individuelle viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PersonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.person_liste_item, parent, false)
        )

    }

    //binder hver enkelt view til dataen
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            //bind dataen til viewholderen som er i synet
            is PersonViewHolder -> {
                holder.bind(items.get(position))
                holder.initialize(items.get(position), clickListener)
            }


        }

        //Hva skal skje når en item har blitt klikket på
//        holder.itemView.setOnClickListener{view ->
//            view.findNavController().navigate(R.id.action_event_liste_fragment_to_eventFragment)
//            Log.i("TEST", "Dette er en test ----------------------------->" )
//            //ALLE ANDRE FRAGMENTER ENN Event_liste_fragment klikker. Tror det har noe å gjøre med at recycleradapter er definert i main activity
//        }
    }

    fun submitList(eventListe: List<Person>) {
        items = eventListe
    }

    //forteller hvor mange items er i lista
    override fun getItemCount(): Int {
        return items.size
    }

    //Bygger viewholder til hovedlisten som holder på all infoen som hver enkel item skal ha.
    class PersonViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val person_navn = itemView.brukernavn_item
        val person_bilde = itemView.bilde_profil_item

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

        //click listener initiliasiering
        fun initialize(item: Person, action: OnPersonItemClickListener) {
            person_navn.text = item.brukernavn

            //Og bilde?

            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
                // itemView.findNavController().navigate(R.id.action_event_liste_fragment_to_eventFragment)
            }
        }
    }

    //Click listener på alle items
    interface OnPersonItemClickListener {
        fun onItemClick(item: Person, position: Int)
    }

}

