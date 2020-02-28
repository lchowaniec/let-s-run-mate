package com.lchowaniec.letsrunmate_final.History


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.ActivityListAdapter
import com.lchowaniec.letsrunmate_final.utils.BottomNaviViewHelper
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.nostra13.universalimageloader.core.ImageLoader
import java.util.*
import kotlin.collections.ArrayList

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
        val view =  inflater.inflate(R.layout.fragment_history, container, false)
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

        Collections.sort(arra, object : Comparator<Activity> {
            override fun compare(o1: Activity?, o2: Activity?): Int {
                return o2!!.date.compareTo(o1!!.date)
            }
        })
        adapter = ActivityListAdapter(activity!!.applicationContext,R.layout.history_adapter_listview_layout,arra)
        mListView.adapter = adapter





        setupFirebaseAuth()
        mProgressBar = view.findViewById(R.id.history_progress_bar)
        myRef.child(mAuth.currentUser!!.uid).addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                mProgressBar.visibility = View.GONE
                Toast.makeText(activity!!.applicationContext,"No activities to show", Toast.LENGTH_LONG).show()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                arra.add(0,p0.getValue(Activity::class.java)!!)
                adapter.notifyDataSetChanged()
                mProgressBar.visibility = View.GONE            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    arra.add(0,p0.getValue(Activity::class.java)!!)
                    adapter.notifyDataSetChanged()
                    mProgressBar.visibility = View.GONE            }













            //}






        override fun onChildRemoved(p0: DataSnapshot) {
        }
    })

        bottomNavigationView = view.findViewById(R.id.bottomNaviViewBar)
        setupBottomNavigationBar()
        mListView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newFragment = HistorySummaryFragment()
                val bundle = Bundle()
                println(adapter.getItem(position))
                bundle.putSerializable("Activity",adapter.getItem(position))
                bundle.putInt("Activity_number",ACTIVITY_NUM)
                newFragment.arguments = bundle

                val transaction = activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(R.anim.enter_right_left,R.anim.exit_right_left,R.anim.enter_left_right,R.anim.exit_left_right)
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



