package RecyclerView.RecyclerView.Moduls

import RecyclerView.RecyclerView.EventRecyclerAdapter
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/*
* Singleton Pattern
 */
class EventRepository() : DataCallback<Event> {

    lateinit var instance: EventRepository
    private var dataset: ArrayList<Event> = ArrayList()

    val ref = FirebaseDatabase.getInstance()
        .getReference("Event") //Henter referanse til det du skriver inn


    fun getTheInstance(): EventRepository {
        instance = EventRepository()
        return instance
    }


    fun leggTilEvent(event: Event) {
        val eventID = ref.push().key // Lager en unik id som kan brukes i objektet

        event.eventID = eventID!!

        ref.child(eventID).setValue(event).addOnCompleteListener {
            //Noe kan skje når databsen er ferdig lastet inn
        }
    }

//    fun leggTilKommentar(eventID: String, liste: ArrayList<Kommentar>){
//        ref.child(eventID).child("kommentarListe").setValue(liste).addOnCompleteListener {
//
//        }
//    }

    fun getEvents(type: Int): MutableLiveData<List<Event>> {
        createDataset2(type)

        var data:MutableLiveData<List<Event>> = MutableLiveData()
        data.setValue(dataset)
        //Log.i("lala", "Test dataset: " + (dataset.value?.get(0) ?: " Empty" ))
        return data
    }

    //Henter tabellen fra firebase og returner den
            fun createDataset2(type: Int) {
                //setIt("Base create")

                ref.addValueEventListener(object : ValueEventListener {

                    //Inneholder alle verdier fra tabellen
                    override fun onDataChange(p0: DataSnapshot) {
                        val arr: ArrayList<Event> = ArrayList()
                        if (p0!!.exists()) {

                            for (evt in p0.children) {
                                val event = evt.getValue(Event::class.java)
                                event!!.viewType = type
                                // Log.i("lala", event!!.title.toString())
                                //Log.i("lala", "Event har blitt hentet")

                                arr.add(event!!)

                            }

                            onCallBack(arr)
                        }
                    }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })

    }

    override fun onCallBack(liste: ArrayList<Event>) {

        dataset.addAll(liste);
    }

}