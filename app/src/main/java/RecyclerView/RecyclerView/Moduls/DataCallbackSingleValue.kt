package RecyclerView.RecyclerView.Moduls

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface DataCallbackSingleValue<E> {

    fun onValueRead(verdi : MutableLiveData<E>)
    fun onValueReadInnlogget(verdi: MutableLiveData<E>, bruker:FirebaseUser)
}