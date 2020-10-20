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
class KommentarRepository() : DataCallback<Kommentar> {

    private var calendar: Calendar = Calendar.getInstance();
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var date: String
    lateinit var instance: KommentarRepository
    val ref = FirebaseDatabase.getInstance()
        .getReference("Kommentar") //Henter referanse til det du skriver inn

    private var dataset: ArrayList<Kommentar> = ArrayList()


    fun getTheInstance(): KommentarRepository {
        instance = KommentarRepository()
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
            Log.i("lala",eventIDEN)
            ref.orderByChild("eventID").equalTo(eventIDEN).addListenerForSingleValueEvent(object : ValueEventListener {

                //Inneholder alle verdier fra tabellen
                override fun onDataChange(p0: DataSnapshot) {
                    val arr: ArrayList<Kommentar> = ArrayList()
                    if (p0!!.exists()) {

                        for (kmt in p0.children) {
                            val kommentar = kmt.getValue(Kommentar::class.java)
                            Log.i("lala",kommentar!!.kommentarTekst)
                            arr.add(kommentar!!)
                        }

                        onCallBack(arr)
                    }
                    Log.i("lala","Utenfor exists")
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
                }

            })

//        dateFormat = SimpleDateFormat("MM/dd/yyyy");
//        date = dateFormat.format(calendar.getTime());
//
//        dataset.add(
//
//            Kommentar(
//                "-MJ7NjgdeI26SdLz-7Me",
//                Person(
//                    "-MJfZwZdE3vbcO2qIMvA",
//                    "Henriette Pedersen",
//                    "24",
//                    "Bø",
//                    "@String/input",
//                    "https://i.pinimg.com/originals/f7/60/87/f76087d518532f3a0c6b027d18e1212a.jpg"
//                ),
//                date,
//                "Dette er en dummy kommentar med random tekst, Yo let's go!"
//            )
//        )
//
//        dataset.add(
//            Kommentar(
//                "-MJ7k7NVJ_ye5ZUwZAda",
//                Person(
//                    "-MJf_NohvXNs5VjToRd_",
//                    "Chris Jack",
//                    "30",
//                    "Langesund",
//                    "@String/input",
//                    "https://i.pinimg.com/originals/35/d2/eb/35d2ebe20571c03d8f257ae725a780aa.jpg"
//                ),date,
//                "Hey, ho, hey, ho, lets'a go for merry so"
//            )
//        )
//        dataset.add(
//            Kommentar(
//                "-MJRcwMJ0ivStGWAP6Pi",
//                Person(
//                    "-MJf_XPDXcKzoTs01wnB",
//                    "Roger Floger",
//                    "27",
//                    "Oslo",
//                    "@String/input",
//                    "https://media.gettyimages.com/photos/closeup-of-a-mans-head-profile-picture-id157192886"
//                ),
//                date,
//                "Lorem ipsum, Dummy tekst, dummy tekst" +
//                        "Test. TEST!"
//            )
//        )

    }

    override fun onCallBack(liste: ArrayList<Kommentar>) {
        Log.i("lala","Inni callback: " + liste.toString())
        dataset.addAll(liste)
    }
}