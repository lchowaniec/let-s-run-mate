package com.lchowaniec.letsrunmate_final.Run

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.SectionsPageAdapter


class RunActivityController : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_controller)

        setupViewPager()


    }
    private fun setupViewPager(){
        val adapter = SectionsPageAdapter(supportFragmentManager)
        adapter.addFragment(RunFragment())
        adapter.addFragment(MapFragment())
        val viewPager:ViewPager = findViewById(R.id.viewPager)
        viewPager.adapter = adapter

    }


}





