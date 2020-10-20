package RecyclerView.RecyclerView.Moduls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

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