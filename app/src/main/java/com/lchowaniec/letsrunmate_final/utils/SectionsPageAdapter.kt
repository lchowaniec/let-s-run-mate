package com.lchowaniec.letsrunmate_final.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    var mFragmentList : ArrayList<Fragment> = ArrayList<Fragment>()


    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }
    fun addFragment(fragment: Fragment){
        mFragmentList.add(fragment)

    }

}