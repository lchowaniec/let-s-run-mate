package com.lchowaniec.letsrunmate_final.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class SectionsStatePagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private var mFragmentList : ArrayList<Fragment> = ArrayList()
    private var mFragments:HashMap<Fragment,Int> = HashMap()
    private var mFragmentNumbers: HashMap<String,Int> = HashMap()
    private var mFragmentNames: HashMap<Int,String> = HashMap()





    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size

    }
     fun addFragment(fragment: Fragment,fragmentName:String){
        mFragmentList.add(fragment)
         mFragments[fragment] = mFragmentList.size-1
         mFragmentNumbers[fragmentName] = mFragmentList.size-1
         mFragmentNames[mFragmentList.size-1] = fragmentName
    }
    private fun getFragmentNumber(fragmentName: String): Int? {
        if(mFragmentNumbers.containsKey(fragmentName)){
            return mFragmentNumbers[fragmentName]
        }else{
            return null
        }
    }
    fun getFragmentNumber(fragment:Fragment):Int? {
        if(mFragments.containsKey(fragment)){
            return mFragments[fragment]
        }else{
            return null
        }
    }
    fun getFragmentName(fragmentNumber:Int):String? {
        if(mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames[fragmentNumber]
        }else{
            return null
        }
    }


}