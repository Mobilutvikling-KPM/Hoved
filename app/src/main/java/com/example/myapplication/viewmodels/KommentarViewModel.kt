package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KommentarViewModel(type: Int): ViewModel() {

    private var mKommentar: MutableLiveData<List<Kommentar>>
    private var kommentarRepo: KommentarRepository = KommentarRepository().getTheInstance()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    var type: Int = type

    init {
        mKommentar = kommentarRepo.getKommentarer(type)  //Henter data fra databasen. EVent Repository
    }

    fun leggTilKommentar(kommentar: Kommentar){
        mIsUpdating.value = true;

        var liste: ArrayList<Kommentar> = mKommentar.value as ArrayList<Kommentar>
        if (liste != null) {
            liste.add(kommentar)
            mKommentar.postValue(liste)
            mIsUpdating.postValue(false)
        }

    }

    //Skal repsentere om data er hentet eller ikke

    fun getKommentarer(): LiveData<List<Kommentar>>{
        return mKommentar
    }

    fun getIsUpdating(): LiveData<Boolean>{
        return mIsUpdating
    }
}