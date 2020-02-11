package com.lchowaniec.letsrunmate_final.Post


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.*
import java.lang.NullPointerException
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.view.MotionEvent
import android.view.GestureDetector
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.lchowaniec.letsrunmate_final.Models.User
import org.w3c.dom.Text
import com.google.firebase.database.DatabaseError
import java.nio.file.Files.exists
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference




/**
 * A simple [Fragment] subclass.
 */
class CommentsViewFragment() : Fragment() {
    init {
        super.onStart()
        arguments = Bundle()
    }
    //fields
    private lateinit var mImage: SquareImageView
    private lateinit var bottomNavigationView:BottomNavigationView
    private lateinit var mUsername:TextView
    private lateinit var mCaption:TextView
    private lateinit var mBackLabel:TextView
    private lateinit var mTimesTamp:TextView
    private lateinit var mBackArrow:ImageView
    private lateinit var mOptions:ImageView
    private lateinit var mTrophyGold:ImageView
    private lateinit var mTropnyWhite:ImageView
    private lateinit var mProfileImage:ImageView
    private lateinit var mLikeDesc:TextView
    //vars
    private lateinit var mActivity:Activity
    private var mActivityNumber:Int =0
    private lateinit var activityUser:String
    private lateinit var activityPhotoUrl:String
    private lateinit var mUserDetails: UserDetails
    private lateinit var mGesture:GestureDetector
    private lateinit var mGesture2:GestureDetector
    private var TrophiedByUser: Boolean = false
    lateinit var stringUsers:StringBuilder
    lateinit var mTrophyString:String

    lateinit var mTrophy: Trophy

    //FIREBASE CONFIG
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View =  inflater.inflate(R.layout.fragment_post_view, container, false)
        mImage = view.findViewById(R.id.post_image)
        bottomNavigationView = view.findViewById(R.id.bottomNaviViewBar)
        mBackArrow = view.findViewById(R.id.imageBack)
        mBackLabel = view.findViewById(R.id.post_titleBar)
        mCaption = view.findViewById(R.id.post_caption)
        mUsername = view.findViewById(R.id.post_username)
        mTimesTamp = view.findViewById(R.id.post_time_ago)
        mOptions = view.findViewById(R.id.post_options_image)
        mTrophyGold = view.findViewById(R.id.post_trophy_clicked)
        mTropnyWhite = view.findViewById(R.id.post_trophy_unclicked)
        mProfileImage = view.findViewById(R.id.post_profile_photo)
        mLikeDesc = view.findViewById(R.id.post_like_desc)


        mTrophy = Trophy(mTropnyWhite,mTrophyGold)
        mGesture = GestureDetector(activity!!.applicationContext,GestureListener())
        mGesture2 = GestureDetector(activity!!.applicationContext,GestureListener2())



