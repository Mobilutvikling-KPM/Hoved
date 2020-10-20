package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.util.Log
import androidx.lifecycle.*

import androidx.loader.content.AsyncTaskLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventViewModel(type: Int, id: String) : ViewModel(), DataCallback<Event> {

    private var mEvents: MutableLiveData<List<Event>>
    private var eventRepo: EventRepository = EventRepository().getTheInstance()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    val ref = FirebaseDatabase.getInstance()
        .getReference("Event") //Henter referanse til det du skriver inn

    var type: Int = type

    init {
        mEvents = eventRepo.getEvents(type)  //Henter data fra databasen. EVent Repository
    }

    fun leggTilEvent(event: Event) {
        mIsUpdating.setValue(true)

        eventRepo.leggTilEvent(event)

        var liste: ArrayList<Event> = mEvents.value as ArrayList<Event>
        if (liste != null) {
            liste.add(event)
            mEvents.postValue(liste)
        }

        mIsUpdating.setValue(false)

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

//    fun getEnkeltEvent(): LiveData<Event>{
//        return enkeltEvent
//    }

    fun createDataset2(type: Int) {
        //setIt("Base create")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

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

    override fun onCallBack(liste: ArrayList<Event>) {
        mIsUpdating.setValue(false)
        mEvents.setValue(liste);
    }
}