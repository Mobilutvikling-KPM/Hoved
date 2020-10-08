package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.loader.content.AsyncTaskLoader
import com.google.firebase.database.FirebaseDatabase

class PersonViewModel(type: Int): ViewModel() {

    private var mPersoner: MutableLiveData<List<Person>>
    private var personRepo: PersonRepository = PersonRepository().getTheInstance()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    var type: Int = type

    init {
        mPersoner = personRepo.getPersoner(type)  //Henter data fra databasen. EVent Repository
    }

    fun leggTilPerson(person: Person){
        mIsUpdating.value = true;

        personRepo.leggTilPerson(person)

        var liste: ArrayList<Person> = mPersoner.value as ArrayList<Person>
        if (liste != null) {
            liste.add(person)
            mPersoner.postValue(liste)
            mIsUpdating.postValue(false)
        }

    }
    //Skal repsentere om data er hentet eller ikke

    fun getPersoner(): LiveData<List<Person>>{
        return mPersoner
    }

    fun getIsUpdating(): LiveData<Boolean>{
        return mIsUpdating
    }
}