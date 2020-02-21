package com.lchowaniec.letsrunmate_final.utils

import android.content.Context

open class TimeAgo(time: Long,context: Context){
    var SECOND_inMILIS: Int = 1000
    var MINUTES_inMILIS:Int = SECOND_inMILIS *60
    var HOURS_inMILIS:Int = MINUTES_inMILIS*60
    var DAYS_inMILIS:Int = HOURS_inMILIS*24
    var mTime:Long =time
    var mContext = context
    open fun getTimeStamp(): String {
        if(mTime<1000000000000L) {
            mTime = mTime*1000
        }
        val now = System.currentTimeMillis()
        var diff = now - mTime
        println("$mTime,$now,$diff")
        if(diff<MINUTES_inMILIS){
            return "now"
        }else if(diff< 2* MINUTES_inMILIS){
            return "a minute ago"
        }else if(diff<60 * MINUTES_inMILIS){
            return "${diff / MINUTES_inMILIS} minutes ago"
        }else if(diff<90* MINUTES_inMILIS){
            return "an hour ago"
        }else if (diff< 24* HOURS_inMILIS){
            return "${diff/ HOURS_inMILIS} hours ago"
        }else if(diff < 48 * HOURS_inMILIS){
            return "yesterday"
        }else{
            return "${diff / DAYS_inMILIS} days ago"
        }




    }

}