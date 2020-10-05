package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.EventRepository
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventViewModel: ViewModel() {

    private var mEvents: MutableLiveData<List<Event>>
    private var eventRepo: EventRepository= EventRepository().getTheInstance()

    init {
        mEvents = eventRepo.getEvents()  //Henter data fra databasen. EVent Repository
    }

    fun getEvents(): LiveData<List<Event>>{
        return mEvents
    }
}