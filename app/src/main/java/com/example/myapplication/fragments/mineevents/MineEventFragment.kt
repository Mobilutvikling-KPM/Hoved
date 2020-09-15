package com.example.myapplication.fragments.mineevents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MineEventFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViews()
        }
        return inflater.inflate(R.layout.fragment_mine_event, container, false)
    }

}