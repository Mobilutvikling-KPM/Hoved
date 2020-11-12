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

/*
* Singleton Pattern
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

    //Later som om vi får data fra database
    fun getPersoner(type: Int): MutableLiveData<List<Person>> {
        createDataset(type)

        var data: MutableLiveData<List<Person>> = MutableLiveData()
        data.setValue(dataset)

        return data
    }

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

    fun lastOppBilde(imageURI: Uri?, personID: String){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Person").child(personID) //Henter referanse til det du skriver inn

        if(imageURI != null){
            val fileRef = mStorageRef!!.child(personID + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageURI!!)

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

                            if (snapshot!!.exists()) {
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
//                        val event = evnt.child("event").getValue(Event::class.java)
//
//                        event!!.viewType = type
//                        arr.add(event!!)
                }

                if(!nullSjekk)
                    dataCallbackHolder.onCallbackHolder(null)
                // onCallBack3(arr)
            }



            override fun onCancelled(p0: DatabaseError) {
                Log.i("lala", "NOE GIKK FEIL MED DATABASEKOBLING!")
            }

        })
    }

    fun bliVenn(folg: Folg){
        val ref = FirebaseDatabase.getInstance()
            .getReference("Folg") //Henter referanse til det du skriver inn

        val vennskapID = ref.push().key!! // Lager en unik id som kan brukes i objektet
        onFind.erFunnet(true)
        ref.child(vennskapID).setValue(folg).addOnCompleteListener {
            //Noe kan skje når databsen er ferdig lastet inn

        }
    }

//    fun bliVenn2(personID: String, innloggetID: String){
//        val ref = FirebaseDatabase.getInstance()
//            .getReference("Folg") //Henter referanse til det du skriver inn
//
//        val vennskapID = ref.push().key!! // Lager en unik id som kan brukes i objektet
//        onFind.erFunnet(true)
//        ref.child(innloggetID).push()
//
//        ref.child(innloggetID).child(vennskapID).setValue(personID).addOnCompleteListener {
//            //Noe kan skje når databsen er ferdig lastet inn
//
//        }
//    }

    fun finnUtOmVenn(innloggetID:String, brukerID: String){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Folg")

        ref.orderByChild("innloggetID").equalTo(innloggetID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {
                var funnetPerson = false
                if (p0!!.exists()) {

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

    fun sluttÅFølg(innloggetID: String, brukerID: String){
        var ref = FirebaseDatabase.getInstance()
            .getReference("Folg")

        onFind.erFunnet(false)

        ref.orderByChild("innloggetID").equalTo(innloggetID).addListenerForSingleValueEvent(object :
            ValueEventListener {

            //Inneholder alle verdier fra tabellen
            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.exists()) {


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


    fun createDataset(type: Int) {
        //dataset.add(Person("","","","","",""))
    }

}

interface DataCallbackHolder<E>{
    fun onCallbackHolder(person: Person?)
}