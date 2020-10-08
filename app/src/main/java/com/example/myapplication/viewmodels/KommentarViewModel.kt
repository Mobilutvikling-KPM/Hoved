package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.widget.Toast
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

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

        //TEST SKRIV INN KOMMENTAR TIL FIREBASE
        val testTekst = kommentar.kommentarTekst
        val ref = FirebaseDatabase.getInstance().getReference("TEST2") //Henter referanse til det
                                                                            // du skriver inn
        val kommentarId = ref.push().key // Lager en unik id som kan brukes i objektet

        if (kommentarId != null) {
            ref.child(kommentarId).setValue(testTekst).addOnCompleteListener {
                //Noe kan skje når databsen er ferdig lastet inn

            }
        }

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