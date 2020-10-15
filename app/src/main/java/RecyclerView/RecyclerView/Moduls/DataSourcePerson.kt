package RecyclerView.RecyclerView.Moduls

class DataSourcePerson {

    companion object{
        fun createDataset(): ArrayList<Person> {
            val pList = ArrayList<Person>()

            pList.add(Person(
                "PA",
                "Henriette Pedersen",
                "24",
                "Fredrikkstad",
                "@String/input",
            "https://i.pinimg.com/originals/f7/60/87/f76087d518532f3a0c6b027d18e1212a.jpg")
            )

            pList.add(Person(
                "PB",
                "Chris Jack",
                "30",
                "Bø",
                "@String/input",
            "https://i.pinimg.com/originals/35/d2/eb/35d2ebe20571c03d8f257ae725a780aa.jpg")
            )

            pList.add(Person(
                "PC",
                "Roger Floger",
                "27",
                "Langesund",
                "@String/input",
            "https://media.gettyimages.com/photos/closeup-of-a-mans-head-profile-picture-id157192886")
            )

            pList.add(Person(
                "PD",
                "Maria S. Akselsen",
                "45",
                "Langesudn",
                "@String/input",
            "https://www.maximimages.com/stock-photo/beautiful-asian-woman-closeup-of-face-profile-MXI31426.jpg"
           )
            )

            return pList;
        }
    }
}