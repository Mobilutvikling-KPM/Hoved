package com.example.myapplication.viewmodels

import RecyclerView.RecyclerView.Moduls.Person
import androidx.lifecycle.MutableLiveData

interface KommentarPersonCallBack<E> {
    fun onPersonFind(verdi : MutableLiveData<E>)
}