package RecyclerView.RecyclerView.Moduls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Folg (
    var innloggetID: String,
    var personID: String
): Parcelable{
    constructor(): this("","")
}