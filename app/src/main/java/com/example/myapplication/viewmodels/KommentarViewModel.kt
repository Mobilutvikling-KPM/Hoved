package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KommentarViewModel(type: Int, id:String): ViewModel(), DataCallback<Kommentar> {

    private var mKommentar: MutableLiveData<List<Kommentar>>
    private var kommentarRepo: KommentarRepository = KommentarRepository(this@KommentarViewModel).getTheInstance(this@KommentarViewModel)
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()

//    val ref = FirebaseDatabase.getInstance().getReference("Kommentar") //Henter referanse til det
    var type: Int = type
    var id: String = id

    init {
        mKommentar = kommentarRepo.getKommentarer(type, id)  //Henter data fra databasen. EVent Repository
    }

    fun leggTilKommentar(kommentar: Kommentar){
        mIsUpdating.setValue(true)

        kommentarRepo.leggTilKommentar(kommentar)

        var liste: ArrayList<Kommentar> = mKommentar.value as ArrayList<Kommentar>
        if (liste != null) {
            liste.add(kommentar)
            mKommentar.postValue(liste)
        }

        mIsUpdating.setValue(false)

    }

    fun getKommentarer(): LiveData<List<Kommentar>>{
        return mKommentar
    }

    fun getIsUpdating(): LiveData<Boolean>{
        return mIsUpdating
    }

    override fun onCallBack(liste: ArrayList<Kommentar>) {
        mIsUpdating.setValue(false)
        mKommentar.setValue(liste)
    }
}