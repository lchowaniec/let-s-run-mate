package com.lchowaniec.letsrunmate_final.utils

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Feed.FeedActivity
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.Profile.ProfileActivity
import com.lchowaniec.letsrunmate_final.R
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong



open class FeedListAdapter(context: Context, resource: Int, objects: MutableList<Activity>) :
    ArrayAdapter<Activity>(context, resource, objects) {
    private val mLayoutInflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mLayoutResource: Int = resource
    private var mContext: Context = context
    private lateinit var mRef: DatabaseReference
    private var mUsername: String = ""
    open interface OnItemsListener{
        fun onItem()

    }
    lateinit var mOnItems:OnItemsListener



    inner class ViewHolder {
        lateinit var mProfilePhoto: CircleImageView
        lateinit var username: TextView
        lateinit var timestamp: TextView
        lateinit var caption: TextView
        lateinit var trophies: TextView
        lateinit var comments: TextView
        lateinit var mainPhoto: SquareImageView
        lateinit var imageTrophyWhite: ImageView
        lateinit var imageTrophyGold: ImageView
        lateinit var commentsLogo: ImageView
        lateinit var captionUsername: TextView

        var userDetails: UserDetails = UserDetails()
        var user = User()
        lateinit var users: StringBuilder
        var trophiedByUser: Boolean = false
        lateinit var trophy: Trophy
        lateinit var mGestureDetector: GestureDetector
        lateinit var mGestureDetector2: GestureDetector

        lateinit var activity: Activity


    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val retView: View
        var holder:ViewHolder

            val inflater = LayoutInflater.from(context)
            retView = inflater.inflate(mLayoutResource,parent,false)
            holder = ViewHolder()
            holder.username =retView.findViewById(R.id.post_username)
            holder.mainPhoto = retView.findViewById(R.id.post_image)
            holder.imageTrophyGold = retView.findViewById(R.id.post_trophy_clicked)
            holder.imageTrophyWhite = retView.findViewById(R.id.post_trophy_unclicked)
            holder.commentsLogo = retView.findViewById(R.id.post_comment)
            holder.trophies = retView.findViewById(R.id.post_like_desc)
            holder.comments = retView.findViewById(R.id.post_comments)
            holder.caption = retView.findViewById(R.id.post_caption)
            holder.timestamp = retView.findViewById(R.id.post_time_ago)
            holder.mProfilePhoto = retView.findViewById(R.id.post_profile_photo)
            holder.trophy = Trophy(holder.imageTrophyWhite,holder.imageTrophyGold)
            holder.activity = getItem(position)!!
            holder.captionUsername= retView.findViewById(R.id.post_caption_username)
            holder.mGestureDetector = GestureDetector(mContext,GestureListener(holder))
            holder.mGestureDetector2 = GestureDetector(mContext,GestureListener2(holder))
            holder.users = java.lang.StringBuilder()
            retView.tag = holder
        mRef = FirebaseDatabase.getInstance().reference




        //currentuser and trophy descriotion
            getCurrentUser()
            getTrophyString(holder)

        //comments
        val comments = getItem(position)!!.comments
        println(comments)
        holder.comments.text = "View all  ${comments.size} comments"
        holder.comments.setOnClickListener{

            if (mContext is FeedActivity) {
                (mContext as FeedActivity).onCommentListener(getItem(position)!!,R.string.feed_activity.toString())
            }

        }
        //time
        val time = getTimestampDiff(getItem(position)!!)
        if(time =="0"){
            holder.timestamp.text = mContext.getString(R.string.today)
        }else{
            holder.timestamp.text = "${time} days"
        }
        //main image
        val imageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(getItem(position)!!.url_photo,holder.mainPhoto)
        //profile image
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
            .child(mContext.getString(R.string.firebase_user_details))
            .orderByChild(mContext.getString(R.string.firebase_user_id))
            .equalTo(getItem(position)!!.user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    holder.username.text = ds.getValue(UserDetails::class.java)!!.username
                    holder.captionUsername.text = ds.getValue(UserDetails::class.java)!!.username
                    holder.caption.text = getItem(position)!!.caption
                        holder.username.setOnClickListener{
                        val intent = Intent(mContext,ProfileActivity()::class.java)
                        intent.putExtra(mContext.getString(R.string.calling_activity),mContext.getString(R.string.feed_activity))
                        intent.putExtra(mContext.getString(R.string.user),holder.user)
                        mContext.startActivity(intent)


                    }
                    imageLoader.displayImage(ds.getValue(UserDetails::class.java)!!.profile_photo,holder.mProfilePhoto)

                    holder.mProfilePhoto.setOnClickListener{
                        val intent = Intent(mContext,ProfileActivity()::class.java)
                        intent.putExtra(mContext.getString(R.string.calling_activity),mContext.getString(R.string.feed_activity))
                        intent.putExtra(mContext.getString(R.string.user),holder.user)
                        mContext.startActivity(intent)

                    }
                    holder.userDetails = ds.getValue(UserDetails::class.java)!!
                    holder.commentsLogo.setOnClickListener{
                        if (mContext is FeedActivity) {
                            (mContext as FeedActivity).onCommentListener(getItem(position)!!,mContext.getString(R.string.activity))
                        }






                }
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        //user object
        val query2 = mRef
            .child(mContext.getString(R.string.firebase_users))
            .orderByChild(mContext.getString(R.string.firebase_user_id))
            .equalTo(getItem(position)!!.user_id)
        query2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    holder.user = ds.getValue(User::class.java)!!


                }}

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


        val style = StyleSpan(R.font.bowlby_one_sc)



        if(isEnd(position)){
            loadMore()

        }


        return retView!!


    }
    private fun isEnd(position:Int):Boolean{
        return position == count-1

    }
    private fun loadMore(){
        try{
            mOnItems = context as OnItemsListener

        }catch (e:ClassCastException){
            e.message
        }
        try{
            mOnItems.onItem()

        }catch (e:java.lang.NullPointerException){
            e.message
        }


    }


    private fun getCurrentUser(){
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
            .child(mContext.getString(R.string.firebase_users))
            .orderByChild(mContext.getString(R.string.firebase_user_id))
            .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    mUsername = ds.getValue(UserDetails::class.java)!!.username



                }}

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })


    }
    inner class GestureListener2 : GestureDetector.SimpleOnGestureListener {
        var mHolder:ViewHolder
        constructor(holder:ViewHolder){
            mHolder = holder
        }
        override fun onDown(e: MotionEvent): Boolean {

            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            Log.d(ContentValues.TAG, "onDoubleTap: double tap detected.")

            val reference = FirebaseDatabase.getInstance().reference
            val query = reference
                .child(mContext.getString(R.string.firebase_activities))
                .child(mHolder.activity.activity_id)
                .child(mContext.getString(R.string.firebase_trophies))
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (singleSnapshot in dataSnapshot.children) {

                        val keyID = singleSnapshot.key

                        //case1: Then user already liked the photo
                        if (mHolder.trophiedByUser && singleSnapshot.getValue(com.lchowaniec.letsrunmate_final.Models.Trophy::class.java)!!.user_id == FirebaseAuth.getInstance().currentUser!!.uid
                        ) {

                            mRef.child(mContext.getString(R.string.firebase_activities))
                                .child(mHolder.activity.activity_id)
                                .child(mContext.getString(R.string.firebase_trophies))
                                .child(keyID!!)
                                .removeValue()
                            ///
                            mRef.child(mContext.getString(R.string.firebase_users_activities))
                                .child(mHolder.activity.user_id)
                                .child(mHolder.activity.activity_id)
                                .child(mContext.getString(R.string.firebase_trophies))
                                .child(keyID)
                                .removeValue()

                            mHolder.trophy.throphing()
                            getTrophyString(mHolder)
                        } else if (!mHolder.trophiedByUser) {
                            //add new like
                            addNewLike(mHolder)
                            break
                        }//case2: The user has not liked the photo
                    }
                    if (!dataSnapshot.exists()) {
                        //add new like
                        addNewLike(mHolder)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
            return true
        }}

    inner class GestureListener : GestureDetector.SimpleOnGestureListener {
        var mHolder:ViewHolder
        constructor(holder:ViewHolder){
            mHolder = holder
        }
        override fun onDown(e: MotionEvent): Boolean {

            Log.d(ContentValues.TAG, "onDoubleTap: double tap detected.")

            val reference = FirebaseDatabase.getInstance().reference
            val query = reference
                .child(mContext.getString(R.string.firebase_activities))
                .child(mHolder.activity.activity_id)
                .child(mContext.getString(R.string.firebase_trophies))
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (singleSnapshot in dataSnapshot.children) {

                        val keyID = singleSnapshot.key

                        //case1: Then user already liked the photo
                        if (mHolder.trophiedByUser && singleSnapshot.getValue(com.lchowaniec.letsrunmate_final.Models.Trophy::class.java)!!.user_id == FirebaseAuth.getInstance().currentUser!!.uid
                        ) {

                            mRef.child(mContext.getString(R.string.firebase_activities))
                                .child(mHolder.activity.activity_id)
                                .child(mContext.getString(R.string.firebase_trophies))
                                .child(keyID!!)
                                .removeValue()
                            ///
                            mRef.child(mContext.getString(R.string.firebase_users_activities))
                                .child(mHolder.activity.user_id)
                                .child(mHolder.activity.activity_id)
                                .child(mContext.getString(R.string.firebase_trophies))
                                .child(keyID)
                                .removeValue()

                            mHolder.trophy.throphing()
                            getTrophyString(mHolder)
                        } else if (!mHolder.trophiedByUser) {
                            //add new like
                            addNewLike(mHolder)
                            break
                        }//case2: The user has not liked the photo
                    }
                    if (!dataSnapshot.exists()) {
                        //add new like
                        addNewLike(mHolder)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            return true
        }
    }
    private fun addNewLike(holder: ViewHolder){
        val trophyID = mRef.push().key
        val trophy = com.lchowaniec.letsrunmate_final.Models.Trophy()
        trophy.user_id = FirebaseAuth.getInstance().currentUser!!.uid
        mRef.child(mContext.getString(R.string.firebase_activities))
            .child(holder.activity.activity_id)
            .child(mContext.getString(R.string.firebase_trophies))
            .child(trophyID!!)
            .setValue(trophy)
        mRef.child(mContext.getString(R.string.firebase_users_activities))
            .child(holder.activity.user_id)
            .child(holder.activity.activity_id)
            .child(mContext.getString(R.string.firebase_trophies))
            .child(trophyID)
            .setValue(trophy)

        holder.trophy.throphing()
        getTrophyString(holder)


    }
    private fun getTrophyString(holder:ViewHolder){
        try{
            val reference = FirebaseDatabase.getInstance().reference
            val query = reference
                .child(mContext.getString(R.string.firebase_activities))
                .child(holder.activity.activity_id)
                .child(mContext.getString(R.string.firebase_trophies))
            query.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    holder.users = StringBuilder()
                    for (snap in p0.children){




                        val reference = FirebaseDatabase.getInstance().reference
                        val query = reference
                            .child(mContext.getString(R.string.firebase_users))
                            .orderByChild(mContext.getString(R.string.user_id))
                            .equalTo(snap.getValue(com.lchowaniec.letsrunmate_final.Models.Trophy()::class.java)!!.user_id)
                        query.addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(p0: DataSnapshot) {
                                for (snap2 in p0.children){
                                    Log.d(ContentValues.TAG,"TROPHIES FOUND")
                                    holder.users.append(snap2.getValue(User()::class.java)!!.username)
                                    holder.users.append(",")

                                }
                                val splitted = holder.users.toString().split(",")
                                holder.trophiedByUser = holder.users.toString().contains(mUsername)
                                val ammount = splitted.lastIndex
                                print(ammount)
                                if(ammount ==1 ){
                                    holder.trophies.text = "Trophied by " + splitted[0]

                                }else if(ammount ==2 ){
                                    holder.trophies.text = "Trophied by " + splitted[0] + " and "+splitted[1]
                                }else if(ammount ==3 ){
                                    holder.trophies.text = "Trophied by " + splitted[0] +", "+splitted[1] +" and "+splitted[2]

                                }else if(ammount ==4 ){
                                    holder.trophies.text = "Trophied by " + splitted[0] +", "+splitted[1] +", "+splitted[2]+", "+splitted[1] +" and"+splitted[3]

                                }else if(ammount>4){
                                    holder.trophies.text = "Trophied by " + splitted[0] +", "+splitted[1] +", "+splitted[2]+", "+splitted[1] +" and"+ (splitted.size-3) + " others"

                                }
                                setupTrophyString(holder,holder.trophies.text.toString())


                            }

                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                        })






                    }
                    if (!p0.exists()){
                        holder.trophies.text = ""
                        holder.trophiedByUser = false
                        setupTrophyString(holder,holder.trophies.text.toString())

                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })



        }catch (e:NullPointerException){
            Log.d(TAG,"getTrophyString: "+e.message)
            holder.trophies.text = ""
            holder.trophiedByUser = false
            setupTrophyString(holder,holder.trophies.text.toString())
        }

    }


    private fun setupTrophyString(holder:ViewHolder,trophyString:String){
        if(holder.trophiedByUser){
            holder.imageTrophyWhite.visibility = View.GONE
            holder.imageTrophyGold.visibility = View.VISIBLE
            holder.imageTrophyGold.setOnTouchListener(object: View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return holder.mGestureDetector.onTouchEvent(event)
                }
            })
            holder.mainPhoto.setOnTouchListener(object:View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return holder.mGestureDetector2.onTouchEvent(event)
                }
            })


        }else{
            holder.imageTrophyWhite.visibility = View.VISIBLE
            holder.imageTrophyGold.visibility = View.GONE
            holder.imageTrophyWhite.setOnTouchListener(object: View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return holder.mGestureDetector.onTouchEvent(event)
                }
            })
            holder.mainPhoto.setOnTouchListener(object:View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return holder.mGestureDetector2.onTouchEvent(event)
                }
            })



        }
        holder.trophies.text = trophyString


    }
    private fun getTimestampDiff(activity: Activity):String{
        var difference = ""
        val c = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        val timestamp: Date
        val today = c.time
        format.format(today)
        val activity_date = activity.date

        try{
            timestamp = format.parse(activity_date)!!
            difference = ((today.time.toDouble() - timestamp.time) / 1000 / 60 / 60 / 24).roundToLong()
                .toString()
            println(difference)
            // difference = getString(Math.round(((today.time.toDouble() - timestamp.time)/1000/60/60/24)).toInt())

        }catch (e: ParseException){
            difference = "0"
        }
        return difference

    }


}
