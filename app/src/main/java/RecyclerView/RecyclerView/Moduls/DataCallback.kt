package RecyclerView.RecyclerView.Moduls

import com.example.myapplication.viewmodels.EventViewModel

interface DataCallback<E> {
    fun onCallBack(liste: ArrayList<E>)

}