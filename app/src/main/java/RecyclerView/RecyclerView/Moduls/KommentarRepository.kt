package RecyclerView.RecyclerView.Moduls

import android.text.format.DateFormat.format
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.String.format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/*
* Singleton Pattern
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

    //Henter data fra databasen
    fun getKommentarer(type: Int, eventID: String): MutableLiveData<List<Kommentar>> {
        createDataset(type, eventID)

        var data: MutableLiveData<List<Kommentar>> = MutableLiveData()
        data.setValue(dataset)

        return data
    }

    fun leggTilKommentar(kommentar: Kommentar) {
        val kommentarId = ref.push().key // Lager en unik id som kan brukes i objektet

        if (kommentarId != null) {
            ref.child(kommentarId).setValue(kommentar).addOnCompleteListener {
                //Noe kan skje når databsen er ferdig lastet inn

            }
        }
    }

    fun createDataset(type: Int, eventIDEN: String) {
        ref.orderByChild("eventID").equalTo(eventIDEN).addValueEventListener(object : ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                val arr: ArrayList<Kommentar> = ArrayList()
                if (p0!!.exists()) {

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