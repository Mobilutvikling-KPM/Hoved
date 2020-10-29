package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

/*
* Hjelper viewmodel med å ta imot argumenter
 */
class ViewModelFactory(private var type: Int, private var id: String, private var callBack: isLoading?): ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EventViewModel::class.java)){
            return EventViewModel(type,"",callBack) as T
        }
        else if(modelClass.isAssignableFrom(PersonViewModel::class.java)){
            return PersonViewModel(type,"", callBack) as T
        }
        else if(modelClass.isAssignableFrom(KommentarViewModel::class.java)){
            return KommentarViewModel(type, id) as T
        }
        throw IllegalArgumentException("ViewModel not found")
    }
}