package RecyclerView.RecyclerView.Moduls

import androidx.lifecycle.MutableLiveData

interface DataCallbackSingleValue<E> {
    fun onValueRead(verdi : MutableLiveData<E>)
}