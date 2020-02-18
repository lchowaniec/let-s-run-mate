package com.lchowaniec.letsrunmate_final.Chat


import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.Message
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.MessageAdapter
import com.lchowaniec.letsrunmate_final.utils.TimeAgo
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class ChatInsideFragment : Fragment() {
    init {
        super.onStart()
        arguments = Bundle()
    }
    lateinit var mUsername:TextView
    lateinit var mTimeStamp:TextView
    lateinit var mProfilePhoto:CircleImageView
    lateinit var mBackArrow:ImageView
    lateinit var mEditText:EditText
    lateinit var mSend:ImageView
    lateinit var mUserDetails: UserDetails

    //recyclerview
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    lateinit var mCurrentID:String
    private lateinit var mRecyclerView:RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_chat_inside, container, false)
        mUsername = view.findViewById(R.id.chat_inside_username)
        mProfilePhoto = view.findViewById(R.id.chat_inside_profile_photo)
        mTimeStamp = view.findViewById(R.id.chat_inside_time)
        mBackArrow = view.findViewById(R.id.chat_inside_back)
        mEditText = view.findViewById(R.id.chat_inside_edittext)
        mSend = view.findViewById(R.id.chat_inside_send)
        mCurrentID = FirebaseAuth.getInstance().currentUser!!.uid
        mRecyclerView = view.findViewById(R.id.chat_inside_recycler_view)
        messageList = ArrayList()
        setupWidgets()
        return view

    }
    fun setupWidgets(){
        mUserDetails = getUser()!!
        mUsername.text = mUserDetails!!.username
        mTimeStamp.text = TimeAgo(mUserDetails!!.last_online,activity!!.applicationContext).getTimeStamp()
        ImageLoader.getInstance().displayImage(mUserDetails!!.profile_photo,mProfilePhoto)
        mBackArrow.setOnClickListener{
            activity!!.supportFragmentManager.popBackStack()
        }
        mSend.setOnClickListener{
            sendMessage()
            mEditText.setText("")

        }
        loadData()


    }
    private fun loadData(){
        println("-----------------------------")
        println(mCurrentID)
        println(mUserDetails.user_id)
        FirebaseDatabase.getInstance().reference
            .child("messages")
            .child(mCurrentID)
            .child(mUserDetails.user_id)
            .addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val message = p0.getValue(Message::class.java)!!
                    messageList.add(message)
                    messageAdapter.notifyDataSetChanged()
                    mRecyclerView.scrollToPosition(messageList.size -1)
                }
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity!!.applicationContext)
            setHasFixedSize(true)
            messageAdapter = MessageAdapter()
            messageAdapter.submitList(messageList)
            adapter = messageAdapter

        }

    }

    private fun sendMessage(){
        val message = mEditText.text.toString()
        val currentUser =  FirebaseAuth.getInstance().currentUser!!.uid
        if(!TextUtils.isEmpty(message)){
            val current_user = "messages/"+currentUser+ "/" + mUserDetails!!.user_id
            val chat_user = "messages/"+ mUserDetails!!.user_id + "/" + currentUser
            val myRef = FirebaseDatabase.getInstance().reference
                .child("messages")
                .child(mUserDetails!!.user_id)
                .child(currentUser)
                .push()
            val key = myRef.key

            val messageMap = HashMap<String,Any>()
            messageMap.put("message", message)
            messageMap.put("seen",false)
            messageMap.put("type","text")
            messageMap.put("time",ServerValue.TIMESTAMP)
            messageMap.put("from",currentUser)
            val messageUserMap = HashMap<String,Any>()
            messageUserMap.put(current_user+"/"+key,messageMap)
            messageUserMap.put(chat_user+"/"+key,messageMap)
            FirebaseDatabase.getInstance().reference.updateChildren(messageUserMap,object:DatabaseReference.CompletionListener{
                override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {
                    if(p0!=null){
                        Log.d(TAG,"ChatInsideFragment error: "+ p0.message)
                    }
                }
            })


        }
    }

    private fun getUser(): UserDetails? {
        Log.d(ContentValues.TAG,"get photo from bundle")
        val bundle = this.arguments
        return if (bundle!=null){
            bundle.getParcelable(getString(R.string.user_details))
        }else{
            null
        }


    }
    private fun getNumber(): Int? {
        Log.d(ContentValues.TAG,"get photo from bundle")
        val bundle = this.arguments
        return if (bundle!=null){
            bundle.getInt(getString(R.string.activity_number))
        }else{
            0
        }

    }


}
