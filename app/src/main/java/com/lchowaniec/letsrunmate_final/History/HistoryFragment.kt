package com.lchowaniec.letsrunmate_final.History


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Models.Trophy
import com.lchowaniec.letsrunmate_final.Post.PostViewFragment
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.ActivityListAdapter
import com.lchowaniec.letsrunmate_final.utils.BottomNaviViewHelper
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.nostra13.universalimageloader.core.ImageLoader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class HistoryFragment : Fragment() {

    private var ACTIVITY_NUM = 2
    private lateinit var bottomNavigationView:BottomNavigationView
    private var mContext: Context? = null
    private lateinit var mToolbar:Toolbar
    private lateinit var mProgressBar:ProgressBar
    //firebase config
    //firebase config

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper

    //adapter
    private lateinit var mListView:ListView
    private lateinit var adapter:ActivityListAdapter
    private var arra: ArrayList<Activity> = ArrayList()
    //fields


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_history, container, false)
        mProgressBar = view!!.findViewById(R.id.history_progress_bar)
        mProgressBar.visibility = View.VISIBLE
        mContext = activity!!.applicationContext
        mToolbar = view.findViewById(R.id.history_appbar)
        arra = ArrayList()





        val myFont = ResourcesCompat.getFont(activity!!.applicationContext,R.font.bangers)
        val title = view.findViewById<TextView>(R.id.history_titlebar)
        title.typeface = myFont
        //fields
        mListView = view.findViewById(R.id.history_listview)
        adapter = ActivityListAdapter(activity!!.applicationContext,R.layout.history_adapter_listview_layout,arra)
        mListView.adapter = adapter




        setupFirebaseAuth()
        mProgressBar = view.findViewById(R.id.history_progress_bar)
        myRef.child(mAuth.currentUser!!.uid).addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                arra.add(p0.getValue(Activity::class.java)!!)
                adapter.notifyDataSetChanged()
                mProgressBar.visibility = View.GONE            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
               // for(snap:DataSnapshot in p0.children){
                  /*  val activity = Activity()
                     //val objectMap2 :Map<String, String>  =  snap.getValue() as (HashMap<String, String>)

                    val objectMap:HashMap<*,*> = snap.getValue()as HashMap<*, *>
                    activity.url_photo = objectMap.get(getString(R.string.firebase_url_photo)).toString()
                    activity.url = objectMap.get(getString(R.string.firebase_url)).toString()
                    activity.date = objectMap.get(getString(R.string.firebase_date)).toString()
                    activity.avgPace = objectMap.get(getString(R.string.firebase_avgPace)).toString()
                    activity.duration_time = objectMap.get(getString(R.string.firebase_duration_time)).toString()
                    activity.distance = objectMap.get(getString(R.string.firebase_distance)).toString()
                    activity.caption = objectMap.get(getString(R.string.firebase_caption)).toString()
                    activity.activity_id = objectMap.get(getString(R.string.firebase_activity_id)).toString()
                    activity.user_id = objectMap.get(getString(R.string.firebase_user_id)).toString()
                    activity.kcal = objectMap.get(getString(R.string.firebase_kcal)).toString().toInt()
                    val trophieList = ArrayList<Trophy>()
                    for(dSnap:DataSnapshot in snap
                        .child(getString(R.string.firebase_trophies)).children){
                        val trophy = Trophy()
                        trophy.user_id = dSnap.getValue(Trophy()::class.java)!!.user_id
                        trophieList.add(trophy)
                    }*/
                   // activity.trophies = trophieList
                    arra.add(p0.getValue(Activity::class.java)!!)
                    adapter.notifyDataSetChanged()
                    mProgressBar.visibility = View.GONE            }













            //}






            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        bottomNavigationView = view.findViewById(R.id.bottomNaviViewBar)
        setupBottomNavigationBar()
        mListView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newFragment = PostViewFragment()
                val bundle = Bundle()
                println(adapter.getItem(position))
                bundle.putSerializable("Activity",adapter.getItem(position))
                bundle.putInt("Activity_number",ACTIVITY_NUM)
                newFragment.arguments = bundle

                val transaction = activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.history_container,newFragment)
                    addToBackStack(null)

                }
                transaction.commit()
            }
        }

        return view
    }private fun setupBottomNavigationBar(){


        BottomNaviViewHelper().setupBottomNaviView(bottomNavigationView)
        BottomNaviViewHelper().enableNavigationBar(mContext,bottomNavigationView)
        val menu: Menu = bottomNavigationView.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true





    }
    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
         mFirebaseDatabase = FirebaseDatabase.getInstance()
         myRef = mFirebaseDatabase.reference.child(mContext!!.getString(R.string.firebase_users_activities))
         mFirebaseHelper = FirebaseHelper(activity!!.applicationContext)



        mAuthListener = FirebaseAuth.AuthStateListener {
            fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = firebaseAuth.currentUser

                if (user != null) {
                    //User is signed in
                    Log.d(ImageLoader.TAG, "Signed_in")

                } else {
                    // User is signed out
                    Log.d(ImageLoader.TAG, "Signed_out")
                }
                // ...
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }





        }


