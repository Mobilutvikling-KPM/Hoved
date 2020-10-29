package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.app.ProgressDialog
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*

import androidx.loader.content.AsyncTaskLoader
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

class EventViewModel(type: Int, id: String, val isLoading: isLoading?) : ViewModel(), DataCallback<Event>, DataCallback2<Event>, DataCallBack3<Event>, OnFind {

    private var mEvents: MutableLiveData<List<Event>>
    private var eventRepo: EventRepository = EventRepository().getTheInstance()
    private var mLagdeEvents: MutableLiveData<List<Event>>
    private var mPåmeldteEvents: MutableLiveData<List<Event>>
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    private var erPåmeldt: MutableLiveData<Boolean> = MutableLiveData()


    //Bilde fra storage
    private var mStorageRef: StorageReference? = null

    var ref = FirebaseDatabase.getInstance()
        .getReference("Event") //Henter referanse til det du skriver inn

    var type: Int = type

    init {
        mEvents = eventRepo.getEvents(type)  //Henter data fra databasen. EVent Repository
         mLagdeEvents = eventRepo.getLagdeEvents(type,id)
        mPåmeldteEvents = eventRepo.getPåmeldteEvents(type,id)
        mStorageRef = FirebaseStorage.getInstance().reference.child("Event bilder")
    }

    fun leggTilEvent(event: Event, imageURI: Uri?) {
        mIsUpdating.setValue(true)

        eventRepo.leggTilEvent(event)
        lastOppBilde(imageURI, event.eventID)

        var liste: ArrayList<Event> = mEvents.value as ArrayList<Event>
        if (liste != null) {
            liste.add(event)
            mEvents.postValue(liste)
        }

        mIsUpdating.setValue(false)

    }

    fun updateEvent(event: Event, imageURI: Uri?){

        eventRepo.updateEvent(event)
        lastOppBilde(imageURI, event.eventID)
    }

    private fun lastOppBilde(imageURI: Uri?, eventID: String){
        ref = FirebaseDatabase.getInstance()
            .getReference("Event").child(eventID) //Henter referanse til det du skriver inn

        if(imageURI != null){
            val fileRef = mStorageRef!!.child(eventID + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageURI!!)

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

    fun påmeldEvent(påmeld:Påmeld, erPåmeldt: Boolean){
        eventRepo.påmeldEvent(påmeld, erPåmeldt)
        erFunnet(true)
    }

    fun slettEvent(event: Event){
        eventRepo.slettEvent(event)
        mStorageRef!!.child(event.eventID+".jpg").delete() //Slett bildet som er lagret i storage

        var liste: ArrayList<Event> = mLagdeEvents.value as ArrayList<Event>
        if (liste != null) {
            liste.remove(event)
            mLagdeEvents.postValue(liste)
        }
    }


    fun økKommentarer(event: Event){

    }

//    fun leggTilKommentar(eventID: String, liste: ArrayList<Kommentar>){
//        eventRepo.leggTilKommentar(eventID, liste)
//    }

    fun getEvents(): LiveData<List<Event>> {
        mIsUpdating.setValue(true)

        //Henter tabellen fra firebase og returner den
        createDataset2(type)

        return mEvents
    }

    fun getLagdeEvents(): MutableLiveData<List<Event>> {

        return mLagdeEvents
    }

    fun getPåmeldteEvents(): MutableLiveData<List<Event>> {

        return mPåmeldteEvents
    }

    fun finnLagdeEvents(personID: String){
        mIsUpdating.setValue(true)
        ref = FirebaseDatabase.getInstance()
            .getReference("Event") //Henter referanse til det du skriver inn

        ref.orderByChild("forfatter").equalTo(personID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val arr: ArrayList<Event> = ArrayList()
                if (snapshot!!.exists()) {

                    for (evnt in snapshot.children) {
                        val event = evnt.getValue(Event::class.java)
                        event!!.viewType = 2
                        arr.add(event!!)
                    }

                    onCallBack2(arr)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }
        })

        }

    fun finnPåmeldteEvents(type: Int, id:String){
        mIsUpdating.setValue(true)
        ref = FirebaseDatabase.getInstance()
            .getReference("Påmeld") //Henter referanse til det du skriver inn

        ref.orderByChild("innloggetID").equalTo(id).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val arr: ArrayList<Event> = ArrayList()

                if (snapshot!!.exists()) {


                    for (evnt in snapshot.children) {

                        val event = evnt.child("event").getValue(Event::class.java)

                        event!!.viewType = type
                        arr.add(event!!)
                    }

                    onCallBack3(arr)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }
        })

    }

    fun finnUtOmPåmeldt(innloggetID:String, eventID: String){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Påmeld")

        ref.orderByChild("innloggetID").equalTo(innloggetID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                var funnetEvent = false
                if (p0!!.exists()) {


                    for (pml in p0.children) {
                        val påmeldt = pml.getValue(Påmeld::class.java)

                        if(påmeldt!!.event.eventID == eventID) {
                            funnetEvent = true
                        }
                    }

                    erFunnet(funnetEvent)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

    fun avsluttPåmeldt(innloggetID: String, eventID: String, erPåmeldt: Boolean){
        eventRepo.avsluttPåmeldt(innloggetID,eventID, erPåmeldt)
        erFunnet(false)
    }

//    fun getEnkeltEvent(): LiveData<Event>{
//        return enkeltEvent
//    }

    fun createDataset2(type: Int) {
        //setIt("Base create")
        ref = FirebaseDatabase.getInstance()
            .getReference("Event") //Henter referanse til det du skriver inn

        ref.addValueEventListener(object : ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                val arr: ArrayList<Event> = ArrayList()
                if (p0!!.exists()) {

                    for (evt in p0.children) {
                        val event = evt.getValue(Event::class.java)
                        event!!.viewType = type

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


    fun getIsUpdating(): LiveData<Boolean> {
        return mIsUpdating
    }

    fun getErPåmeldt(): LiveData<Boolean>{
        return erPåmeldt
    }

    override fun onCallBack(liste: ArrayList<Event>) {
        mIsUpdating.setValue(false)

        mEvents.setValue(liste)
    }

    override fun onCallBack2(liste: ArrayList<Event>) {
        mIsUpdating.setValue(false)
        mLagdeEvents.setValue(liste)
    }

    override fun onCallBack3(liste: ArrayList<Event>) {
        mIsUpdating.setValue(false)
        mPåmeldteEvents.setValue(liste)
    }

    override fun erFunnet(skjekk: Boolean) {
        erPåmeldt.setValue(skjekk)
    }
}