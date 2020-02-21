package com.lchowaniec.letsrunmate_final.Chat


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.BottomNaviViewHelper
import com.lchowaniec.letsrunmate_final.utils.ChatAdapter

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {
    lateinit var mFriendsList:ArrayList<String>
    lateinit var mFriendsDetails: ArrayList<UserDetails>
    private lateinit var mListView: ListView
    private lateinit var adapter:ChatAdapter
    private lateinit var  bottomNaviBar: BottomNavigationView
    var ACTIVITY_NUM = 3
    interface chatListener{
        fun chatListener(userDetails: UserDetails)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)
        mFriendsList = ArrayList()
        mFriendsDetails = ArrayList()
        mListView = view!!.findViewById<ListView>(R.id.chat_listview)
        bottomNaviBar = view!!.findViewById(R.id.chat_bottom)
        setupBottomNavigationBar()
        getFriends()

        return view
    }

    fun getFriends(){
        val mDatabaseReference = FirebaseDatabase.getInstance().reference
        val myRef = mDatabaseReference.child(getString(R.string.firebase_following))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        myRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG,"ChatFragment: getFriends "+ p0)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (ds in p0.children){
                    mFriendsList.add(ds.child(getString(R.string.firebase_user_id)).getValue().toString())

                }
                getDetails()

            }
        })



    }
    private fun getDetails(){

        val reference = FirebaseDatabase.getInstance().reference
        for(i  in 0..mFriendsList.size -1){
            println(i)
            Log.d(TAG,"MOJE ACTIVITY LIST: "+mFriendsList)
            val query = reference
                .child(getString(R.string.firebase_user_details))
                .orderByChild(getString(R.string.user_id))
                .equalTo(mFriendsList.get(i))

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {

                        mFriendsDetails.add(ds.getValue(UserDetails::class.java)!!)
                        println("JESTEM JESTEM $mFriendsDetails")


                    }
                    setupListView()


                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        }


    }


    private fun setupBottomNavigationBar(){


        BottomNaviViewHelper().setupBottomNaviView(bottomNaviBar)
        BottomNaviViewHelper().enableNavigationBar(activity!!.applicationContext,bottomNaviBar)
        val menu: Menu = bottomNaviBar.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true




    }

    open fun setupListView(){
        println(mFriendsDetails)
        val adapter = ChatAdapter(activity!!, R.layout.chat_adapter_item, mFriendsDetails)
        mListView.adapter = adapter
        adapter.notifyDataSetChanged()
        mListView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newFragment = ChatInsideFragment()
                val bundle = Bundle()
                println(adapter.getItem(position))
                bundle.putParcelable(getString(R.string.user_details),adapter.getItem(position))
                bundle.putInt(getString(R.string.activity_number),ACTIVITY_NUM)
                newFragment.arguments = bundle

                val transaction = activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(R.anim.enter_right_left,R.anim.exit_right_left,R.anim.enter_left_right,R.anim.exit_left_right)
                    replace(R.id.chat_container,newFragment)
                    addToBackStack(null)

                }
                transaction.commit()
            }
        }
    }

}
