package com.lchowaniec.letsrunmate_final.utils

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lchowaniec.letsrunmate_final.Chat.ChatActivity
import com.lchowaniec.letsrunmate_final.Feed.FeedActivity
import com.lchowaniec.letsrunmate_final.History.HistoryActivity
import com.lchowaniec.letsrunmate_final.MainActivity
import com.lchowaniec.letsrunmate_final.Profile.ProfileActivity
import com.lchowaniec.letsrunmate_final.R


open class BottomNaviViewHelper {
    private val TAG = "BottomNaviViewHelper"
    fun setupBottomNaviView(BottomNaviView: BottomNavigationView){
        BottomNaviView.itemIconTintList = null


    }
    fun enableNavigationBar(context: Context?, bottomNaviView: BottomNavigationView){
          bottomNaviView.setOnNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.home ->{
                    val intent1  =Intent(context, MainActivity::class.java)
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent1)


                    return@setOnNavigationItemSelectedListener true
                }
                R.id.feed ->{
                    val intent2 = Intent(context, FeedActivity::class.java)
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent2)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.history->{
                    val intent3 = Intent(context,HistoryActivity::class.java)
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent3)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.messages->{
                    val intent4 = Intent(context,ChatActivity::class.java)
                    intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent4)
                    return@setOnNavigationItemSelectedListener true

                }
                R.id.profile->{
                    val intent5 = Intent(context,
                        ProfileActivity::class.java)
                    intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent5)


                    return@setOnNavigationItemSelectedListener true
                }else->{
                    return@setOnNavigationItemSelectedListener false
            }
            }




        }

    }

}
