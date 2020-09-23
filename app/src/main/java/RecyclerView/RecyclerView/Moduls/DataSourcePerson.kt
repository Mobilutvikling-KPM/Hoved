package RecyclerView.RecyclerView.Moduls

class DataSourcePerson {

    companion object{
        fun createDataset(): ArrayList<Person> {
            val pList = ArrayList<Person>()

            pList.add(Person("Henriette Pedersen",
                "24",
                "@String/input",
            "https://i.pinimg.com/originals/f7/60/87/f76087d518532f3a0c6b027d18e1212a.jpg")
            )

            pList.add(Person("Chris Jack",
                "30",
                "@String/input",
            "https://i.pinimg.com/originals/35/d2/eb/35d2ebe20571c03d8f257ae725a780aa.jpg")
            )

            pList.add(Person("Roger Floger",
                "27",
                "@String/input",
            "https://media.gettyimages.com/photos/closeup-of-a-mans-head-profile-picture-id157192886")
            )

            pList.add(Person("Maria S. Akselsen",
                "45",
                "@String/input",
            "https://www.maximimages.com/stock-photo/beautiful-asian-woman-closeup-of-face-profile-MXI31426.jpg"
           )
            )

            return pList;
        }
    }
}