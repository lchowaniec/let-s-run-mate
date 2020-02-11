package com.lchowaniec.letsrunmate_final.utils

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.getSystemService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lchowaniec.letsrunmate_final.Models.Comment
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

open class CommentListAdapter(context: Context, resource: Int, objects: MutableList<Comment>):
    ArrayAdapter<Comment>(context, resource, objects) {

    private val mLayoutInflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val layoutResource:Int = resource
    private val mContext:Context = context









    inner class ViewHolder{
        lateinit var comment:TextView
        lateinit var username:TextView
        lateinit var timestamp:TextView
        lateinit var reply:TextView
        lateinit var trophies:TextView
        lateinit var profileImage:CircleImageView
        lateinit var imageTrophy:ImageView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder:ViewHolder = ViewHolder()
        var retView:View?
        if(convertView==null){
            val inflater = LayoutInflater.from(context)

            retView = inflater.inflate(layoutResource,parent,false)

            viewHolder = ViewHolder()
            viewHolder.comment = retView.findViewById(R.id.comment_comment)
            viewHolder.username = retView.findViewById(R.id.comment_username)
            viewHolder.timestamp = retView.findViewById(R.id.comment_date)
            viewHolder.reply = retView.findViewById(R.id.comment_reply)
            viewHolder.profileImage = retView.findViewById(R.id.comment_circleView)
            viewHolder.imageTrophy = retView.findViewById(R.id.comment_trophy)
            retView.setTag(viewHolder)
        }else{
            retView = convertView
        }

        viewHolder.comment.text = getItem(position)!!.comment
        val difference = getTimestampDiff(getItem(position)!!)
        if(!difference.equals("0")){
            viewHolder.timestamp.text = "$difference d"
        }else{
            viewHolder.timestamp.text = context.getString(R.string.today)
        }
        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
            .child(mContext.getString(R.string.firebase_user_details))
            .orderByChild(mContext.getString(R.string.user_id))
            .equalTo(getItem(position)!!.user_id)
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snap in p0.children){
                    viewHolder.username.text = snap.getValue(UserDetails::class.java)!!.username
                    val imageLoader = ImageLoader.getInstance()
                    imageLoader.displayImage(
                        snap.getValue(UserDetails::class.java)!!.profile_photo,
                        viewHolder.profileImage
                    )

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })













        return retView!!
    }


    private fun getTimestampDiff(comment: Comment):String{
        var difference = ""
        val c = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        val timestamp: Date
        val today = c.time
        format.format(today)
        val activity_date =  comment.date

        try{
            timestamp = format.parse(activity_date)!!
            difference = Math.round(((today.time.toDouble() - timestamp.time)/1000/60/60/24)).toString()
            println(difference)
            // difference = getString(Math.round(((today.time.toDouble() - timestamp.time)/1000/60/60/24)).toInt())

        }catch (e: ParseException){
            difference = "0"
        }
        return difference

    }

}
