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

class PersonViewModel(type: Int, id :String): ViewModel(), DataCallbackSingleValue<Person>, DataCallback<Person> {

    private var mPersoner: MutableLiveData<List<Person>>
    private var innloggetSinProfil: MutableLiveData<Person> = MutableLiveData()
    private var enkeltPerson: MutableLiveData<Person> = MutableLiveData()
    private var personRepo: PersonRepository = PersonRepository().getTheInstance()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()

    var type: Int = type

    init {
        mPersoner = personRepo.getPersoner(type)  //Henter data fra databasen. EVent Repository
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

    fun opprettBruker(bruker: FirebaseUser){

        hentInnloggetProfil(bruker.uid,true)
    }

    fun finnVenner(personID: String){
       var ref = FirebaseDatabase.getInstance()
            .getReference("Folg")

        ref.orderByChild("innloggetID").equalTo(personID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                val arr: ArrayList<Person> = ArrayList()
                if (p0!!.exists()) {

                    for (flg in p0.children) {
                        val folg = flg.getValue(Folg::class.java)

                        arr.add(folg!!.person)
                    }

                    onCallBack(arr)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

    fun hentInnloggetProfil(id: String, nyBruker: Boolean){
        val ref = FirebaseDatabase.getInstance()
            .getReference("Person")
        ref.child(id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                var mellom: MutableLiveData<Person> = MutableLiveData()
                if(p0.exists()){


                    mellom.setValue( p0.getValue(Person::class.java))

                }

                onValueReadInnlogget(mellom, nyBruker, id)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "Feil med databasen i personViewModel")
            }

        })
    }

    fun søkEtterPerson(id: String){
        val ref = FirebaseDatabase.getInstance()
            .getReference("Person")
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

    override fun onValueReadInnlogget(verdi: MutableLiveData<Person>, nyBruker: Boolean, id:String) {
        innloggetSinProfil.setValue(verdi.value)
        if(verdi.value == null)
            personRepo.leggTilPerson(Person(id,"Test","","","",""))
    }

    override fun onCallBack(liste: ArrayList<Person>) {
        mPersoner.setValue(liste)
    }

}