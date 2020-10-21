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
    private var kommentarRepo: KommentarRepository = KommentarRepository().getTheInstance()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()

    val ref = FirebaseDatabase.getInstance().getReference("Kommentar") //Henter referanse til det
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

    //Skal repsentere om data er hentet eller ikke

    fun getKommentarer(): LiveData<List<Kommentar>>{
        return mKommentar
    }

    fun getIsUpdating(): LiveData<Boolean>{
        return mIsUpdating
    }

    fun createDataset() {
        mIsUpdating.setValue(true)
        ref.orderByChild("eventID").equalTo(id).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                val arr: ArrayList<Kommentar> = ArrayList()
                if (p0!!.exists()) {

                    for (kmt in p0.children) {
                        val kommentar = kmt.getValue(Kommentar::class.java)

                        arr.add(kommentar!!)
                    }

                    onCallBack(arr)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

    override fun onCallBack(liste: ArrayList<Kommentar>) {
        mIsUpdating.setValue(false)
        mKommentar.setValue(liste)
    }
}