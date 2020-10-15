package com.example.myapplication.fragments.profil

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_profil.view.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ProfilFragment : Fragment() {

    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_profil, container, false)
        view.bli_venn.visibility = View.GONE
        view.redigerKnapp.setOnClickListener() {
            navController!!.navigate(R.id.action_profilFragment2_to_redigerProfilFragment)
        }
        view.LOLKNAPP.setOnClickListener() {
            navController!!.navigate(R.id.action_profilFragment2_to_loginFragment2)
        }
        view.slettKnapp.setOnClickListener() {
            showDeleteDialog()
        }
        return view

    }
    private fun showDeleteDialog() {
        AlertDialog.Builder(context)
            .setTitle("Delete entry")
            .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, which ->
                // Continue with delete operation
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) //referanse til navGraph

    }
    /*
    fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.container, redigerProfilFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }*/
}