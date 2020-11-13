package RecyclerView.RecyclerView.Moduls

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * Repository for kommentarer. Bruker singleton pattern. Kommuniserer med databasen og storage i firebase
 *
 * @property callback callback interface
 */
class KommentarRepository(val callback: DataCallback<Kommentar>) {

    lateinit var instance: KommentarRepository
    val ref = FirebaseDatabase.getInstance()
        .getReference("Kommentar") //Henter referanse til det du skriver inn

    private var dataset: ArrayList<Kommentar> = ArrayList()


    fun getTheInstance(callback: DataCallback<Kommentar>): KommentarRepository {
        instance = KommentarRepository(callback)
        return instance
    }

    /**
     * Aktiverer henting av alle kommentarer i ett spesefikikt event i firebase og returnerer en tom liste
     * @param type viewType til recyclerview
     */
    fun getKommentarer(eventID: String): MutableLiveData<List<Kommentar>> {
        createDataset(eventID)

        var data: MutableLiveData<List<Kommentar>> = MutableLiveData()
        data.setValue(dataset)

        return data
    }

    /**
     * Legger til en kommentarer i firebase
     * @param kommentar kommentaren som skal legges til
     */
    fun leggTilKommentar(kommentar: Kommentar) {
        val kommentarId = ref.push().key // Lager en unik id som kan brukes i objektet

        if (kommentarId != null) {
            ref.child(kommentarId).setValue(kommentar)
        }
    }

    /**
     * Henter alle kommentarer som matcher eventID
     * @param type viewType til recyclerview
     * @param eventIDEN eventet det gjelder
     */
    fun createDataset( eventIDEN: String) {
        ref.orderByChild("eventID").equalTo(eventIDEN).addValueEventListener(object : ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                val arr: ArrayList<Kommentar> = ArrayList()
                if (p0.exists()) {

                    for (kmt in p0.children) {
                        val kommentar = kmt.getValue(Kommentar::class.java)
                        arr.add(kommentar!!)
                    }

                    callback.onCallBack(arr)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })

    }

}