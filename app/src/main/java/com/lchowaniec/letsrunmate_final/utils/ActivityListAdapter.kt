package com.lchowaniec.letsrunmate_final.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.R

class ActivityListAdapter(context: Context, resource: Int, objects: MutableList<Activity>):
    ArrayAdapter<Activity>(context, resource, objects) {
    private val mContext = context
    private val mResource = resource
    private val mObject = objects
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var retView: View
        if(convertView == null) {
            val distance = getItem(position)!!.distance
            val date = getItem(position)!!.date
            val kcal = getItem(position)!!.kcal
            val time = getItem(position)!!.duration_time
            val pace = getItem(position)!!.avgPace

//            val activity = Activity()
//            activity.distance = distance
//            activity.duration_time = time
//            activity.kcal = kcal
//            activity.date = date
//            activity.avgPace = pace
            val inflater = LayoutInflater.from(context)
            retView = inflater.inflate(mResource,parent,false)
            val  mDistance = retView.findViewById<TextView>(R.id.history_adapter_distance)
            val  mKcal = retView.findViewById<TextView>(R.id.history_adapter_kcal)
            val  mTime = retView.findViewById<TextView>(R.id.history_adapter_time)
            val  mDate = retView.findViewById<TextView>(R.id.history_adapter_date)
            val mPace = retView.findViewById<TextView>(R.id.history_adapter_pace)
            val myFont = ResourcesCompat.getFont(context,R.font.bangers)
            val myFont2 = ResourcesCompat.getFont(context,R.font.paprika)
            val myFont3 = ResourcesCompat.getFont(context,R.font.bowlby_one_sc)

            mDistance.typeface = myFont
            mDistance.text = "$distance km"
            mKcal.typeface = myFont2
            mKcal.text = "${kcal} kcal"
            mDate.typeface = myFont3
            mDate.text = date
            mTime.typeface = myFont2
            mTime.text = time
            mPace.typeface = myFont2
            mPace.text = pace.replace(",","'")

        }else{
            retView = convertView
        }
        return retView

    }
}






