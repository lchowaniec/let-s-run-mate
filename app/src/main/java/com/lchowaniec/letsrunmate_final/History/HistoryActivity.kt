package com.lchowaniec.letsrunmate_final.History

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lchowaniec.letsrunmate_final.R

class HistoryActivity : AppCompatActivity() {
   //val fm: FragmentManager = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )

        replaceFragments("HistoryFragment")
/*
        val fragment = HistoryFragment()
        val transaction: FragmentTransaction =
            this.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.history_container, fragment)
        transaction.commit()*/


    }
    open fun replaceFragments(fragmentName:String){
        var fragment: Fragment? = null
       when(fragmentName){
           "HistoryFragment" -> fragment = HistoryFragment()
            "HistorySummaryFragment" -> fragment = HistorySummaryFragment()
           }


        val fragmentTransaction = supportFragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.history_container, fragment!!)
        fragmentTransaction.commit()


    }


}
