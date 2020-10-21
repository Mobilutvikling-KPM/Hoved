package RecyclerView.RecyclerView.Moduls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Folg (
    var innloggetID: String,
    var person: Person
): Parcelable{
    constructor(): this("",Person("","","","","",""))
}