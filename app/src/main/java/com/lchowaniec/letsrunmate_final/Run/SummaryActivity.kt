package com.lchowaniec.letsrunmate_final.Run

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.lchowaniec.letsrunmate_final.R


class SummaryActivity : FragmentActivity() {


    lateinit var mAuth:FirebaseAuth
     var key:String? =""

    override fun onCreate(savedInstanceState: Bundle?) {
        //Mapbox.getInstance(this,getString(R.string.mapboxkey))

        super.onCreate(savedInstanceState)
         key = intent.getStringExtra("key")
        setContentView(R.layout.activity_summary)
        val fm = supportFragmentManager
        val fragment = SummaryFragment()
        val bundle = Bundle()
        bundle.putString("key",key)
        fragment.arguments = bundle
        fm.beginTransaction().replace(R.id.summary_container,fragment).commit()


        mAuth = FirebaseAuth.getInstance()


    }


}
