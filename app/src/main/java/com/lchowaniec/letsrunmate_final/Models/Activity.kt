package com.lchowaniec.letsrunmate_final.Models

import com.mapbox.geojson.Point
import java.io.Serializable

open class Activity(): Serializable{
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


    constructor(date:String,duration_time:String,caption:String,activity_id:String,user_id:String,distance:String,avgPace:String,points:ArrayList<Point>,url:String,kcal:Int) :this(){
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
    }



}