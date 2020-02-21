package com.lchowaniec.letsrunmate_final.Models

import android.os.Parcel
import android.os.Parcelable

open class UserDetails() : Parcelable {
    var description: String = ""
    var username: String = ""
    var distance: Float = 0F
     var friends: Int = 0
     var activities: Int =0
    var profile_photo :String = ""
    var user_id:String =  ""
    var last_online:Long = 0L

    constructor(parcel: Parcel) : this() {
        description = parcel.readString()!!
        username = parcel.readString()!!
        distance = parcel.readFloat()
        friends = parcel.readInt()
        activities = parcel.readInt()
        profile_photo = parcel.readString()!!
        user_id = parcel.readString()!!
    }

    constructor(description: String, username: String, distance: Float, friends: Int, activities: Int,profile_photo:String,user_id:String,last_online:Long): this() {

        this.description = description
        this.username = username
        this.distance = distance
        this.friends = friends
        this.activities = activities
        this.profile_photo = profile_photo
        this.user_id = user_id
        this.last_online = last_online

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeString(username)
        parcel.writeFloat(distance)
        parcel.writeInt(friends)
        parcel.writeInt(activities)
        parcel.writeString(profile_photo)
        parcel.writeString(user_id)
        parcel.writeLong(last_online)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetails> {
        override fun createFromParcel(parcel: Parcel): UserDetails {
            return UserDetails(parcel)
        }

        override fun newArray(size: Int): Array<UserDetails?> {
            return arrayOfNulls(size)
        }
    }


}