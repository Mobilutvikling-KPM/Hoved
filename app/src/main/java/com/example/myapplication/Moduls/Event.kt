package com.example.myapplication.Moduls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
    var eventID: String,
    var title: String,
    var body: String,
    var image: String,
    var dato: String,
    var klokke: String,
    var sted: String,
    var forfatter: String,
    var kategori: String,
    var antPåmeldte: String,
    var antKommentar: String,
    var viewType: Int
): Parcelable {

    constructor() : this("","", " ", " ", " ", " "," ", "",
    "", "0", "0",1){

    }
}