    try{
        mActivity = getAc()!!
        ImageLoader().setImage(mActivity.url_photo,mImage,null,"")
        mActivityNumber = getNumber()!!
        getActivityUserDetails()
        getTrophyString()
    }catch (e:NullPointerException){
        Log.e(TAG,"OnCreateView:NullPointerException" +e.message)
    }
        setupFirebaseAuth()
        setupBottomNavigationBar()
      //  trophyFunction()
      //  imageFunction()
        return view
    }
  /*  fun imageFunction(){
        mImage.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return mGesture2.onTouchEvent(event)
            }

        })
    }
    fun trophyFunction(){
        mTrophyGold.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return mGesture.onTouchEvent(event)            }

        })
        mTropnyWhite.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return mGesture.onTouchEvent(event)
            }


        })

    }*/

    inner class GestureListener2 : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {

            Log.d(TAG, "onDoubleTap: double tap detected.")

            val reference = FirebaseDatabase.getInstance().reference
            val query = reference
                .child(getString(R.string.firebase_activities))
                .child(mActivity.activity_id)
                .child(getString(R.string.firebase_trophies))
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (singleSnapshot in dataSnapshot.children) {

                        val keyID = singleSnapshot.key

                        //case1: Then user already liked the photo
                        if (TrophiedByUser && singleSnapshot.getValue(com.lchowaniec.letsrunmate_final.Models.Trophy::class.java!!)!!.user_id
                                .equals(FirebaseAuth.getInstance().currentUser!!.uid)
                        ) {

                            myRef.child(getString(R.string.firebase_activities))
                                .child(mActivity.activity_id)
                                .child(getString(R.string.firebase_trophies))
                                .child(keyID!!)
                                .removeValue()
                            ///
                            myRef.child(getString(R.string.firebase_users_activities))
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(mActivity.activity_id)
                                .child(getString(R.string.firebase_trophies))
                                .child(keyID)
                                .removeValue()

                            mTrophy.throphing()
                            getTrophyString()
                        } else if (!TrophiedByUser) {
                            //add new like
                            addNewLike()
                            break
                        }//case2: The user has not liked the photo
                    }
                    if (!dataSnapshot.exists()) {
                        //add new like
                        addNewLike()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            return true
        }
    }


    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.d(TAG, "onDoubleTap: double tap detected.")

            val reference = FirebaseDatabase.getInstance().reference
            val query = reference
                .child(getString(R.string.firebase_activities))
                .child(mActivity.activity_id)
                .child(getString(R.string.firebase_trophies))
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (singleSnapshot in dataSnapshot.children) {

                        val keyID = singleSnapshot.key

                        //case1: Then user already liked the photo
                        if (TrophiedByUser && singleSnapshot.getValue(com.lchowaniec.letsrunmate_final.Models.Trophy::class.java!!)!!.user_id
                                .equals(FirebaseAuth.getInstance().currentUser!!.uid)
                        ) {

                            myRef.child(getString(R.string.firebase_activities))
                                .child(mActivity.activity_id)
                                .child(getString(R.string.firebase_trophies))
                                .child(keyID!!)
                                .removeValue()
                            ///
                            myRef.child(getString(R.string.firebase_users_activities))
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(mActivity.activity_id)
                                .child(getString(R.string.firebase_trophies))
                                .child(keyID)
                                .removeValue()

                            mTrophy.throphing()
                            getTrophyString()
                        } else if (!TrophiedByUser) {
                            //add new like
                            addNewLike()
                            break
                        }//case2: The user has not liked the photo
                    }
                    if (!dataSnapshot.exists()) {
                        //add new like
                        addNewLike()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            return true
        }
    }



    private fun addNewLike(){
        val trophyID = myRef.push().key
        val trophy = com.lchowaniec.letsrunmate_final.Models.Trophy()
        trophy.user_id = FirebaseAuth.getInstance().currentUser!!.uid
        myRef.child(getString(R.string.firebase_activities))
            .child(mActivity.activity_id)
            .child(getString(R.string.firebase_trophies))
            .child(trophyID!!)
            .setValue(trophy)
        myRef.child(getString(R.string.firebase_users_activities))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(mActivity.activity_id)
            .child(getString(R.string.firebase_trophies))
            .child(trophyID!!)
            .setValue(trophy)

        mTrophy.throphing()
        getTrophyString()


    }
    private fun getTrophyString(){
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
            .child(getString(R.string.firebase_activities))
            .child(mActivity.activity_id)
            .child(getString(R.string.firebase_trophies))
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                stringUsers = StringBuilder()
                for (snap in p0.children){




                    val reference = FirebaseDatabase.getInstance().reference
                    val query = reference
                        .child(getString(R.string.firebase_users))
                        .orderByChild(getString(R.string.user_id))
                        .equalTo(snap.getValue(com.lchowaniec.letsrunmate_final.Models.Trophy()::class.java)!!.user_id)
                    query.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            for (snap2 in p0.children){
                                Log.d(TAG,"TROPHIES FOUND")
                                stringUsers.append(snap2.getValue(User()::class.java)!!.username)
                                stringUsers.append(",")

                            }
                            val splitted = stringUsers.toString().split(",")
                            TrophiedByUser = stringUsers.toString().contains(mUserDetails.username+",")
                            val ammount = splitted.lastIndex
                            print(ammount)
                            if(ammount ==1 ){
                                mTrophyString = "Liked by " + splitted[0]

                            }else if(ammount ==2 ){
                                mTrophyString = "Liked by " + splitted[0] + " and "+splitted[1]
                            }else if(ammount ==3 ){
                                mTrophyString = "Liked by " + splitted[0] +", "+splitted[1] +" and "+splitted[2]

                            }else if(ammount ==4 ){
                                mTrophyString = "Liked by " + splitted[0] +", "+splitted[1] +", "+splitted[2]+", "+splitted[1] +" and"+splitted[3]

                            }else if(ammount>4){
                                mTrophyString = "Liked by " + splitted[0] +", "+splitted[1] +", "+splitted[2]+", "+splitted[1] +" and"+ (splitted.size-3) + " others"

                            }
                            setupWidgets()


                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    })






                }
                if (!p0.exists()){
                    mTrophyString = ""
                    TrophiedByUser = false
                    setupWidgets()

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })


    }




    private fun getAc(): Activity? {
        Log.d(TAG,"get photo from bundle")
        val bundle = this.arguments
        if (bundle!=null){
            return bundle.getParcelable<Activity>("Activity")
        }else{
            return null
        }

    }private fun getNumber(): Int? {
        Log.d(TAG,"get photo from bundle")
        val bundle = this.arguments
        if (bundle!=null){
            return bundle.getInt("Activity_number")
        }else{
            return 0
        }

    }
    private fun getActivityUserDetails(){
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
            .child(getString(R.string.firebase_user_details))
            .orderByChild(getString(R.string.user_id))
            .equalTo(mActivity.user_id)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (snap in p0.children){
                    mUserDetails = snap.getValue(UserDetails::class.java)!!

                }
                //setupWidgets()
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })


    }
    private fun getTimestampDiff():String{
        var difference = ""
        val c = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd,HH:mm:ss",Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        val timestamp: Date
        val today = c.time
        format.format(today)
        val activity_date = mActivity.date

        try{
            timestamp = format.parse(activity_date)!!
            difference = Math.round(((today.time.toDouble() - timestamp.time)/1000/60/60/24)).toString()
            println(difference)
           // difference = getString(Math.round(((today.time.toDouble() - timestamp.time)/1000/60/60/24)).toInt())

        }catch (e:ParseException){
            difference = "0"
        }
        return difference

    }
    private fun setupWidgets(){
        val timeDifference = getTimestampDiff()
        if(!timeDifference.equals("0")){
            mTimesTamp.text = timeDifference + getString(R.string.days_ago)
        }else{
            mTimesTamp.text = "TODAY"
        }
        ImageLoader().setImage(mUserDetails.profile_photo,mProfileImage,null,"")
        mUsername.text = mUserDetails.username
        mLikeDesc.text = mTrophyString
        mCaption.text = mActivity.caption
        if(TrophiedByUser){
            mTropnyWhite.visibility = View.GONE
            mTrophyGold.visibility = View.VISIBLE
            mTrophyGold.setOnTouchListener(object: View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    Log.d(TAG,"OnTouch TrophyGold")
                    return mGesture2.onTouchEvent(event)            }

            })
        }else{
            mTropnyWhite.visibility = View.VISIBLE
            mTrophyGold.visibility = View.GONE
            mTropnyWhite.setOnTouchListener(object : View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    Log.d(TAG,"OnTouch TrophyWhite")

                    return mGesture2.onTouchEvent(event)
                }


            })
            mImage.setOnTouchListener(object:View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return mGesture.onTouchEvent(event)

                }

            })

        }

    }
    private fun setupBottomNavigationBar(){


        BottomNaviViewHelper().setupBottomNaviView(bottomNavigationView)
        BottomNaviViewHelper().enableNavigationBar(activity!!.applicationContext,bottomNavigationView)
        val menu: Menu = bottomNavigationView.menu
        val menuItem: MenuItem = menu.getItem(mActivityNumber)
        menuItem.isChecked = true




    }
    /* FIREBASE*/
    private fun setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference
        mFirebaseHelper = FirebaseHelper(activity!!.applicationContext)

        mAuthListener = FirebaseAuth.AuthStateListener {
            fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = firebaseAuth.currentUser

                if (user != null) {
                    //User is signed in

                } else {
                    // User is signed out
                }
                // ...
            }




        }
    }


}
