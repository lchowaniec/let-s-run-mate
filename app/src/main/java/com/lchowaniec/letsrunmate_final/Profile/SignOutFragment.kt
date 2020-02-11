package com.lchowaniec.letsrunmate_final.Profile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.lchowaniec.letsrunmate_final.MainActivity
import com.lchowaniec.letsrunmate_final.R

/**
 * A simple [Fragment] subclass.
 */
class SignOutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_sign_out, container, false)
        val textview = view.findViewById<TextView>(R.id.sign_out_progressbartext)
        val progressbar = view.findViewById<ProgressBar>(R.id.sign_out_progressbar)
        val button = view.findViewById<Button>(R.id.sign_out_button)
        textview.visibility = View.GONE
        progressbar.visibility = View.GONE
        button.setOnClickListener {
            textview.visibility = View.VISIBLE
            progressbar.visibility = View.VISIBLE
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity,MainActivity::class.java)
            startActivity(intent)
        }





        return view
    }


}
