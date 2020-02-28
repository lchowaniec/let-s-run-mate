package com.lchowaniec.letsrunmate_final.Feed


import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.BottomNaviViewHelper
import com.lchowaniec.letsrunmate_final.utils.FeedListAdapter
import kotlinx.android.synthetic.main.feed_bar_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FeedFragment : Fragment() {



    //vars
    private var ACTIVITY_NUM = 1
    private var mActivitiesList :ArrayList<Activity> = ArrayList()
    private lateinit var mFollowingList : ArrayList<String>
    private lateinit var mListView: ListView
    private lateinit var mFeedListAdapter: FeedListAdapter
    private lateinit var mContext: Context
    private lateinit var mToolbar:androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigationView:BottomNavigationView
    private var mCounter:Int = 0
    private lateinit var mPartActivitiesList:ArrayList<Activity>



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_feed, container, false)
        mListView = view.findViewById(R.id.listViewFeed)
        mToolbar = view.findViewById(R.id.feed_appbar)
        mToolbar.feed_title.text = getString(R.string.let_s_run_mate)
        mFollowingList = ArrayList()
        mContext = activity!!.applicationContext
        bottomNavigationView = view!!.findViewById(R.id.feed_bottom)
        getFollowing()
        setupBottomNavigationBar()

        return view
    }

    private fun getActivities(){
        val reference = FirebaseDatabase.getInstance().reference
        for(i  in 0..mFollowingList.size -1){
            println(i)
            Log.d(TAG,"MOJE ACTIVITY LIST: "+mActivitiesList)
            var many = 0
            val query = reference
                .child(mContext.getString(R.string.firebase_users_activities))
                .child(mFollowingList.get(i))
                .orderByChild(mContext.getString(R.string.user_id))
                .equalTo(mFollowingList.get(i))

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        mActivitiesList.add(ds.getValue(Activity::class.java)!!)
                        println("JESTEM JESTEM $mActivitiesList")
                        many++


                    }
                    displayActivities()


                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        }


        }




    private fun getFollowing(){
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
            .child(mContext.getString(R.string.firebase_following))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    mFollowingList.add(ds.child(mContext.getString(R.string.firebase_user_id)).getValue().toString())
                }
                mFollowingList.add(FirebaseAuth.getInstance().currentUser!!.uid)
                getActivities()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }
    private fun displayActivities(){
        mPartActivitiesList = ArrayList()
        println("ACTIVITY LIST SIZE: "+ mActivitiesList.size)
        if(mActivitiesList!=null){
            try {
                Collections.sort(mActivitiesList, object : Comparator<Activity> {
                    override fun compare(o1: Activity?, o2: Activity?): Int {
                        return o2!!.date.compareTo(o1!!.date)
                    }
                })
                var iter = mActivitiesList.size
                if (iter > 10) {
                    iter = 10
                }
                println("TUTAJ ITER SPRAWDZAM "+ iter)
                mCounter = 0
                for (i in 0..iter-1) {
                    mPartActivitiesList.add(mActivitiesList.get(i))
                    mCounter++
                    Log.d(TAG,"mPartActivitiesList: "+ mPartActivitiesList)
                }

                mFeedListAdapter =
                    FeedListAdapter(activity!!, R.layout.feed_list_item, mPartActivitiesList)
                mListView.adapter = mFeedListAdapter
                mFeedListAdapter.notifyDataSetChanged()
            }catch (e: IndexOutOfBoundsException){
                Log.d(TAG,"IndexOutOfBounds:"+e.message)
            }

        }

    }
    open fun displayMoreActivities(){
        println("ACTIVITY LIST SIZE W MORE: "+ mActivitiesList.size + " " + mCounter)
        if(mActivitiesList.size > mCounter && mActivitiesList.size >0){
            println("ACTIVITY LIST SIZE W MORE: "+ mActivitiesList.size)
            val iter:Int
            if(mActivitiesList.size> (mCounter+10)){
                iter = 10
            }else{
                iter = mActivitiesList.size - mCounter
            }

            for(i in mCounter..mCounter+iter-1){
                mPartActivitiesList.add(mActivitiesList.get(i))
            }
            mCounter += iter
            mFeedListAdapter.notifyDataSetChanged()
        }
    }
    private fun setupBottomNavigationBar(){


        BottomNaviViewHelper().setupBottomNaviView(bottomNavigationView)
        BottomNaviViewHelper().enableNavigationBar(mContext,bottomNavigationView)
        val menu: Menu = bottomNavigationView.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true




    }


}
