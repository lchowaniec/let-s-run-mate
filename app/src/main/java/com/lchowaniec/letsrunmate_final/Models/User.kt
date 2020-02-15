package com.lchowaniec.letsrunmate_final.Models

import android.os.Parcel
import android.os.Parcelable

open class User():Parcelable {
     var user_id:String = ""
    var email:String = ""
    var username :String = ""

    constructor(parcel: Parcel) : this() {
        user_id = parcel.readString()!!
        email = parcel.readString()!!
        username = parcel.readString()!!
    }

    constructor(user_id: String, email: String,username: String) : this() {
        this.user_id = user_id
        this.email = email
        this.username = username
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(email)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}