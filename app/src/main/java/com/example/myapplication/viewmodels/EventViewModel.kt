package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventViewModel(type: Int, id: String, val isLoading: isLoading?) : ViewModel(), DataCallback<Event>, DataCallback2<Event>,
    DataCallBack3<Event>, OnFind, DataCallbackHolderEvent<Event> {

    private var mEvents: MutableLiveData<List<Event>>
    private var eventRepo: EventRepository = EventRepository(isLoading, this@EventViewModel,this@EventViewModel,
                                                            this@EventViewModel,this@EventViewModel, this@EventViewModel)
                                            .getTheInstance(isLoading, this@EventViewModel,this@EventViewModel,
                                                this@EventViewModel,this@EventViewModel,this@EventViewModel)
    private var mLagdeEvents: MutableLiveData<List<Event>>
    private var mPåmeldteEvents: MutableLiveData<List<Event>>
    private var mPåmeldteEventsHolder: ArrayList<Event> = ArrayList()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    private var erPåmeldt: MutableLiveData<Boolean> = MutableLiveData()

    var type: Int = type

    init {
        mEvents = eventRepo.getEvents(type)  //Henter data fra databasen. EVent Repository
         mLagdeEvents = eventRepo.getLagdeEvents(type,id)
        mPåmeldteEvents = eventRepo.getPåmeldteEvents(type,id)
    }

    fun leggTilEvent(event: Event, imageURI: Uri?) {
        mIsUpdating.setValue(true)

        eventRepo.leggTilEvent(event)

        if(imageURI != null)
        eventRepo.lastOppBilde(imageURI, event.eventID)

        var liste: ArrayList<Event> = mEvents.value as ArrayList<Event>
        if (liste != null) {
            liste.add(event)
            mEvents.postValue(liste)
        }

        mIsUpdating.setValue(false)

    }

    fun updateEvent(event: Event, imageURI: Uri?){

        eventRepo.updateEvent(event)

        if(imageURI != null)
        eventRepo.lastOppBilde(imageURI, event.eventID)
    }


    fun påmeldEvent(påmeld:Påmeld, erPåmeldt: Boolean){
        eventRepo.påmeldEvent(påmeld, erPåmeldt)
    }

    fun slettEvent(event: Event){
        eventRepo.slettEvent(event)

        var liste: ArrayList<Event> = mLagdeEvents.value as ArrayList<Event>
        if (liste != null) {
            liste.remove(event)
            mLagdeEvents.postValue(liste)
        }
    }


    fun økAntKommentarer(eventID: String){
        eventRepo.økAntKommentar(eventID)
    }


    fun getEvents(): LiveData<List<Event>> {
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
        eventRepo.finnLagdeEvents(personID)

        }

    fun finnPåmeldteEvents(type: Int, id:String){
        mPåmeldteEventsHolder.clear()
        mIsUpdating.setValue(true)
        eventRepo.finnPåmeldteEvents(type,id)

    }

    fun finnUtOmPåmeldt(innloggetID:String, eventID: String){
        eventRepo.finnUtOmPåmeldt(innloggetID,eventID)
    }

    fun avsluttPåmeldt(innloggetID: String, eventID: String, erPåmeldt: Boolean){
        this.erPåmeldt.setValue(false)
        eventRepo.avsluttPåmeldt(innloggetID,eventID, erPåmeldt)
    }


    fun createDataset2(type: Int) {
        eventRepo.createDataset2(type)
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

    override fun onCallbackHolder(event: Event) {
        mPåmeldteEventsHolder.add(event)

        for(ev: Event in mPåmeldteEventsHolder)
            onCallBack3(mPåmeldteEventsHolder)
    }
}