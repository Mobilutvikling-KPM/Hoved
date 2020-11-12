package RecyclerView.RecyclerView.Moduls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kommentar(
    var eventID: String,
    var person: Person,
    var date: String,
    var kommentarTekst: String
) : Parcelable {
    constructor(): this("",Person("","","","","",""), "", ""){

    }
}