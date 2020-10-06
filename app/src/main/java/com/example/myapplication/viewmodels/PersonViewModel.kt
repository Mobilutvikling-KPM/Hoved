package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.EventRepository
import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.Moduls.PersonRepository
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.loader.content.AsyncTaskLoader

class PersonViewModel(type: Int): ViewModel() {

    private var mPersoner: MutableLiveData<List<Person>>
    private var personRepo: PersonRepository = PersonRepository().getTheInstance()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    var type: Int = type

    init {
        mPersoner = personRepo.getPersoner(type)  //Henter data fra databasen. EVent Repository
    }

    //Skal repsentere om data er hentet eller ikke

    fun getPersoner(): LiveData<List<Person>>{
        return mPersoner
    }

    fun getIsUpdating(): LiveData<Boolean>{
        return mIsUpdating
    }
}