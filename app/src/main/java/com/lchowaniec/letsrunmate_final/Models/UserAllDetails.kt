package com.lchowaniec.letsrunmate_final.Models

class UserAllDetails() {
     lateinit var user:User
     lateinit var userDetails:UserDetails


    constructor(user: User,userDetails: UserDetails) : this() {
        this.user = user
        this.userDetails = userDetails

    }



}


