package RecyclerView.RecyclerView


import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Kommentar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import kotlinx.android.synthetic.main.comment_liste_item.view.*




class KommentarRecyclerAdapter(var clickListener: OnKommentarItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Kommentar> = ArrayList()

    //Sender ut hver individuelle viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return KommentarViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_liste_item, parent, false)
        )

    }

    //binder hver enkelt view til dataen
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            //bind dataen til viewholderen som er i synet
            is KommentarViewHolder -> {
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

    fun submitList(eventListe: List<Kommentar>) {
        items = eventListe
    }

    //forteller hvor mange items er i lista
    override fun getItemCount(): Int {
        return items.size
    }

    //Bygger viewholder til hovedlisten som holder på all infoen som hver enkel item skal ha.
    class KommentarViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val person_navn = itemView.brukernavn_kommentar
        val kommentar_Tekst = itemView.kommentar_tekst
        val person_bilde = itemView.bilde_profil_kommentar
        val dato = itemView.kommentar_dato

        fun bind(kommentar: Kommentar) {
            person_navn.setText(kommentar.person.brukernavn)
            kommentar_Tekst.setText(kommentar.kommentarTekst)
            dato.setText(kommentar.date)

            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load(kommentar.person.profilBilde) //hvilket bilde som skal loades
                .into(person_bilde) //Hvor vi ønsker å loade bildet inn i
        }

        //click listener initiliasiering
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


