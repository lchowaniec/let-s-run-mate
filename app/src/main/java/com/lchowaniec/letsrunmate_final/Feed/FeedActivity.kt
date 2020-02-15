package com.lchowaniec.letsrunmate_final.Feed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.lchowaniec.letsrunmate_final.R

class FeedActivity : AppCompatActivity() {
    private val ACTIVITY_NUM = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
        val fragment = SearchFragment()
        val transaction: FragmentTransaction =
            this.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.search_container, fragment)
        transaction.commit()
}
}
