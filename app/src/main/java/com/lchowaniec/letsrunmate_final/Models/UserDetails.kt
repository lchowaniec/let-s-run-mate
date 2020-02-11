package com.lchowaniec.letsrunmate_final.Models

open class UserDetails(){
    var description: String = ""
    var username: String = ""
    var distance: Float = 0F
     var friends: Int = 0
     var activities: Int =0
    var profile_photo :String = ""

    constructor(description: String, username: String, distance: Float, friends: Int, activities: Int,profile_photo:String): this() {

        this.description = description
        this.username = username
        this.distance = distance
        this.friends = friends
        this.activities = activities
        this.profile_photo = profile_photo

    }




}