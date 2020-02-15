package com.lchowaniec.letsrunmate_final.Feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Profile.ProfileActivity
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.UserListAdapter
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {
    lateinit var mContext : Context
    val ACTIVITY_NUM = 4
    //widgets
    private lateinit var mSearch:EditText
    private lateinit var mListView:ListView
    private lateinit var mImageBack:ImageView
    //vars
     lateinit var mListUsers:ArrayList<User>
    lateinit var mAdapter:UserListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        mContext= activity!!.applicationContext
        mSearch = view.findViewById(R.id.search_search)
        mListView = view.findViewById(R.id.search_listview)
        mImageBack = view.findViewById(R.id.imageBack)
        mImageBack.setOnClickListener{
            activity!!.finish()
        }
        initListListener()
        return view
    }
    private fun initListListener(){
        mListUsers = ArrayList()
        mSearch.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val text = mSearch.text.toString().toLowerCase(Locale.getDefault())
                searchFriend(text)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })



    }

    private  fun searchFriend(username:String){
        mListUsers.clear()
        if(username.length ==0){
        }else{
            val reference = FirebaseDatabase.getInstance().reference
            val query = reference.child(getString(R.string.firebase_users))
                .orderByChild(getString(R.string.username_firebase)).equalTo(username)
            query.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    for(ds in p0.children){
                        mListUsers.add(ds.getValue(User::class.java)!!)
                        updateListView()
                    }

                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }

    }
    private fun updateListView(){
        mAdapter = UserListAdapter(mContext,R.layout.user_adaper_item,mListUsers)
        mListView.adapter = mAdapter
        mListView.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val intent = Intent(activity!!.applicationContext,
                    ProfileActivity::class.java)
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.search_activity))
                intent.putExtra(getString(R.string.user),mListUsers.get(position))
                startActivity(intent)
            }
        })


    }

}

