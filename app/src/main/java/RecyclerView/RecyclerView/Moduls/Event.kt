package RecyclerView.RecyclerView.Moduls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
    var title: String,
    var body: String,
    var image: String,
    var dato: String,
    var sted: String,
    var antPåmeldte: String,
    var antKommentar: String,
    var viewType: Int
): Parcelable {

}