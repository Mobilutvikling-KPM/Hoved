package com.example.myapplication.fragments.profil

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.myapplication.R


/**
 * A simple [Fragment] subclass.
 * Use the [RedigerProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedigerProfilFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rediger_profil, container, false)

    }
}