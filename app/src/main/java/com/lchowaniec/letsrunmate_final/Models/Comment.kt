package com.lchowaniec.letsrunmate_final.Models

open class Comment (){

    var comment:String=""
    var user_id:String=""
    private var trophies: HashMap<String,Trophy> = HashMap()
    var date:String =""

    constructor(comment:String,user_id:String,trophies:HashMap<String,Trophy>,date:String):this(){
        this.comment = comment
        this.date = date
        this.trophies = trophies
        this.user_id = user_id
    }
}