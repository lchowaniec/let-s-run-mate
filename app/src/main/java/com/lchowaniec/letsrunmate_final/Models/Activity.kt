package com.lchowaniec.letsrunmate_final.Models

import android.os.Parcel
import android.os.Parcelable
import com.mapbox.geojson.Point
import java.io.Serializable

open class Activity(): Serializable, Parcelable {
    var date:String =""
    var duration_time:String =""
    var distance:String=""
    //var image_path:String = ""
    var caption:String=""
    var activity_id:String=""
    var user_id:String =""
    var avgPace:String=""
    var points:ArrayList<Point> = ArrayList()
    var url: String = ""
    var kcal:Int = 0
    var url_photo:String  =""
    private var trophies: HashMap<String,Trophy> = HashMap()






    constructor(parcel: Parcel) : this() {
        parcel.writeString(date)
        parcel.writeString(duration_time)
        parcel.writeString(distance)
        parcel.writeString(caption)
        parcel.writeString(activity_id)
        parcel.writeString(user_id)
        parcel.writeString(avgPace)
        parcel.writeString(url)
        parcel.writeString(kcal.toString())
        parcel.writeString(url_photo)
        parcel.writeMap(trophies.toMap())


    }


    constructor(date:String,duration_time:String,caption:String,activity_id:String,user_id:String,distance:String,avgPace:String,points:ArrayList<Point>,url:String,kcal:Int,url_photo:String,trophies:HashMap<String,Trophy>) :this(){
        this.date = date
        this.duration_time = duration_time
        this.caption = caption
        this.activity_id = activity_id
        this.user_id = user_id
        this.distance = distance
        this.avgPace = avgPace
        this.points = points
        this.url = url
        this.kcal = kcal
        this.url_photo = url_photo
        this.trophies = trophies
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(duration_time)
        parcel.writeString(distance)
        parcel.writeString(caption)
        parcel.writeString(activity_id)
        parcel.writeString(user_id)
        parcel.writeString(avgPace)
        parcel.writeString(url)
        parcel.writeInt(kcal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Activity> {
        override fun createFromParcel(parcel: Parcel): Activity {
            return Activity(parcel)
        }

        override fun newArray(size: Int): Array<Activity?> {
            return arrayOfNulls(size)
        }
    }


}