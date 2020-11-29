package com.example.myapplication.repository

import com.example.myapplication.Moduls.*
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.callback_interface.DataCallBack3
import com.example.myapplication.callback_interface.DataCallback
import com.example.myapplication.callback_interface.DataCallback2
import com.example.myapplication.callback_interface.OnFind
import com.example.myapplication.callback_interface.isLoading
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * Repository for events. Bruker singleton pattern. Kommuniserer med databasen og storage i firebase
 *
 * @property isLoading callback interface
 * @property dataCallback callback interface
 * @property dataCallback2 callback interface
 * @property dataCallback3 callback interface
 * @property onFind callback interface
 * @property dataCallbackHolder callback interface
 */
class EventRepository(var isLoading: isLoading?, var dataCallback: DataCallback<Event>, var dataCallback2: DataCallback2<Event>,
                      var dataCallback3: DataCallBack3<Event>, var onFind: OnFind, var dataCallbackHolder: DataCallbackHolderEvent<Event>
)  {

    lateinit var instance: EventRepository
    private var dataset: ArrayList<Event> = ArrayList()
    private var dataset2: ArrayList<Event> = ArrayList()
    private var dataset3: ArrayList<Event> = ArrayList()

    //Referanse til storage
    private var mStorageRef: StorageReference? = null

    var ref = FirebaseDatabase.getInstance()
        .getReference("Event") //Henter referanse til det du skriver inn

    init{
        mStorageRef = FirebaseStorage.getInstance().reference.child("Event bilder")
    }

    //Singleton instance
    fun getTheInstance(isLoading: isLoading?,
                       dataCallback: DataCallback<Event>, dataCallback2: DataCallback2<Event>,
                       dataCallback3: DataCallBack3<Event>, onFind: OnFind, dataCallbackHolder: DataCallbackHolderEvent<Event>
    ): EventRepository {
        instance = EventRepository(isLoading, dataCallback,dataCallback2,dataCallback3,onFind, dataCallbackHolder)
        return instance
    }


    /**
     * legger til en event i firebase
     * @param event eventet som skal legges til
     */
    fun leggTilEvent(event: Event) {
        ref = FirebaseDatabase.getInstance()
            .getReference("Event") //Henter referanse til det du skriver inn
        val eventID = ref.push().key // Lager en unik id som kan brukes i objektet

        event.eventID = eventID!!

        ref.child(eventID).setValue(event).addOnCompleteListener {
            //Noe kan skje når databsen er ferdig lastet inn
        }
    }

    /**
     * Laster opp bilde i firebase storage
     * @param imageURI bildeaddressen som skal lastes opp
     * @param eventID eventet bildet tilhører
     */
     fun lastOppBilde(imageURI: Uri?, eventID: String){
        ref = FirebaseDatabase.getInstance()
            .getReference("Event").child(eventID) //Henter referanse til det du skriver inn

        if(imageURI != null){
            val fileRef = mStorageRef!!.child(eventID + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageURI)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val map = HashMap<String, Any>()
                    map["image"] = url
                    ref.updateChildren(map).addOnCompleteListener {
                        isLoading!!.loadingFinished(url)
                    }
                }

            }
        }
    }


    /**
     * Sletter event og dens tilhørende json objekter
     * @param event eventet som skal slettes
     **/
    fun slettEvent(event: Event){
        ref = FirebaseDatabase.getInstance()
            .getReference("Event") //Henter referanse til det du skriver inn

        if(event.image != "")
            mStorageRef!!.child(event.eventID+".jpg").delete() //Slett bildet som er lagret i storage

        //sletter event
        ref.child(event.eventID).removeValue()

        //Slett påmeldte
        ref=FirebaseDatabase.getInstance()
            .getReference("Påmeld") //Henter referanse til det du skriver inn

        ref.orderByChild("eventID").equalTo(event.eventID).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (pml in snapshot.children) {
                      ref.child(pml.key!!).removeValue()
                    }

                }

                slettKommentar(event.eventID)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })


    }

    /**
     * Slett kommentarene som tilhører ett event
     * @param eventID eventet det gjelder
     */
    fun slettKommentar(eventID: String) {
        //        //
        ref=FirebaseDatabase.getInstance()
            .getReference("Kommentar") //Henter referanse til det du skriver inn

        ref.orderByChild("eventID").equalTo(eventID).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (kmt in snapshot.children) {
                        ref.child(kmt.key!!).removeValue()
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        }
        )
    }

    /**
     * Avslutter en påmeldeling. Sletter den fra firebase
     * @param innloggetID brukeren det gjelder
     * @param eventID eventet det gjelder
     * @param erPåmeldt om brukeren er påmeldt eller ikke
     */
    fun avsluttPåmeldt(innloggetID: String, eventID: String, erPåmeldt: Boolean){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Påmeld")

        ref.orderByChild("innloggetID").equalTo(innloggetID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {


                    for (pml in p0.children) {
                        val påmeldt = pml.getValue(Påmeld::class.java)

                        if (påmeldt!!.eventID == eventID) {
                            ref.child(pml.key!!).removeValue()
                        }
                    }
                    økMedlemer(eventID, erPåmeldt)
                    onFind.erFunnet(false)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

    /**
     * Oppdaterer event i firebase
     * @param event eventet som skal oppdateres
     */
    fun updateEvent(event: Event){
        ref = FirebaseDatabase.getInstance()
            .getReference("Event") //Henter referanse til det du skriver inn


        ref.child(event.eventID).setValue(event)
    }

    /**
     * Melder bruker på ett event
     * @param påmeld Påmeld objektet som er opprettet
     * @param erPåmeldt skjekker om bruker er påmeldt
     */
    fun påmeldEvent(påmeld: Påmeld, erPåmeldt: Boolean){
        ref = FirebaseDatabase.getInstance()
            .getReference("Påmeld") //Henter referanse til det du skriver inn

        val eventID = ref.push().key!! // Lager en unik id som kan brukes i objektet

        ref.child(eventID).setValue(påmeld).addOnCompleteListener {
            //Noe kan skje når databsen er ferdig lastet inn
            økMedlemer(påmeld.eventID, erPåmeldt)
        }

        onFind.erFunnet(true)
    }

    /**
     * Skriver ut alle events som en bruker har laget
     * @param personID personen som eier eventene
     */
    fun finnLagdeEvents(personID: String){

        ref = FirebaseDatabase.getInstance()
            .getReference("Event") //Henter referanse til det du skriver inn

        ref.orderByChild("forfatter").equalTo(personID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val arr: ArrayList<Event> = ArrayList()
                if (snapshot.exists()) {

                    for (evnt in snapshot.children) {
                        val event = evnt.getValue(Event::class.java)
                        event!!.viewType = 2
                        arr.add(event)
                    }

                    dataCallback2.onCallBack2(arr)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }
        })

    }

    /**
     * Øk antall medlemer i eventet
     * @param eventID eventet det gjelder
     * @param erPåmeldt sjekker om bruker er påmeldt
     */
    fun økMedlemer(eventID: String, erPåmeldt: Boolean){

        ref = FirebaseDatabase.getInstance()
            .getReference("Event").child(eventID).child("antPåmeldte")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var ant = dataSnapshot.getValue(String::class.java)

                var nr: Int = Integer.parseInt(ant.toString())


                if(erPåmeldt)
                    nr = nr -1
                else nr = nr +1

                ref.setValue(""+(nr))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("lala", "onCancelled", databaseError.toException())
            }
        })

    }

    /**
     * Øk antall kommentarer i event
     * @param eventID eventet det gjelder
     */
    fun økAntKommentar(eventID: String){
        ref = FirebaseDatabase.getInstance()
            .getReference("Event").child(eventID).child("antKommentar")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var ant = dataSnapshot.getValue(String::class.java)

                var nr: Int = Integer.parseInt(ant.toString())

                nr = nr +1

                ref.setValue(""+(nr))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("lala", "onCancelled", databaseError.toException())
            }
        })
    }


    /**
     * Aktiverer henting av alle eventer i firebase og returnerer en tom liste
     * @param type viewType til recyclerview
     */
    fun getEvents(type: Int): MutableLiveData<List<Event>> {
        createDataset2(type)

        var data:MutableLiveData<List<Event>> = MutableLiveData()
        data.setValue(dataset)
        return data
    }

    /**
     * Aktiverer henting av alle eventer som bruker er påmeldt på i firebase og returnerer en tom liste
     * @param type viewType til recyclerview
     * @param id brukerens id
     */
    fun getPåmeldteEvents(type: Int, id: String): MutableLiveData<List<Event>> {
        var data:MutableLiveData<List<Event>> = MutableLiveData()
        data.setValue(dataset3)

        return data
    }


    /**
     * Skjekker om en bruker er påmeldt på ett enkelt event eller ikke
     * @param innloggetID brukerens id
     * @param eventID eventet som skal skjekkes
     */
    fun finnUtOmPåmeldt(innloggetID:String, eventID: String){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Påmeld")

        ref.orderByChild("innloggetID").equalTo(innloggetID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                var funnetEvent = false
                if (p0.exists()) {


                    for (pml in p0.children) {
                        val påmeldt = pml.getValue(Påmeld::class.java)

                        if(påmeldt!!.eventID == eventID) {
                            funnetEvent = true
                        }
                    }

                    onFind.erFunnet(funnetEvent)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }


    /**
     * Aktiverer henting av alle eventer som bruker har laget i firebase og returnerer en tom liste
     * @param id brukerens id
     */
    fun getLagdeEvents( id: String): MutableLiveData<List<Event>> {
        finnLagdeEvents(id)

        var data:MutableLiveData<List<Event>> = MutableLiveData()
        data.setValue(dataset2)
        return data
    }


    /**
     * Finner eventer som en bruker er påmeldt på
     * @param type viewType til recyclerview
     * @param id brukerens id
     */
    fun finnPåmeldteEvents(type: Int, id: String){

        ref = FirebaseDatabase.getInstance()
            .getReference("Påmeld") //Henter referanse til det du skriver inn

        ref.orderByChild("innloggetID").equalTo(id).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    var ref2 = FirebaseDatabase.getInstance()
                        .getReference("Event") //Henter referanse til det du skriver inn

                    for (pmlt in snapshot.children) {
                        var eventID = pmlt.child("eventID").value as String
                        ref2.orderByChild("eventID").equalTo(eventID).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {

                                if (snapshot.exists()) {

                                    for (evnt in snapshot.children) {
                                        val event = evnt.getValue(Event::class.java)
                                        event!!.viewType = type

                                        dataCallbackHolder.onCallbackHolder(event)
                                    }

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
                            }

                        })

                    } //Slutt på yttre for

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }
        })

    }



    /**
     * Henter alle events fra firebase
     * @param type viewType til recyclerview
     */
    fun createDataset2(type: Int) {
                //setIt("Base create")
        ref = FirebaseDatabase.getInstance()
            .getReference("Event") //Henter referanse til det du skriver inn

        ref.addValueEventListener(object : ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                val arr: ArrayList<Event> = ArrayList()
                if (p0.exists()) {

                    for (evt in p0.children) {
                        val event = evt.getValue(Event::class.java)
                        event!!.viewType = type

                        arr.add(event)

                    }
                    dataCallback.onCallBack(arr)
                    onCallBack(arr)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })

    }

    /**
     * funksjon som brukes som callback etter søk i firebase er fullført. Fyller dataset med alle eventer
     * @param liste listen med eventer
     */
     fun onCallBack(liste: ArrayList<Event>) {

        dataset.addAll(liste);
    }

}

interface DataCallbackHolderEvent<E>{
    fun onCallbackHolder(event: Event?)
}
