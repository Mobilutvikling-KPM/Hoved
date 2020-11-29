package com.example.myapplication.viewmodels

import com.example.myapplication.Moduls.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.callback_interface.DataCallback
import com.example.myapplication.repository.KommentarRepository


/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * ViewModel for kommentarer. Tar vare på data og henter data fra repository
 *
 * @property type viewtype til recyclerview
 * @property id id til innlogget bruker
 */
class KommentarViewModel(type: Int, id:String): ViewModel(), DataCallback<Kommentar> {

    private var mKommentar: MutableLiveData<List<Kommentar>>
    private var kommentarRepo: KommentarRepository = KommentarRepository(this@KommentarViewModel).getTheInstance(this@KommentarViewModel)
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()

    var type: Int = type
    var id: String = id

    init {
        mKommentar = kommentarRepo.getKommentarer(id)  //Henter data fra databasen. EVent Repository
    }

    /**
     * legger til en kommentar i firebase
     * @param kommentar kommentar som skal bli lagt til
     */
    fun leggTilKommentar(kommentar: Kommentar){
        mIsUpdating.setValue(true)

        kommentarRepo.leggTilKommentar(kommentar)

        var liste: ArrayList<Kommentar> = mKommentar.value as ArrayList<Kommentar>

            liste.add(kommentar)
            mKommentar.postValue(liste)


        mIsUpdating.setValue(false)

    }

    /**
     * get liste
     * @return liste med kommentarer
     */
    fun getKommentarer(): LiveData<List<Kommentar>>{
        return mKommentar
    }


    /**
     * get boolean
     * @return returner boolean
     */
    fun getIsUpdating(): LiveData<Boolean>{
        return mIsUpdating
    }

    /**
     * Trigges når databasen er ferdig med å finne verdien
     * @param liste en liste med personer som har blitt funnet i firebase
     */
    override fun onCallBack(liste: ArrayList<Kommentar>) {
        mIsUpdating.setValue(false)
        mKommentar.setValue(liste)
    }
}