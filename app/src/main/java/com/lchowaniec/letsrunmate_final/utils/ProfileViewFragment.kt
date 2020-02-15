package com.lchowaniec.letsrunmate_final.utils



import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Models.UserAllDetails
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.Post.PostViewFragment
import com.lchowaniec.letsrunmate_final.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class ProfileViewFragment : Fragment() {
    private var ACTIVITY_NUM = 4
    private lateinit var mActivities:TextView
    private lateinit var mDistance:TextView
    private lateinit var mFriends:TextView
    private lateinit var mUsername:TextView
    private var mContext:Context? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var profile_menu: ImageView
    private lateinit var profile_photo: CircleImageView
    private lateinit var progressBar:ProgressBar
    private lateinit var mUser:User
    private lateinit var addFriend:ImageView
    private lateinit var deleteFriend:ImageView
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
        val view: View = inflater.inflate(R.layout.fragment_view_profile, container, false)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference
        bottomNavigationView = view.findViewById(R.id.bottomNaviViewBar)
        mUsername = view.findViewById(R.id.profile_username)
        mActivities = view.findViewById(R.id.show_activities)
        mDistance = view.findViewById(R.id.show_distance)
        mFriends = view.findViewById(R.id.show_friends)
        addFriend = view.findViewById(R.id.view_profile_add)
        deleteFriend = view.findViewById(R.id.view_profile_delete)


        mProgressBarListView = view.findViewById(R.id.profile_listview_progress_bar)
        progressBar = view.findViewById(R.id.profile_progress_bar)
        profile_menu = view.findViewById(R.id.profile_menu)
        profile_photo = view.findViewById(R.id.edit_profile_photo)
        progressBar.visibility= View.GONE
        mContext = activity
        adapterList = ArrayList()




        mFirebaseHelper = FirebaseHelper(activity!!.baseContext)


        try{
            mUser = getUserBundle()!!
            println(mUser.user_id)
            println("HALOHALOAHLO")
            initUser()

        }catch (e:NullPointerException){
            Toast.makeText(mContext,"Something gone wrong :/",Toast.LENGTH_SHORT).show()
            activity!!.supportFragmentManager.popBackStack()
            Log.d(TAG,e.message)
        }
        isFriends()

        setupBottomNavigationBar()
        setupFirebaseAuth()


        mListView = view.findViewById(R.id.profile_listview)
        adapter = ActivityListAdapter(activity!!.applicationContext,R.layout.history_adapter_listview_layout,adapterList)
        mListView.adapter =adapter
        addFriend.setOnClickListener{
            FirebaseDatabase.getInstance().reference
                .child(getString(R.string.firebase_following))
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(mUser.user_id)
                .child(getString(R.string.user_id))
                .setValue(mUser.user_id)
            FirebaseDatabase.getInstance().reference
                .child(getString(R.string.firebase_followers))
                .child(mUser.user_id)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(getString(R.string.user_id))
                .setValue(FirebaseAuth.getInstance().currentUser!!.uid)





            setUnFriendIcon()
        }
        deleteFriend.setOnClickListener{
            FirebaseDatabase.getInstance().reference
                .child(getString(R.string.firebase_following))
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(mUser.user_id)
                .removeValue()
            FirebaseDatabase.getInstance().reference
                .child(getString(R.string.firebase_followers))
                .child(mUser.user_id)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()

            setFriendIcon()
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
    private fun isFriends(){
        setFriendIcon()
        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child(getString(R.string.firebase_following))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .orderByChild(getString(R.string.firebase_user_id)).equalTo(mUser.user_id)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(ds in p0.children){
                    setUnFriendIcon()
                }
            }
        })

    }
    private fun setFriendIcon(){
        addFriend.visibility = View.VISIBLE
        deleteFriend.visibility = View.GONE

    }
    private fun setUnFriendIcon(){
        addFriend.visibility = View.GONE
        deleteFriend.visibility = View.VISIBLE

    }
    private fun getUserBundle(): User? {
        val bundle = this.arguments
        if(bundle!=null){
            return bundle.getParcelable(getString(R.string.user))


        }else{
            return null
        }

    }
    private fun initUser(){
        Log.d(TAG,"A TU JESTEM ?"+mUser.user_id)
        //get data about user from database
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference.child(getString(R.string.firebase_user_details))
            .orderByChild(getString(R.string.firebase_user_id))
            .equalTo(mUser.user_id)
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(ds in p0.children){
                    val wholeUser: UserAllDetails = UserAllDetails()
                    wholeUser.user = mUser
                    wholeUser.userDetails= ds.getValue(UserDetails::class.java)!!
                    setProfileDetails(wholeUser)
                    println(wholeUser.user.username)
                    println(wholeUser.userDetails.profile_photo)


                }
            }
        })



    }


    private fun setProfileDetails(userAllDetails: UserAllDetails){
        Log.d(TAG,"JA tu nie wchodze w ogole")

        val user:User = userAllDetails.user
        val userDetails = userAllDetails.userDetails
        ImageLoader().setImage(userDetails.profile_photo,profile_photo,null,"")
        mUsername.text = user.username
        mActivities.text = userDetails.activities.toString()
        mDistance.text = userDetails.distance.toString()
        mFriends.text = userDetails.friends.toString()




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
        myRef2.child(mUser.user_id).addChildEventListener(object: ChildEventListener{
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





    }


    override fun onStart() {
        super.onStart()
    }


    override fun onStop() {
        super.onStop()

    }

}

