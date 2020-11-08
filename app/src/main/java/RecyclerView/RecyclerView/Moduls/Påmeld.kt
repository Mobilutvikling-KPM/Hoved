package RecyclerView.RecyclerView.Moduls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Påmeld (
    var innloggetID: String,
    var eventID: String
): Parcelable{
    constructor(): this("","")
}