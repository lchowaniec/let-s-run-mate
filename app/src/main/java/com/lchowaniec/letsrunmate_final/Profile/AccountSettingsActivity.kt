package com.lchowaniec.letsrunmate_final.Profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.BottomNaviViewHelper
import com.lchowaniec.letsrunmate_final.utils.SectionsStatePagerAdapter
import kotlinx.android.synthetic.main.account_settings_bar_layout.*
import kotlinx.android.synthetic.main.account_settings_center_layout.*

class AccountSettingsActivity : AppCompatActivity() {
    private var ACTIVITY_NUM = 4
    private lateinit var pagerAdapter: SectionsStatePagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var mRelativeLayout: RelativeLayout
    private lateinit var bottomNaviBar:BottomNavigationView







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        val mAuth = FirebaseAuth.getInstance()

        // Top bar title change
        val mToolbar: Toolbar = findViewById(R.id.account_settings_appbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Settings\t"
        bottomNaviBar = findViewById(R.id.bottomNaviViewBar)
        setupBottomNavigationBar()
        setupSettingList()
        //ViewPager
        viewPager = findViewById(R.id.container)
        mRelativeLayout = findViewById(R.id.account_settings_allview)
        setupFragments()

        //back button listener
        account_settings_back.setOnClickListener {
            finish()
        }

    }
    private fun setupFragments(){
        pagerAdapter = SectionsStatePagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(EditProfileFragment(),getString(R.string.edit_profile_fragment))
        pagerAdapter.addFragment(SignOutFragment(),getString(R.string.sign_out_fragment))

    }
    private fun setViewPager(fragmentNumber: Int){
        mRelativeLayout.visibility = View.GONE
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = fragmentNumber


    }

    private fun setupBottomNavigationBar(){


        BottomNaviViewHelper().setupBottomNaviView(bottomNaviBar)
        BottomNaviViewHelper().enableNavigationBar(baseContext,bottomNaviBar)
        val menu: Menu = bottomNaviBar.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true




    }
    private fun setupSettingList() {
        val options = ArrayList<String>()
        options.add(getString(R.string.edit_profile_fragment))
        options.add(getString(R.string.sign_out_fragment))

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        accounts_settings_listview.adapter = adapter

        accounts_settings_listview.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, position: Int, id: Long ->
            setViewPager(position)
        }

    }

}





