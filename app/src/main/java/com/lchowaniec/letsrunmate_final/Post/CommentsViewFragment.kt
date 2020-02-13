package com.lchowaniec.letsrunmate_final.Post


import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.lchowaniec.letsrunmate_final.utils.SquareImageView
import com.lchowaniec.letsrunmate_final.utils.Trophy




/**
 * A simple [Fragment] subclass.
 */
class CommentsViewFragment : Fragment() {

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


