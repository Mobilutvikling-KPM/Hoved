package com.example.myapplication.callback_interface

import androidx.lifecycle.MutableLiveData

interface KommentarPersonCallBack<E> {
    fun onPersonFind(verdi : MutableLiveData<E>)
}