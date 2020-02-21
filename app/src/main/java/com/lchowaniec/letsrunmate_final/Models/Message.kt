package com.lchowaniec.letsrunmate_final.Models

open class Message{
     lateinit var message:String
     var seen: Boolean = false
    var time:Long = 0L
     lateinit var type: String
    lateinit var from:String


    constructor(message: String,seen:Boolean,time:Long,type:String,from:String){
        this.from = from
        this.message = message
        this.seen = seen
        this.time = time
        this.type = type

    }
    constructor(){}
}