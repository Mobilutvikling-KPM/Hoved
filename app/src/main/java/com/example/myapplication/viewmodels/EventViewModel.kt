package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.*
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * ViewModel for events. Tar vare på data og henter data fra repository
 *
 * @property type viewtype til recyclerview
 * @property id id til innlogget bruker
 * @property isLoading interface brukes til callback til view
 */

class EventViewModel(type: Int, id: String, val isLoading: isLoading?) : ViewModel(), DataCallback<Event>, DataCallback2<Event>,
    DataCallBack3<Event>, OnFind, DataCallbackHolderEvent<Event> {

    private var mEvents: MutableLiveData<List<Event>>
    private var eventRepo: EventRepository = EventRepository(isLoading, this@EventViewModel,this@EventViewModel,
                                                            this@EventViewModel,this@EventViewModel, this@EventViewModel)
                                            .getTheInstance(isLoading, this@EventViewModel,this@EventViewModel,
                                                this@EventViewModel,this@EventViewModel,this@EventViewModel)
    private var mLagdeEvents: MutableLiveData<List<Event>>
    private var mPåmeldteEvents: MutableLiveData<List<Event>>
    private var mPåmeldteEventsHolder: ArrayList<Event> = ArrayList()
    private var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()
    private var erPåmeldt: MutableLiveData<Boolean> = MutableLiveData()

    var type: Int = type

    init {
        mEvents = eventRepo.getEvents(type)  //Henter data fra databasen. EVent Repository
         mLagdeEvents = eventRepo.getLagdeEvents(id)
        mPåmeldteEvents = eventRepo.getPåmeldteEvents(type,id)
    }

    /**
     * Legger til event i firebase
     * @param event eventet som skal legges inn i firebase
     * @param imageURI bildeadressen som skal legges inn i firebase-storage
     */
    fun leggTilEvent(event: Event, imageURI: Uri?) {
        mIsUpdating.setValue(true)

        eventRepo.leggTilEvent(event)

        if(imageURI != null)
        eventRepo.lastOppBilde(imageURI, event.eventID)

        var liste: ArrayList<Event> = mEvents.value as ArrayList<Event>

            liste.add(event)
            mEvents.postValue(liste)


        mIsUpdating.setValue(false)

    }

    /**
     * Oppdaterer ett event med ny informasjon
     * @param event eventet som skal oppdateres
     * @param imageURI bilde som skal endres/puttes inn
     */
    fun updateEvent(event: Event, imageURI: Uri?){

        eventRepo.updateEvent(event)

        if(imageURI != null)
        eventRepo.lastOppBilde(imageURI, event.eventID)
    }


    /**
     * Påmeld en bruker på ett event
     * @param påmeld eventet som skal bli påmeldt og brukeren det gjelder
     * @param erPåmeldt sjekker om personen er påmeldt fra før eller ikke
     */
    fun påmeldEvent(påmeld:Påmeld, erPåmeldt: Boolean){
        eventRepo.påmeldEvent(påmeld, erPåmeldt)
    }

    /**
     * Sletter ett event fra firebase
     * @param event eventet som skal slettes
     */
    fun slettEvent(event: Event){
        eventRepo.slettEvent(event)

        var liste: ArrayList<Event> = mLagdeEvents.value as ArrayList<Event>

            liste.remove(event)
            mLagdeEvents.postValue(liste)
        
    }

    /**
     * Øk antall kommentarer i eventet som noen har kommentert i
     * @param eventID evente det gjelder
     */
    fun økAntKommentarer(eventID: String){
        eventRepo.økAntKommentar(eventID)
    }


    /**
     * get liste
     * @return returnerer liste
     */
    fun getEvents(): LiveData<List<Event>> {
        return mEvents
    }

    /**
     * get liste
     * @return returnerer liste
     */
    fun getLagdeEvents(): MutableLiveData<List<Event>> {

        return mLagdeEvents
    }

    /**
     * get liste
     * @return returnerer liste
     */
    fun getPåmeldteEvents(): MutableLiveData<List<Event>> {

        return mPåmeldteEvents
    }

    /**
     * finner eventer som tilhører en bruker
     * @param personID forfatter som skal søkes etter i eventlisten
     */
    fun finnLagdeEvents(personID: String){
        mIsUpdating.setValue(true)
        eventRepo.finnLagdeEvents(personID)

        }

    /**
     * finner eventer som en bruker har meldt seg på
     * @param type type recycler viewType
     * @param id id til personen som skal søkes etter i påmeldt lista
     */
    fun finnPåmeldteEvents(type: Int, id:String){
        mPåmeldteEventsHolder.clear()
        mIsUpdating.setValue(true)
        eventRepo.finnPåmeldteEvents(type,id)

    }

    /**
     * Finn ut om person allerede er påmeldt på ett event eller ikke
     * @param innloggetID brukerens id
     * @param eventID eventet som skal skjekkes
     */
    fun finnUtOmPåmeldt(innloggetID:String, eventID: String){
        eventRepo.finnUtOmPåmeldt(innloggetID,eventID)
    }

    /**
     * Slett fra påmeldt liste
     * @param innloggetID brukerens id
     * @param eventID eventet som skal skjekkes
     * @param erPåmeldt skjekker om bruker er påmeldt fra før eller ikke
     */
    fun avsluttPåmeldt(innloggetID: String, eventID: String, erPåmeldt: Boolean){
        this.erPåmeldt.setValue(false)
        eventRepo.avsluttPåmeldt(innloggetID,eventID, erPåmeldt)
    }

    /**
     * get boolean
     * @return returnerer boolean
     */
    fun getIsUpdating(): LiveData<Boolean> {
        return mIsUpdating
    }

    /**
     * get boolean
     * @return returnerer boolean
     */
    fun getErPåmeldt(): LiveData<Boolean>{
        return erPåmeldt
    }

    /**
     * Trigges når databasen er ferdig med å finne verdien. Skriver ut alle eventer i databasen
     * @param liste eventene som har blitt funnet
     */
    override fun onCallBack(liste: ArrayList<Event>) {
        mIsUpdating.setValue(false)
        mEvents.setValue(liste)
    }

    /**
     * Trigges når databasen er ferdig med å finne verdien. FInner eventer som bruker har laget
     * @param liste egne eventer som har blitt funnet
     */
    override fun onCallBack2(liste: ArrayList<Event>) {
        mIsUpdating.setValue(false)
        mLagdeEvents.setValue(liste)
    }

    /**
     * Trigges når databasen er ferdig med å finne verdien. Finner påmeldte Eventer
     * @param liste påmeldte eventer som har blitt funnet
     */
    override fun onCallBack3(liste: ArrayList<Event>) {
        mIsUpdating.setValue(false)

        mPåmeldteEvents.setValue(liste)

    }

    /**
     * Trigges når databasen er ferdig med å finne verdien.
     * @param skjekk om firebase fant en match for søket
     */
    override fun erFunnet(skjekk: Boolean) {
        erPåmeldt.setValue(skjekk)
    }

    /**
     * Trigges når databasen er ferdig med å finne verdien. Finner enkelt event
     * @param event eventet som har blitt funnet
     */
    override fun onCallbackHolder(event: Event) {
        mPåmeldteEventsHolder.add(event)

        for(ev: Event in mPåmeldteEventsHolder)
            onCallBack3(mPåmeldteEventsHolder)
    }
}