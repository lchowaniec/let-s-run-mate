package com.lchowaniec.letsrunmate_final.Profile


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Models.UserAllDetails
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.lchowaniec.letsrunmate_final.utils.ImageLoader

/**
 * A simple [Fragment] subclass.
 */
class EditProfileFragment : Fragment() {
    private val TAG = "EditProfileFragment"
    //Edit fields
    private lateinit var mProfilePhoto: ImageView
    private lateinit var mUsername: TextView
    private lateinit var mEmail:TextView
    private lateinit var mDescription:TextView
    private lateinit var userID:String
    private lateinit var mUserAllDetails: UserAllDetails

    //FIREBASE CONFIG
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper

    //buttons
    private lateinit var btn_success:ImageView


    private var mContext: Context? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference
        mUsername = view.findViewById(R.id.edit_profile_username_edittext)
        mDescription  = view.findViewById(R.id.edit_profile_description_edittext)
        mEmail = view.findViewById(R.id.edit_profile_email_edittext)
        mContext = activity
        mFirebaseHelper = FirebaseHelper(activity!!.baseContext)
        setupFirebaseAuth()
        btn_success = view.findViewById(R.id.edit_profile_succes_btn)
        mProfilePhoto= view.findViewById(R.id.edit_profile_photo)
        mUserAllDetails = UserAllDetails()
        val backbtn = view.findViewById<ImageView>(R.id.edit_profile_back_btn)
        backbtn.setOnClickListener {
            activity?.finish()


        }
        btn_success.setOnClickListener {
            saveChanges()
        }

        return view

    }
    private fun saveChanges(){
        Log.d(TAG,"Saving data function")
          val username: String = mUsername.text.toString()

          val description:String = mDescription.text.toString()
        myRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(dataSnapshot: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                for(ds:DataSnapshot in dataSnapshot.child(getString(R.string.firebase_users)).children){
                    if(ds.key.equals(userID)){
                        user.username = ds.getValue(User::class.java)!!.username
                    }
                }
                if(mUserAllDetails.user.username.equals(username)){
                    Toast.makeText(mContext,"nothing change",Toast.LENGTH_SHORT).show()



                }else if(!mUserAllDetails.user.username.equals(username)){

                    isExists(username)

                }
                if(!mUserAllDetails.userDetails.description.equals(mDescription)){
                    mFirebaseHelper.updateDescription(description)
                }




            }
        })



    }
/*
    Check if username already exists in Firebase database
 */
    private fun isExists(username:String) {
        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child(getString(R.string.firebase_users))
            .orderByChild(getString(R.string.username_firebase))
            .equalTo(username)
    query.addListenerForSingleValueEvent(object:ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onDataChange(p0: DataSnapshot) {
            if(!p0.exists()){
               mFirebaseHelper.updateUsername(username)
                Toast.makeText(activity,"Username updated",Toast.LENGTH_SHORT).show()
            }
            for(ds: DataSnapshot in p0.children){
                if(ds.exists()){
                    Toast.makeText(activity,"Username already in use",Toast.LENGTH_SHORT).show()

                }

            }
        }


    })

    }


    private fun setProfileDetails(userAllDetails: UserAllDetails){
        mUserAllDetails = userAllDetails

        val user: User = userAllDetails.user
        val userDetails = userAllDetails.userDetails
        ImageLoader()
            .setImage(userDetails.profile_photo,mProfilePhoto,null,"")
        mUsername.text = user.username
        mDescription.text = userDetails.description
        mEmail.text = user.email




    }

    /* FIREBASE*/
    private fun setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid

        mAuthListener = FirebaseAuth.AuthStateListener {
            fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = firebaseAuth.currentUser

                if (user != null) {
                    //User is signed in
                    Log.d(com.nostra13.universalimageloader.core.ImageLoader.TAG,"Signed_in")

                } else {
                    // User is signed out
                    Log.d(com.nostra13.universalimageloader.core.ImageLoader.TAG,"Signed_out")
                }
                // ...
            }
            myRef.addListenerForSingleValueEvent(object: ValueEventListener {
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
