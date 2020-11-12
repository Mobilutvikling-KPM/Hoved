package RecyclerView.RecyclerView.Moduls

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.viewmodels.OnFind
import com.example.myapplication.viewmodels.isLoading
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * Repository for personer. Bruker singleton pattern. Kommuniserer med databasen og storage i firebase
 *
 * @property isLoading callback interface
 * @property dataCallbackSingleValue callback interface
 * @property dataCallback callback interface
 * @property onFind callback interface
 * @property dataCallbackHolder callback interface
 */
class PersonRepository(var isLoading: isLoading?, var dataCallbackSingleValue: DataCallbackSingleValue<Person>,
                       var dataCallback: DataCallback<Person>, var onFind: OnFind, var dataCallbackHolder: DataCallbackHolder<Person>) {

    lateinit var instance: PersonRepository
    private var dataset: ArrayList<Person> = ArrayList()

    //Bilde fra storage
    private var mStorageRef: StorageReference? = null

    init{
        mStorageRef = FirebaseStorage.getInstance().reference.child("Profil bilder")
    }

    fun getTheInstance(isLoading: isLoading?, dataCallbackSingleValue: DataCallbackSingleValue<Person>,
                       dataCallback: DataCallback<Person>, onFind: OnFind, dataCallbackHolder: DataCallbackHolder<Person>): PersonRepository {
        instance = PersonRepository(isLoading, dataCallbackSingleValue,dataCallback, onFind, dataCallbackHolder)
        return instance
    }

    /**
     * returnerer en tom liste
     */
    fun getPersoner(): MutableLiveData<List<Person>> {

        var data: MutableLiveData<List<Person>> = MutableLiveData()
        data.setValue(dataset)

        return data
    }

    /**
     * legger til eller oppdaterer en person i firebase
     * @param person personen som skal legges til
     */
    fun leggTilPerson(person: Person) {

        val ref = FirebaseDatabase.getInstance()
            .getReference("Person") //Henter referanse til det du skriver inn

        ref.child(person.personID).setValue(person)

        //Oppdaterer kommentarfelt dersom bruker har postet noe
        val ref2 = FirebaseDatabase.getInstance()
            .getReference("Kommentar") //Henter referanse til det du skriver inn

        ref2.orderByChild("person/personID").equalTo(person.personID).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(kmt in snapshot.children){

                    val map = HashMap<String, Any>()
                    map["person"] = person
                    ref2.child(kmt.key!!).updateChildren(map)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

    /**
     * Laster opp bilde i firebase storage
     * @param imageURI bildeaddressen som skal lastes opp
     * @param personID personen bildet tilhører
     */
    fun lastOppBilde(imageURI: Uri?, personID: String){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Person").child(personID) //Henter referanse til det du skriver inn

        if(imageURI != null){
            val fileRef = mStorageRef!!.child(personID + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageURI)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val map = HashMap<String, Any>()
                    map["profilBilde"] = url
                    ref.updateChildren(map).addOnCompleteListener {
                        isLoading!!.loadingFinished("")
                    }

                }

            }
        }
    }

    /**
     * Finn alle personer som bruker følger
     * @param personID person sin id
     */
    fun finnVenner(personID: String){

        var ref = FirebaseDatabase.getInstance()
            .getReference("Folg")

        ref.orderByChild("innloggetID").equalTo(personID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {

                var ref2 = FirebaseDatabase.getInstance()
                    .getReference("Person") //Henter referanse til det du skriver inn

                var nullSjekk: Boolean = false;
                for (folg in p0.children) {

                    nullSjekk = true;
                    var personID = folg.child("personID").value as String

                    ref2.orderByChild("personID").equalTo(personID).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.exists()) {
                                for (prs in snapshot.children) {
                                    val person = prs.getValue(Person::class.java)
                                    dataCallbackHolder.onCallbackHolder(person!!)
                                }
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
                        }

                    })

                }

                if(!nullSjekk)
                    dataCallbackHolder.onCallbackHolder(null)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

    /**
     * Følg en person
     * @param folg forholdet som skal opprettes
     */
    fun bliVenn(folg: Folg){
        val ref = FirebaseDatabase.getInstance()
            .getReference("Folg") //Henter referanse til det du skriver inn

        val vennskapID = ref.push().key!! // Lager en unik id som kan brukes i objektet
        onFind.erFunnet(true)
        ref.child(vennskapID).setValue(folg).addOnCompleteListener {
            //Noe kan skje når databsen er ferdig lastet inn

        }
    }

    /**
     * Skjekk om det er ett etablert forhold
     * @param innloggetID bruker sin id
     * @param brukerID personen som skal skjekkes
     */
    fun finnUtOmVenn(innloggetID:String, brukerID: String){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Folg")

        ref.orderByChild("innloggetID").equalTo(innloggetID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                var funnetPerson = false
                if (p0.exists()) {

                    // Log.i("lala","folg "+p0.value)
                    for (flg in p0.children) {
                        val folg = flg.getValue(Folg::class.java)

                        if(folg!!.personID == brukerID) {
                            funnetPerson = true
                        }

                    }

                    onFind.erFunnet(funnetPerson)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

    /**
     * Henter profil til innlogget bruker
     * @param id id til bruker
     * @param nyBruker skjekker om brukeren er en ny bruker eller ikke
     */
    fun hentInnloggetProfil(id: String, nyBruker: Boolean){
        val ref = FirebaseDatabase.getInstance()
            .getReference("Person")
        ref.child(id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                var mellom: MutableLiveData<Person> = MutableLiveData()
                if(p0.exists()){


                    mellom.setValue( p0.getValue(Person::class.java))

                }

                dataCallbackSingleValue.onValueReadInnlogget(mellom, nyBruker, id)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "Feil med databasen i personViewModel")
            }

        })
    }

    /**
     * Finner en person i firebase og skriver den ut
     * @param id personen som skal hentes
     */
    fun søkEtterPerson(id: String){
        val ref = FirebaseDatabase.getInstance()
            .getReference("Person")
        ref.child(id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var mellom: MutableLiveData<Person> = MutableLiveData()

                    mellom.setValue( p0.getValue(Person::class.java))

                    dataCallbackSingleValue.onValueRead(mellom)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "Feil med databasen i personViewModel")
            }

        })

    }

    /**
     * Avslutt ett følgeforhold med en person
     * @param innloggetID brukerens id
     * @param brukerID personen det gjelder
     */
    fun sluttÅFølg(innloggetID: String, brukerID: String){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Folg")

        onFind.erFunnet(false)

        ref.orderByChild("innloggetID").equalTo(innloggetID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {


                    for (flg in p0.children) {
                        val folg = flg.getValue(Folg::class.java)

                        if(folg!!.personID == brukerID) {
                            ref.child(flg.key!!).removeValue()
                        }
                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

}

interface DataCallbackHolder<E>{
    fun onCallbackHolder(person: Person?)
}