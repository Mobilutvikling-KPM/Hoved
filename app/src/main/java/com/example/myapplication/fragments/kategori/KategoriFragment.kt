package com.example.myapplication.fragments.kategori

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_kategori.*

/**
 * A simple [Fragment] subclass.
 */
class KategoriFragment(private val callbackListener: CallbackListener) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.fragment_kategori, container, false)
    }
    override fun getTheme(): Int {
        return R.style.DialogTheme
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            //send back data to PARENT fragment using callback
            callbackListener.onDataReceived(editText.text.toString())
            // Now dismiss the fragment
            dismiss()
        }

    }
    interface CallbackListener {
        fun onDataReceived(data: String)
    }

}