package com.lchowaniec.letsrunmate_final.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lchowaniec.letsrunmate_final.Models.Comment
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

open class CommentListAdapter(context: Context, resource: Int, objects: MutableList<Comment>):
    ArrayAdapter<Comment>(context, resource, objects) {

    private val mLayoutInflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val layoutResource:Int = resource
    private val mContext:Context = context
    var selected:Int =999


    inner class ViewHolder{
        lateinit var comment:TextView
        lateinit var username:TextView
        lateinit var timestamp:TextView
        lateinit var reply:TextView
        lateinit var trophies:TextView
        lateinit var profileImage:CircleImageView
        lateinit var imageTrophyWhite:ImageView
        lateinit var imageTrophyGold:ImageView

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder = ViewHolder()
        val retView:View?

            val inflater = LayoutInflater.from(context)

            retView = inflater.inflate(layoutResource,parent,false)

            viewHolder = ViewHolder()
            viewHolder.comment = retView.findViewById(R.id.comment_comment)
            viewHolder.username = retView.findViewById(R.id.comment_username)
            viewHolder.timestamp = retView.findViewById(R.id.comment_date)
            viewHolder.reply = retView.findViewById(R.id.comment_reply)
            viewHolder.profileImage = retView.findViewById(R.id.comment_circleView)
            viewHolder.trophies = retView.findViewById(R.id.comment_like_counter)
            viewHolder.imageTrophyWhite = retView.findViewById(R.id.comment_trophy_unclicked)
            viewHolder.imageTrophyGold = retView.findViewById(R.id.comment_trophy_clicked)




        retView.tag = viewHolder


        


        viewHolder.comment.text = getItem(position)!!.comment
        val difference = getTimestampDiff(getItem(position)!!)
        if(difference != "0"){
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
                    val user = snap.getValue(UserDetails::class.java)!!
                    viewHolder.username.text = user.username
                    ImageLoader.getInstance().displayImage(user.profile_photo,viewHolder.profileImage)
                    println(user)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
        if(selected ==position){
            if(viewHolder.imageTrophyGold.visibility == View.VISIBLE){
                viewHolder.imageTrophyGold.visibility = View.GONE
                viewHolder.imageTrophyWhite.visibility = View.VISIBLE
            }else if(viewHolder.imageTrophyWhite.visibility == View.VISIBLE){
                viewHolder.imageTrophyGold.visibility == View.VISIBLE
                viewHolder.imageTrophyGold.visibility == View.GONE
            }

        }







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
        val activityDate =  comment.date

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

}
