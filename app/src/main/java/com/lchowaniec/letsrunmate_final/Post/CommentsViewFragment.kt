package com.lchowaniec.letsrunmate_final.Post


import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Models.Comment
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.CommentListAdapter
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.lchowaniec.letsrunmate_final.utils.Trophy
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToLong


/**
 * A simple [Fragment] subclass.
 */
class CommentsViewFragment : Fragment() {

    init {
        super.onStart()
        arguments = Bundle()
    }

    // TODO 1: MULTIPLE TROPHIES
    //fields
    private lateinit var mBackArrow: ImageView
    private lateinit var mSend:TextView
    private lateinit var mComment:EditText
    private lateinit var mListView:ListView
    private lateinit var firstComment:TextView
    private lateinit var firstDate:TextView
    private lateinit var firstPhoto:CircleImageView
    private lateinit var firstUsername:TextView
    private lateinit var mTrophyGold:ImageView
    private lateinit var mTropnyWhite:ImageView
    private var TrophiedByUser: Boolean = false


    //vars
    private lateinit var mActivity: Activity
    private lateinit var mActivityID:String
    private lateinit var mContext: Context
    lateinit var mTrophy: Trophy


    //FIREBASE CONFIG
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper

    private var mCommentList :ArrayList<Comment> = ArrayList()
    private lateinit var mCommentAdapter: CommentListAdapter
    private lateinit var mGesture:GestureDetector


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_comments_view, container, false)
        mContext = activity!!.applicationContext
        mBackArrow = view.findViewById(R.id.imageBack)
        mSend = view.findViewById(R.id.comment_send)
        mComment = view.findViewById(R.id.comment_edit_text)
        mListView = view.findViewById(R.id.comment_listview)
        mCommentList = ArrayList()
        mCommentAdapter = CommentListAdapter(mContext,R.layout.layout_comment,mCommentList)
        firstComment = view.findViewById(R.id.comment_comment)
        firstDate = view.findViewById(R.id.comment_date)
        firstPhoto = view.findViewById(R.id.comment_circleView)
        firstUsername = view.findViewById(R.id.comment_username)









        try{
            mActivity = getActivityFromBundle()!!
            mActivityID = mActivity.activity_id
            setupFirebaseAuth()



        }catch (e:NullPointerException){
            Log.e(ContentValues.TAG,"OnCreateView:NullPointerException" +e.message)
        }


        return view
    }
    private fun setupFirst(){
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
            .child(mContext.getString(R.string.firebase_user_details))
            .orderByChild(mContext.getString(R.string.user_id))
            .equalTo(mActivity.user_id)
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snap in p0.children){
                    firstUsername.text = snap.getValue(UserDetails::class.java)!!.username
                    val imageLoader = ImageLoader.getInstance()
                    imageLoader.displayImage(
                        snap.getValue(UserDetails::class.java)!!.profile_photo,
                        firstPhoto
                    )

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
        val difference = getTimestampDiff(mActivity)
        if(difference != "0"){
            firstDate.text = "$difference d"
        }else{
            firstDate.text = mContext.getString(R.string.today)
        }
        firstComment.text = mActivity.caption
    }

    private fun setupWidgets(){


       // mCommentAdapter = CommentListAdapter(activity!!.applicationContext,R.layout.layout_comment,mCommentList)
        mListView.adapter = mCommentAdapter
        setupFirst()


        mBackArrow.setOnClickListener{
            activity!!.supportFragmentManager.popBackStack()
        }

        mSend.setOnClickListener {
            if (mComment.text.toString() != "") {
                addComment(mComment.text.toString())
                mComment.setText("")
                closeKeybord()


            } else {
                Toast.makeText(mContext, "Comment is empty :(", Toast.LENGTH_SHORT).show()
            }
        }


            mListView.setOnItemClickListener(object: AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var check = false

                    Log.d(TAG, "JESTEM TUTAJ W ONCLICK")
                    val mComment = mListView.getItemAtPosition(position) as Comment
                    val reference = FirebaseDatabase.getInstance().reference
                    val query = reference
                        .child(getString(R.string.firebase_activities))
                        .child(mActivity.activity_id)
                        .child(getString(R.string.firebase_comments))
                        .child(mComment.commentId)
                        .child(getString(R.string.firebase_trophies))
                        .orderByChild(getString(R.string.firebase_user_id))
                        .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (singleSnapshot in dataSnapshot.children) {


                                val keyID = singleSnapshot.key

                                myRef.child(getString(R.string.firebase_activities))
                                    .child(mActivity.activity_id)
                                    .child(getString(R.string.firebase_comments))
                                    .child(mComment.commentId)
                                    .child(getString(R.string.firebase_trophies))
                                    .child(keyID!!)
                                    .removeValue()
                                myRef.child(getString(R.string.firebase_users_activities))
                                    .child(mActivity.user_id)
                                    .child(mActivity.activity_id)
                                    .child(getString(R.string.firebase_comments))
                                    .child(mComment.commentId)
                                    .child(getString(R.string.firebase_trophies))
                                    .child(keyID!!)
                                    .removeValue()
                                check = true
                                mCommentAdapter.selected = position
                                mCommentAdapter.notifyDataSetChanged()



                            }
                            if(check == false){
                            addNewLike(mComment)
                                mCommentAdapter.selected = position
                                mCommentAdapter.notifyDataSetChanged()
                            }



                        }
                    })

                }
            })
    }



    private fun getTimestampDiff(activity: Activity):String{
        var difference = ""
        val c = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        val timestamp: Date
        val today = c.time
        format.format(today)
        val activityDate =  activity.date

        try{
            timestamp = format.parse(activityDate)!!
            difference = ((today.time.toDouble() - timestamp.time) / 1000 / 60 / 60 / 24).roundToLong()
                .toString()
            println(difference)
            // difference = getString(Math.round(((today.time.toDouble() - timestamp.time)/1000/60/60/24)).toInt())

        }catch (e: ParseException){
            difference = "0"
        }
        return difference

    }

    fun closeKeybord(){
        val view = activity!!.currentFocus
        if(view!=null){
        val manager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken,0)
        }
    }





    private fun getActivityFromBundle(): Activity? {
        Log.d(ContentValues.TAG, "get activity from bundle"+arguments)
        val bundle = this.arguments
        return if (bundle != null) {
            bundle.getParcelable("Activity")
        } else {
            null
        }
    }
    fun addNewLike(comment:Comment){


       val  mAuth = FirebaseAuth.getInstance()
        val mFirebaseDatabase = FirebaseDatabase.getInstance()
       val  myRef = mFirebaseDatabase.reference
        val trophyID = myRef.push().key
        val commentID =comment.commentId
        val trophy = com.lchowaniec.letsrunmate_final.Models.Trophy()
        trophy.user_id = FirebaseAuth.getInstance().currentUser!!.uid
        myRef.child(getString(R.string.firebase_activities))
            .child(mActivity.activity_id)
            .child(getString(R.string.firebase_comments))
            .child(commentID)
            .child(getString(R.string.firebase_trophies))
            .child(trophyID!!)
            .setValue(trophy)
        myRef.child(getString(R.string.firebase_users_activities))
            .child(mActivity.user_id)
            .child(mActivity.activity_id)
            .child(getString(R.string.firebase_comments))
            .child(commentID)
            .child(getString(R.string.firebase_trophies))
            .child(trophyID!!)
            .setValue(trophy)



    }
    private fun addComment(newComment:String){
        val commentID = myRef.push().key!!
        val comment = Comment()
        comment.comment = newComment
        comment.commentId = commentID
        comment.date = FirebaseHelper(mContext).getTime()
        comment.user_id = FirebaseAuth.getInstance().currentUser!!.uid
        myRef.child(mContext.getString(R.string.firebase_activities))
            .child(mActivity.activity_id)
            .child(mContext.getString(R.string.firebase_comments))
            .child(commentID)
            .setValue(comment)
        myRef.child(mContext.getString(R.string.firebase_users_activities))
            .child((mActivity.user_id))
            .child(mActivity.activity_id)
            .child(mContext.getString(R.string.firebase_comments))
            .child(commentID)
            .setValue(comment)

    }
    private fun getActivityUserDetails(){
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
            .child(mContext.getString(R.string.firebase_activities))
            .orderByChild(mContext.getString(R.string.firebase_activity_id))
            .equalTo(mActivity.activity_id)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (snap in p0.children){
                  mActivity = snap.getValue(Activity::class.java)!!

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })


    }

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.")



        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser


            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
            // ...
        }
        setupWidgets()

        myRef.child(mContext.getString(R.string.firebase_activities))
            .child(mActivityID)
            .child(mContext.getString(R.string.firebase_comments))
            .addChildEventListener(object:ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    Log.d(TAG,"OnChildChanged: CommentsViewFragment")

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                    Log.d(TAG, "TUTAJ DODAJE 2")

                    mCommentList.add(p0.getValue(Comment::class.java)!!)
                    mCommentAdapter.notifyDataSetChanged()
                }


                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })

    }





}


