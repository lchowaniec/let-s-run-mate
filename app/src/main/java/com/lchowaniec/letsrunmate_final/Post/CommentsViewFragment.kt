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

    //fields
    private lateinit var mImage: SquareImageView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var mUsername: TextView
    private lateinit var mCaption: TextView
    private lateinit var mBackLabel: TextView
    private lateinit var mTimesTamp: TextView
    private lateinit var mBackArrow: ImageView
    private lateinit var mOptions: ImageView
    private lateinit var mTrophyGold: ImageView
    private lateinit var mTropnyWhite: ImageView
    private lateinit var mProfileImage: ImageView
    private lateinit var mLikeDesc: TextView
    //vars
    private lateinit var mActivity: Activity
    private var mActivityNumber: Int = 0
    private lateinit var activityUser: String
    private lateinit var activityPhotoUrl: String
    private lateinit var mUserDetails: UserDetails
    private lateinit var mGesture: GestureDetector
    private lateinit var mGesture2: GestureDetector
    private var TrophiedByUser: Boolean = false
    lateinit var stringUsers: StringBuilder
    lateinit var mTrophyString: String

    lateinit var mTrophy: Trophy

    //FIREBASE CONFIG
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_comments_view, container, false)

        return view
    }
}


