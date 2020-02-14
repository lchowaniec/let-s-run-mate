package com.lchowaniec.letsrunmate_final.Profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.BottomNaviViewHelper

class SearchActivity : AppCompatActivity() {
    private val mContext = SearchActivity().applicationContext
    private val ACTIVITY_NUM = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }
    private fun setupBottomNavigationBar(){


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNaviViewBar)
        BottomNaviViewHelper().setupBottomNaviView(bottomNavigationView)
        BottomNaviViewHelper().enableNavigationBar(mContext,bottomNavigationView)
        val menu: Menu = bottomNavigationView.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true




    }
}
