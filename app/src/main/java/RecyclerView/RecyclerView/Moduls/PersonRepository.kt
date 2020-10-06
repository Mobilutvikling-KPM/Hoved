package RecyclerView.RecyclerView.Moduls

import androidx.lifecycle.MutableLiveData

/*
* Singleton Pattern
 */
class PersonRepository() {

    lateinit var instance: PersonRepository

    private var dataset: ArrayList<Person> = ArrayList()


    fun getTheInstance(): PersonRepository{
        instance = PersonRepository()
        return instance
    }

    //Later som om vi får data fra database
    fun getPersoner(type: Int): MutableLiveData<List<Person>>{
        createDataset(type)

        var data: MutableLiveData<List<Person>> = MutableLiveData()
        data.setValue(dataset)

        return data
    }

    fun createDataset(type: Int){
        dataset.addAll(DataSourcePerson.createDataset())
    }

}