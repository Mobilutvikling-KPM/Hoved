package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * ViewModel for personer. Tar vare på data og henter data fra repository
 *
 * @property type viewtype til recyclerview
 * @property id id til innlogget bruker
 * @property isLoading interface brukes til callback til view
 */
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

    //viewType til recyclerview
    var type: Int = type

    init {
        mPersoner = personRepo.getPersoner()  //Henter data fra databasen. EVent Repository
        mStorageRef = FirebaseStorage.getInstance().reference.child("Profil bilder")
    }

    /**
     * legger til/oppdaterer en person i firebase
     * @param person personen som skal legges til
     */
    fun leggTilPerson(person: Person){
        mIsUpdating.setValue(true)

        personRepo.leggTilPerson(person)

        var liste: ArrayList<Person> = mPersoner.value as ArrayList<Person>

            liste.add(person)
            mPersoner.postValue(liste)
            mIsUpdating.setValue(false)

    }

    /**
     * legger til ett at en personen følger en annen i firebase
     * @param folg forholdet
     */
    fun bliVenn(folg: Folg){
        personRepo.bliVenn(folg)
    }

    /**
     * Avslutter ett følge forhold
     * @param innloggetID den som avslutter
     * @param brukerID personen som blir avsluttet
     */
    fun sluttÅFølg(innloggetID: String, brukerID: String){
        personRepo.sluttÅFølg(innloggetID,brukerID)
    }

    /**
     * Finn ut om innlogget bruker har fulgt denne personen
     * @param innloggetID den som avslutter
     * @param brukerID personen som blir avsluttet
     */
    fun finnUtOmVenn(innloggetID:String, brukerID: String){

        personRepo.finnUtOmVenn(innloggetID,brukerID)
    }


    /**
     * Finner alle personer innlogget bruker har fulgt
     * @param personID innlogget bruker sin id
     */
    fun finnVenner(personID: String){
        mIsUpdating.setValue(true)
        mPersonHolder.clear()
        personRepo.finnVenner(personID)

    }

    /**
     * Henter profilen til innlogget bruker. Dersom brukeren er ny vil det være en alternativ rute
     * @param bruker om brukeren er ny eller ikke
     */
    fun hentInnloggetProfil(bruker: FirebaseUser){
        personRepo.hentInnloggetProfil(bruker)
    }

    /**
     * Henter brukeren til en enkelt person
     * @param id personid til personen som skal hentes
     */
    fun søkEtterPerson(id: String){
        personRepo.søkEtterPerson(id)
    }

    /**
     * Laster opp ett bilde i firebase-storage
     * @param imageURI bildeaddressen
     * @param personID brukeren det gjelder
     */
    fun lastOppBilde(imageURI: Uri?, person:Person){
        personRepo.lastOppBilde(imageURI,person)
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

    /**
     * Trigges når databasen er ferdig med å finne verdien
     * @param verdi personen som har blitt funnet
     */
    override fun onValueRead(verdi: MutableLiveData<Person>) {
        enkeltPerson.setValue(verdi.value)
    }

    /**
     * Trigges når databasen er ferdig med å finne brukeren til personen som har logget inn. Dersom det er en ny bruker trigges leggTilPerson()
     * @param verdi personen som har blitt funnet
     * @param bruker Brukeren som har logget inn
     */
    override fun onValueReadInnlogget(verdi: MutableLiveData<Person>,bruker: FirebaseUser) {
        innloggetSinProfil.setValue(verdi.value)
        if(verdi.value == null)
            personRepo.leggTilPerson(Person(bruker.uid,bruker.displayName!!,"","","",""))
    }

    /**
     * Trigges når databasen er ferdig med å finne verdien
     * @param liste liste med personer som har blitt funnet
     */
    override fun onCallBack(liste: ArrayList<Person>) {
        mIsUpdating.setValue(false)
        mPersoner.setValue(liste)
    }

    /**
     * Trigges når databasen er ferdig med søket.
     * @param skjekk om verdien finnes i firebase eller ikke
     */
    override fun erFunnet(skjekk: Boolean) {

        erBekjent.setValue(skjekk)
    }

    /**
     * Trigges når databasen er ferdig med å finne verdien .
     * @param person personen som har blitt funnet
     */
    override fun onCallbackHolder(person: Person?) {
        if(person != null) {
            mPersonHolder.add(person);
            mIsUpdating.setValue(false)

            onCallBack(mPersonHolder)
        } else  mIsUpdating.setValue(false)
    }

}