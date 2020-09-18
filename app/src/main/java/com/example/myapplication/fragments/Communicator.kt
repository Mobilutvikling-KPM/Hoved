package com.example.myapplication.fragments

interface Communicator {

    fun sendDataKomm(tittel: String,
                     beskrivelse: String,
                     image: String,
                     dato: String,
                     sted: String,
                    antPåmeldte: String,
                    antKommentar: String)
}