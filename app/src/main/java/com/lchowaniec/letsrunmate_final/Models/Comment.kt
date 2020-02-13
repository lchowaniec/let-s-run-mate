package com.lchowaniec.letsrunmate_final.Models

open class Comment (){

    var comment:String=""
    var user_id:String=""
    var trophies: List<Trophy> =ArrayList()
    var date:String =""

    constructor(comment:String,user_id:String,trophies:ArrayList<Trophy>,date:String):this(){
        this.comment = comment
        this.date = date
        this.trophies = trophies
        this.user_id = user_id
    }
}