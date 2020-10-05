package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.EventRepository
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.loader.content.AsyncTaskLoader

class EventViewModel(type: Int): ViewModel() {

    private var mEvents: MutableLiveData<List<Event>>
    private var eventRepo: EventRepository= EventRepository().getTheInstance()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    var type: Int = type

    init {
        mEvents = eventRepo.getEvents(type)  //Henter data fra databasen. EVent Repository
    }

    //Skal repsentere om data er hentet eller ikke

    fun leggTilEvent(event: Event){
        mIsUpdating.value = true;

       var liste: ArrayList<Event> = mEvents.value as ArrayList<Event>
        if (liste != null) {
            liste.add(event)
            mEvents.postValue(liste)
            mIsUpdating.postValue(false)
        }

    }
    fun getEvents(): LiveData<List<Event>>{
        return mEvents
    }

    fun getIsUpdating(): LiveData<Boolean>{
        return mIsUpdating
    }
}