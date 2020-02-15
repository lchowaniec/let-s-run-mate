package com.lchowaniec.letsrunmate_final.Profile



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Models.UserAllDetails
import com.lchowaniec.letsrunmate_final.Post.PostViewFragment
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.ActivityListAdapter
import com.lchowaniec.letsrunmate_final.utils.BottomNaviViewHelper
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.lchowaniec.letsrunmate_final.utils.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoader.TAG
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private var ACTIVITY_NUM = 4
    private lateinit var mActivities:TextView
    private lateinit var mDistance:TextView
    private lateinit var mFriends:TextView
    private lateinit var mUsername:TextView
    private var mContext:Context? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var mToolbar:Toolbar
    private lateinit var profile_menu: ImageView
    private lateinit var profile_photo: CircleImageView
    private lateinit var progressBar:ProgressBar
    private var mDistanceCounter:Float = 0f
    private var mActivitiesCounter:Int = 0
    private var mFriendsFollowingCounter:Int = 0
    private var mFriendsFollowersCounter:Int = 0


    //FIREBASE CONFIG
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef:DatabaseReference
    private lateinit var mAuthListener:FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper
    //Adapter
    private var adapterList: ArrayList<Activity> = ArrayList()
    private lateinit var mListView: ListView
    private lateinit var adapter: ActivityListAdapter
    private lateinit var mProgressBarListView: ProgressBar




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference
        bottomNavigationView = view.findViewById(R.id.bottomNaviViewBar)
        mUsername = view.findViewById(R.id.profile_username)
        mActivities = view.findViewById(R.id.show_activities)
        mDistance = view.findViewById(R.id.show_distance)
        mFriends = view.findViewById(R.id.show_friends)


        mProgressBarListView = view.findViewById(R.id.profile_listview_progress_bar)
        progressBar = view.findViewById(R.id.profile_progress_bar)
        profile_menu = view.findViewById(R.id.profile_menu)
        profile_photo = view.findViewById(R.id.edit_profile_photo)
        progressBar.visibility= View.GONE
        mContext = activity
        adapterList = ArrayList()




        mFirebaseHelper = FirebaseHelper(activity!!.baseContext)


        setupBottomNavigationBar()
        setupFirebaseAuth()



        mListView = view.findViewById(R.id.profile_listview)
        adapter = ActivityListAdapter(activity!!.applicationContext,R.layout.history_adapter_listview_layout,adapterList)
        mListView.adapter =adapter


        profile_menu.setOnClickListener {
            val intent = Intent(mContext,AccountSettingsActivity::class.java)
            startActivity(intent)

        }

        mListView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newFragment = PostViewFragment()
                val bundle = Bundle()
                println(adapter.getItem(position))
                bundle.putSerializable("Activity",adapter.getItem(position))
                bundle.putInt("Activity_number",ACTIVITY_NUM)
                newFragment.arguments = bundle

                val transaction = activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.container_profile,newFragment)
                    addToBackStack(null)

                }
                transaction.commit()
            }
        }
        return view




}
    private fun friendsCounter(){
        mFriendsFollowingCounter = 0
        mFriendsFollowersCounter = 0
        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child(getString(R.string.firebase_following))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(ds in p0.children){
                    mFriendsFollowingCounter++
                }

            }
        })

        val ref2 = FirebaseDatabase.getInstance().reference
        val query2 = ref2.child(getString(R.string.firebase_followers))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        query2.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(ds in p0.children){
                    mFriendsFollowersCounter++
                }
                mFriends.text =
                    "$mFriendsFollowersCounter / $mFriendsFollowingCounter"
            }
        })

    }
    private fun distanceCounter(){
        mDistanceCounter = 0f
        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child(getString(R.string.firebase_users_activities))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(ds in p0.children){
                    mDistanceCounter += ds.getValue(Activity::class.java)!!.distance
                }
                mDistance.text = String.format("%.2f km", mDistanceCounter )


            }


        })


    }
    private fun activitiesCounter(){
        mActivitiesCounter = 0
        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child(getString(R.string.firebase_users_activities))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(ds in p0.children){
                    mActivitiesCounter++
                }
                mActivities.text = mActivitiesCounter.toString()
            }
        })

    }


    private fun setProfileDetails(userAllDetails: UserAllDetails){

        val user:User = userAllDetails.user
        val userDetails = userAllDetails.userDetails
        ImageLoader().setImage(userDetails.profile_photo,profile_photo,null,"")
        mUsername.text = user.username
        friendsCounter()
        activitiesCounter()
        distanceCounter()





    }
    private fun setupBottomNavigationBar(){


        BottomNaviViewHelper().setupBottomNaviView(bottomNavigationView)
        BottomNaviViewHelper().enableNavigationBar(mContext,bottomNavigationView)
        val menu: Menu = bottomNavigationView.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true




    }

    /* FIREBASE*/
    private fun setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance()
        val myRef2 = mFirebaseDatabase.reference.child(mContext!!.getString(R.string.firebase_users_activities))
        myRef2.child(mAuth.currentUser!!.uid).addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                adapterList.add(p0.getValue(Activity::class.java)!!)
                adapter.notifyDataSetChanged()
                mProgressBarListView.visibility = View.GONE             }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                adapterList.add(p0.getValue(Activity::class.java)!!)
                adapter.notifyDataSetChanged()
                mProgressBarListView.visibility = View.GONE                }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


        mAuthListener = FirebaseAuth.AuthStateListener {
            fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = firebaseAuth.currentUser

                if (user != null) {
                    //User is signed in
                    Log.d(TAG,"Signed_in")

                } else {
                    // User is signed out
                    Log.d(TAG,"Signed_out")
                }
                // ...
            }
            myRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    setProfileDetails( mFirebaseHelper.getAccountsSettings(dataSnapshot))



                }

            })



        }
    }


    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }


    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

}

