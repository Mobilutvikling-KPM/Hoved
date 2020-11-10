package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class PersonViewModel(type: Int, id :String, val isLoading: isLoading?): ViewModel(), DataCallbackSingleValue<Person>,
    DataCallback<Person>, OnFind, DataCallbackHolder<Person> {

    private var mPersoner: MutableLiveData<List<Person>>
    private var mPersonHolder: ArrayList<Person> = ArrayList()
    private var innloggetSinProfil: MutableLiveData<Person> = MutableLiveData()
    private var enkeltPerson: MutableLiveData<Person> = MutableLiveData()
    private var personRepo: PersonRepository = PersonRepository(isLoading,this@PersonViewModel,
                                                                this@PersonViewModel,this@PersonViewModel,
                                                            this@PersonViewModel)
                                                .getTheInstance(
                                                                    isLoading,this@PersonViewModel,
                                                        this@PersonViewModel,this@PersonViewModel,
                                                    this@PersonViewModel)

    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    private var erBekjent: MutableLiveData<Boolean> = MutableLiveData()

    //Bilde fra storage
    private var mStorageRef: StorageReference? = null

    var type: Int = type

    init {
        mPersoner = personRepo.getPersoner(type)  //Henter data fra databasen. EVent Repository
        mStorageRef = FirebaseStorage.getInstance().reference.child("Profil bilder")
    }

    fun leggTilPerson(person: Person){
        mIsUpdating.setValue(true)

        personRepo.leggTilPerson(person)

        var liste: ArrayList<Person> = mPersoner.value as ArrayList<Person>
        if (liste != null) {
            liste.add(person)
            mPersoner.postValue(liste)
            mIsUpdating.setValue(false)
        }
    }

    fun bliVenn(folg: Folg){
        personRepo.bliVenn(folg)
    }

//    fun bliVenn2(personID: String,innloggetID: String){
//        personRepo.bliVenn2(personID,innloggetID)
//    }

    fun sluttÅFølg(innloggetID: String, brukerID: String){
        personRepo.sluttÅFølg(innloggetID,brukerID)
    }

    fun finnUtOmVenn(innloggetID:String, brukerID: String){
        personRepo.finnUtOmVenn(innloggetID,brukerID)
    }

    fun opprettBruker(bruker: FirebaseUser){
        hentInnloggetProfil(bruker.uid,true)
    }

    fun finnVenner(personID: String){
        mIsUpdating.setValue(true)
        mPersonHolder.clear()
        personRepo.finnVenner(personID)

    }

    fun hentInnloggetProfil(id: String, nyBruker: Boolean){
        personRepo.hentInnloggetProfil(id,nyBruker)
    }

    fun søkEtterPerson(id: String){
        personRepo.søkEtterPerson(id)
    }

    fun lastOppBilde(imageURI: Uri?, personID: String){
        personRepo.lastOppBilde(imageURI,personID)
    }

    fun getInnloggetProfil(): LiveData<Person>{
        return innloggetSinProfil
    }

    fun getEnkeltPerson(): LiveData<Person>{
        return enkeltPerson
    }

    fun getPersoner(): LiveData<List<Person>>{
        return mPersoner
    }

    fun getIsUpdating(): LiveData<Boolean>{
        return mIsUpdating
    }

    fun getErBekjent(): LiveData<Boolean>{
        return erBekjent
    }

    override fun onValueRead(verdi: MutableLiveData<Person>) {
        enkeltPerson.setValue(verdi.value)
    }

    override fun onValueReadInnlogget(verdi: MutableLiveData<Person>, nyBruker: Boolean, id:String) {
        innloggetSinProfil.setValue(verdi.value)
        if(verdi.value == null)
            personRepo.leggTilPerson(Person(id,"","","","",""))
    }

    override fun onCallBack(liste: ArrayList<Person>) {
        mIsUpdating.setValue(false)
        Log.i("lala","hey ho")
        mPersoner.setValue(liste)
    }

    override fun erFunnet(skjekk: Boolean) {

       erBekjent.setValue(skjekk)
    }

    override fun onCallbackHolder(person: Person?) {
        if(person != null) {
            mPersonHolder.add(person);
            mIsUpdating.setValue(false)
            Log.i("lala", "hey ho")
            // for(prs: Person in mPersonHolder)
            onCallBack(mPersonHolder)
        } else  mIsUpdating.setValue(false)
    }

}