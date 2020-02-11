package com.lchowaniec.letsrunmate_final.Profile



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Models.UserAllDetails
import com.lchowaniec.letsrunmate_final.R
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
    //FIREBASE CONFIG
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef:DatabaseReference
    private lateinit var mAuthListener:FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference
        bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNaviViewBar)
        mUsername = view.findViewById(R.id.profile_username)
        mActivities = view.findViewById(R.id.show_activities)
        mDistance = view.findViewById(R.id.show_distance)
        mFriends = view.findViewById(R.id.show_friends)


        progressBar = view.findViewById<ProgressBar>(R.id.profile_progress_bar)
        profile_menu = view.findViewById<ImageView>(R.id.profile_menu)
        profile_photo = view.findViewById(R.id.edit_profile_photo)
        progressBar.visibility= View.GONE
        mContext = activity


        mFirebaseHelper = FirebaseHelper(activity!!.baseContext)


        setupBottomNavigationBar()
        setupFirebaseAuth()
        profile_menu.setOnClickListener {
            val intent = Intent(mContext,AccountSettingsActivity::class.java)
            startActivity(intent)

        }
        return view




}


    private fun setProfileDetails(userAllDetails: UserAllDetails){

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

