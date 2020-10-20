package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.util.Log
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.loader.content.AsyncTaskLoader
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PersonViewModel(type: Int, id :String): ViewModel(), DataCallbackSingleValue<Person> {

    private var mPersoner: MutableLiveData<List<Person>>
    private var innloggetSinProfil: MutableLiveData<Person> = MutableLiveData()
    private var enkeltPerson: MutableLiveData<Person> = MutableLiveData()
    private var personRepo: PersonRepository = PersonRepository().getTheInstance()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    val ref = FirebaseDatabase.getInstance()
        .getReference("Person")
    var type: Int = type

    init {
        mPersoner = personRepo.getPersoner(type)  //Henter data fra databasen. EVent Repository
    }

    fun leggTilPerson(person: Person, bruker: FirebaseUser){
        mIsUpdating.setValue(true)

        personRepo.leggTilPerson(person, bruker)

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

    fun hentInnloggetProfil(id: String){
        ref.child(id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var mellom: MutableLiveData<Person> = MutableLiveData()

                    mellom.setValue( p0.getValue(Person::class.java))

                    onValueReadInnlogget(mellom)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "Feil med databasen i personViewModel")
            }

        })
    }

    fun søkEtterPerson(id: String){

       ref.child(id).addListenerForSingleValueEvent(object: ValueEventListener{
           override fun onDataChange(p0: DataSnapshot) {
               if(p0.exists()){
                   var mellom: MutableLiveData<Person> = MutableLiveData()

                  mellom.setValue( p0.getValue(Person::class.java))

                 onValueRead(mellom)
               }

           }

           override fun onCancelled(p0: DatabaseError) {
               Log.i("lala", "Feil med databasen i personViewModel")
           }

       })

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

    override fun onValueRead(verdi: MutableLiveData<Person>) {
        enkeltPerson.setValue(verdi.value)
    }

    override fun onValueReadInnlogget(verdi: MutableLiveData<Person>) {
        innloggetSinProfil.setValue(verdi.value)
    }

}