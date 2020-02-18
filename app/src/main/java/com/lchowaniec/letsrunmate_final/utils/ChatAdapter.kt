package com.lchowaniec.letsrunmate_final.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter (context: Context, resource: Int, objects: MutableList<UserDetails>):
    ArrayAdapter<UserDetails>(context, resource, objects) {
    val mFirebaseDatabase = FirebaseDatabase.getInstance().reference
    val mContext = context
    val mResource = resource
    inner class ViewHolder(){
        lateinit var mProfilePhoto:CircleImageView
        lateinit var mUsername: TextView
        lateinit var mTime:TextView
        lateinit var mUser:UserDetails
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val retView: View
        val holder = ViewHolder()
        val inflater = LayoutInflater.from(context)
        retView = inflater.inflate(mResource,parent,false)

        holder.mUsername = retView.findViewById(R.id.chat_username)
        holder.mTime = retView.findViewById(R.id.chat_time)
        holder.mProfilePhoto = retView.findViewById(R.id.chat_profile_photo)
        holder.mUser = getItem(position)!!
        println(" getItem: ${getItem(position)!!.username}")

        holder.mUsername.text = getItem(position)!!.username
        holder.mTime.text = TimeAgo(getItem(position)!!.last_online,mContext).getTimeStamp()
        ImageLoader.getInstance().displayImage(getItem(position)!!.profile_photo,holder.mProfilePhoto)
        retView.tag = holder


        return retView
    }
}