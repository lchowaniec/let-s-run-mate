package com.lchowaniec.letsrunmate_final.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView

open class UserListAdapter(context: Context, resource: Int, objects: MutableList<User>) :
    ArrayAdapter<User>(context, resource, objects) {
    private val mContext = context
    private val mResource = resource
    private val mObject = objects
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val retView: View
        if(convertView == null) {
            val username = getItem(position)!!.username
            val email = getItem(position)!!.email
            Log.d(TAG,"NOW USER"+ username)

            val inflater = LayoutInflater.from(context)
            retView = inflater.inflate(mResource,parent,false)
            val  mUsername = retView.findViewById<TextView>(R.id.username2)
            val  mEmail = retView.findViewById<TextView>(R.id.email2)
            val mPhoto = retView.findViewById<CircleImageView>(R.id.image2)


            mUsername.text = username
            mEmail.text = email
            val ref = FirebaseDatabase.getInstance().reference
            val query = ref.child(mContext.getString(R.string.firebase_user_details))
            query.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for(ds in p0.children){
                                val imageLoader = ImageLoader.getInstance()
                        imageLoader.displayImage(ds.getValue(UserDetails::class.java)!!.profile_photo,mPhoto)

                    }
                }
            })


        }else{
            retView = convertView
        }
        return retView

    }
}