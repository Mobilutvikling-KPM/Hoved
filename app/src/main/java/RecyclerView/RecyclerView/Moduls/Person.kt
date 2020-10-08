package RecyclerView.RecyclerView.Moduls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Person (
    var personID: String,
    var brukernavn: String,
    var alder: String,
    var bio:String,
    var profilBilde: String
) : Parcelable {

}


