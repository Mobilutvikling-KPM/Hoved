package RecyclerView.RecyclerView.Moduls

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

/*
* Singleton Pattern
 */
class PersonRepository() {

    lateinit var instance: PersonRepository

    private var dataset: ArrayList<Person> = ArrayList()


    fun getTheInstance(): PersonRepository {
        instance = PersonRepository()
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

        //val personID = ref.push().key // Lager en unik id som kan brukes i objektet

        Log.i("lala","person lagt til: " + person.personID)
        //person.personID = personID!!

            ref.child(person.personID).setValue(person).addOnCompleteListener {
                //Noe kan skje når databsen er ferdig lastet inn

            }


    }

    fun bliVenn(folg: Folg){
        val ref = FirebaseDatabase.getInstance()
            .getReference("Folg") //Henter referanse til det du skriver inn

        val vennskapID = ref.push().key!! // Lager en unik id som kan brukes i objektet

        ref.child(vennskapID).setValue(folg).addOnCompleteListener {
            //Noe kan skje når databsen er ferdig lastet inn
        }
    }


    fun createDataset(type: Int) {
        //dataset.add(Person("","","","","",""))
    }

}