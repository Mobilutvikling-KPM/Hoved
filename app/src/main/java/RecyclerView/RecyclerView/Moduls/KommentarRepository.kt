package RecyclerView.RecyclerView.Moduls

import androidx.lifecycle.MutableLiveData

/*
* Singleton Pattern
 */
class KommentarRepository() {

    lateinit var instance: KommentarRepository

    private var dataset: ArrayList<Kommentar> = ArrayList()


    fun getTheInstance(): KommentarRepository{
        instance = KommentarRepository()
        return instance
    }

    //Later som om vi får data fra database
    fun getKommentarer(type: Int): MutableLiveData<List<Kommentar>>{
        createDataset(type)

        var data: MutableLiveData<List<Kommentar>> = MutableLiveData()
        data.setValue(dataset)

        return data
    }

    fun createDataset(type: Int){

        dataset.add(
            Kommentar(
            Person("Henriette Pedersen",
                    "24",
                    "@String/input",
                    "https://i.pinimg.com/originals/f7/60/87/f76087d518532f3a0c6b027d18e1212a.jpg")
                    ,
                "Dette er en dummy kommentar med random tekst, Yo let's go!")
        )

        dataset.add(
            Kommentar(
                Person("Chris Jack",
                    "30",
                    "@String/input",
                    "https://i.pinimg.com/originals/35/d2/eb/35d2ebe20571c03d8f257ae725a780aa.jpg")
            ,
                "Hey, ho, hey, ho, lets'a go for merry so"
            )
        )
        dataset.add(
            Kommentar(
                Person("Roger Floger",
                    "27",
                    "@String/input",
                    "https://media.gettyimages.com/photos/closeup-of-a-mans-head-profile-picture-id157192886")
            ,
                "Lorem ipsum, Dummy tekst, dummy tekst" +
                        "Test. TEST!"
            )
        )
    }

}