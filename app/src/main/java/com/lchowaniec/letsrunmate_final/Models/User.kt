package com.lchowaniec.letsrunmate_final.Models

open class User() {
     var user_id:String = ""
    var email:String = ""
    var username :String = ""

    constructor(user_id: String, email: String,username: String) : this() {
        this.user_id = user_id
        this.email = email
        this.username = username
    }






}