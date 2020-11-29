package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.callback_interface.isLoading
import java.lang.IllegalArgumentException

/**
 *
 * @author Patrick S. Lorentzen - 151685
 *
 * ViewModelFactory som gjør at viewmodelene kan ta imot argumenter
 *  * @property type viewtype til recyclerview
 * @property id id til innlogget bruker
 * @property isLoading interface brukes til callback til view
 */
class ViewModelFactory(private var type: Int, private var id: String, private var callBack: isLoading?,
                       ): ViewModelProvider.Factory {


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