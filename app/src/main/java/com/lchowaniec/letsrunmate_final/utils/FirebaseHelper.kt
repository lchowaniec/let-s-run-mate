package com.lchowaniec.letsrunmate_final.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Models.User
import com.lchowaniec.letsrunmate_final.Models.UserAllDetails
import com.lchowaniec.letsrunmate_final.Models.UserDetails
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.R.string.firebase_user_details
import com.lchowaniec.letsrunmate_final.R.string.firebase_users
import com.mapbox.geojson.Point
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class FirebaseHelper(context: Context) {

    val mContext = context
    val mAuth = FirebaseAuth.getInstance()
    val mFirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef = mFirebaseDatabase.reference
    val userID= mAuth.currentUser!!.uid
    var newActivityKey:String="123"
    val mStorage = FirebaseStorage.getInstance()
    val myRefStorage = mStorage.reference















    open fun UserExistsCheck(username: String,dataSnapshot: DataSnapshot): Boolean {
        val user = User()
        for (ds: DataSnapshot in dataSnapshot.child(this.userID.toString()).children){
            user.username = ds.getValue(User::class.java)!!.username
            if(user.username.equals(username)){
                return true
            }
        }
        return false

    }
    open fun activityCounter(dataSnapshot: DataSnapshot):Int{
        var counter:Int =0
        for(ds: DataSnapshot in dataSnapshot
            .child(mContext.getString(R.string.firebase_users_activities))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .children){
            counter++
            Log.d(TAG,"counter: "+counter)

        }
        return counter


        }



    open fun getTime(): String {
        val mDate = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault())
      //  mDate.timeZone = TimeZone.getTimeZone("EUROPE/BERLIN")
        return mDate.format(Date())
    }


    open fun uploadUrl(url:String,activity_key: String,distance:String,durationTime:String,avgPace:String){
        val activity = Activity()

        myRef.child(mContext.getString(R.string.firebase_activities))
            .child(activity_key)
            .child(mContext.getString(R.string.firebase_url))
            .setValue(url)
        myRef.child(mContext.getString(R.string.firebase_users_activities))
            .child(userID)
            .child(activity_key)
            .child(mContext.getString(R.string.firebase_url))
            .setValue(url)




    }
    open fun allCoordinates(url: String):ArrayList<Point> {
        val ref = URL(url)
        Log.d(TAG,"allCoordinates URL: "+url)

        var points: ArrayList<Point> = ArrayList()
        var x: Int = 1
        val mIn: BufferedReader = BufferedReader(InputStreamReader(ref.openStream()))
        val listX: ArrayList<Double> = ArrayList()
        val listY: ArrayList<Double> = ArrayList()
        for (line in mIn.lines()) {
            if (x == 2) {
                listY.add(line.toDouble())
                x = 1
            } else {
                listX.add(line.toDouble())
                x++
            }


        }
        for (i in 0..listX.size - 1) {
            points.add(Point.fromLngLat(listX[i],listY[i]))
        }
        Log.d(TAG,"allCoordinates: "+listX+listY)
        return points
    }
    open fun addPhoto(file:Uri,key:String){
        val uploadTask: UploadTask
        val acc_uid = mAuth.currentUser!!.uid
        var my_uri: String
        val my_ref = myRefStorage.child(Paths().FIREBASE_ACTIVITIES_STORAGE + "/" + acc_uid +"/"+ key +".png")
        uploadTask = my_ref.putFile(file)
        val urlTask = uploadTask.continueWithTask(object:Continuation<UploadTask.TaskSnapshot,Task<Uri>>{
            override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                return my_ref.downloadUrl
            }

        }).addOnCompleteListener(object:OnCompleteListener<Uri>{
            override fun onComplete(p0: Task<Uri>) {
                if(p0.isSuccessful){
                    val downloadUrl:Uri? = p0.result
                    my_uri = downloadUrl.toString()
                    uploadPhoto(my_uri,key)

                }
            }

        })





    }
    open fun uploadPhoto(url:String,activity_key:String){
        myRef.child(mContext.getString(R.string.firebase_activities))
            .child(activity_key)
            .child(mContext.getString(R.string.firebase_url_photo))
            .setValue(url)
        myRef.child(mContext.getString(R.string.firebase_users_activities))
            .child(userID)
            .child(activity_key)
            .child(mContext.getString(R.string.firebase_url_photo))
            .setValue(url)


    }


        open fun addNewActivity(durationTime:String,distance:String,avgPace:String,file_name:String,file:Uri,kcal:Int): String{
            Log.d(TAG,"Add activity do database")
            newActivityKey = myRef.child(mContext.getString(R.string.firebase_activities)).push().key.toString()
            val upload_task:UploadTask
            val activity = Activity()
            var url :String ="123"
            var my_uri:String

            val acc_uid = FirebaseAuth.getInstance().currentUser!!.uid
            val my_ref = myRefStorage.child(Paths().FIREBASE_ACTIVITIES_STORAGE + "/" + acc_uid +"/"+ file_name +".txt")
            upload_task= my_ref.putFile(file)
            val urlTask = upload_task.continueWithTask(object: Continuation<UploadTask.TaskSnapshot,Task<Uri>>{
                override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    return my_ref.downloadUrl
                }

            }).addOnCompleteListener(object:OnCompleteListener <Uri>{
                override fun onComplete(p0: Task<Uri>){
                    if(p0.isSuccessful){
                        val downloadUrl:Uri? = p0.result
                        my_uri = downloadUrl.toString()
                        activity.url = my_uri
                        uploadUrl(my_uri,newActivityKey,distance,durationTime,avgPace)


                    }
                }

            })

            activity.date = getTime()
            activity.duration_time = durationTime
            activity.user_id = FirebaseAuth.getInstance().currentUser!!.uid
            activity.activity_id = newActivityKey
            activity.distance = distance
            activity.avgPace = avgPace
            activity.kcal = kcal


            myRef.child(mContext.getString(R.string.firebase_users_activities))
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(newActivityKey)
                .setValue(activity)


            myRef.child(mContext.getString(R.string.firebase_activities))
                .child(newActivityKey)
                .setValue(activity)




            return newActivityKey

    }



    open fun addNewUser(email:String,username: String,descripton: String, profile_photo:String) {
        val user = User(userID.toString(), email, usernameCheck().changeUsername(username))

        if (userID != null) {
            myRef.child(mContext.getString(firebase_users))
                .child(userID)
                .setValue(user)
        }

        val settings: UserDetails = UserDetails(
            descripton,
            usernameCheck().changeUsername(username),
            0F,
            0,
            0,
            ""
        )
        if (userID != null) {
            myRef.child(mContext.getString(R.string.firebase_user_details))
                .child(userID)
                .setValue(settings)
        }
    }

    open fun getAllActivities(dataSnapshot: DataSnapshot,userID:String):ArrayList<Activity> {
        val array = ArrayList<Activity>()
        var activity:Activity= Activity()
        val ref = mFirebaseDatabase.reference.child(mContext.getString(R.string.firebase_users_activities))
        ref.child(userID).addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(ds:DataSnapshot in p0.children){
                        activity = (ds.getValue(Activity::class.java))!!
                }

        }
            })
        return array
    }




        open fun getActivityDetails(dataSnapshot: DataSnapshot,newActivityKey:String):Activity{
        val activity= Activity()
        for(ds:DataSnapshot in dataSnapshot.children){
            if(ds.key.equals(mContext.getString(R.string.firebase_activities))){
                activity.points =
                (ds.child(newActivityKey)
                    .getValue(Activity::class.java)?.points!!
                        )
                activity.avgPace =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.avgPace!!
                            )
                activity.distance =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.distance!!
                            )
                activity.date =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.date!!
                            )
                activity.duration_time =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.duration_time!!
                            )
                activity.caption =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.caption!!
                            )
                activity.user_id =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.user_id!!
                            )
                activity.activity_id =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.activity_id!!
                            )
                activity.url =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.url!!
                            )
                activity.kcal =
                    (ds.child(newActivityKey)
                        .getValue(Activity::class.java)?.kcal!!)

            }
        }


            return activity
    }
    open fun getAccountsSettings(dataSnapshot: DataSnapshot):UserAllDetails{




        val settings= UserDetails()
        val user = User()

        //userDetails
        for(ds:DataSnapshot in dataSnapshot.children){
            if(ds.key.equals(mContext.getString(R.string.firebase_user_details))){
                settings.username =
                (ds.child(userID)
                    .getValue(UserDetails::class.java)?.username!!
                )
                settings.description =
                (ds.child(userID)
                    .getValue(UserDetails::class.java)?.description!!
                        )
                settings.distance =
                (ds.child(userID)
                    .getValue(UserDetails::class.java)?.distance!!
                        )
                settings.friends =
                (ds.child(userID)
                    .getValue(UserDetails::class.java)?.friends!!
                        )
                settings.activities =
                (ds.child(userID)
                    .getValue(UserDetails::class.java)?.activities!!
                        )
                settings.profile_photo =
                (ds.child(userID)
                    .getValue(UserDetails::class.java)?.profile_photo!!
                        )

                }
            //user
            if (ds.key.equals(mContext.getString(firebase_users))){

                user.username =
                    (ds.child(userID)
                        .getValue(User::class.java)?.username!!
                        )
                user.user_id =
                (ds.child(userID)
                    .getValue(User::class.java)?.user_id!!
                        )
                user.email =
                (ds.child(userID)
                    .getValue(User::class.java)?.email!!
                        )

            }
        }

        val userAll = UserAllDetails(user,settings)
        Log.d(TAG,"nickname:"+ usernameCheck().changeUsername("Luki chowciu"))

        return userAll





    }

    fun updateUsername(username: String) {
        myRef.child(mContext.getString(firebase_users))
            .child(userID)
            .child(mContext.getString(R.string.username_firebase))
            .setValue(username)
        myRef.child(mContext.getString(firebase_user_details))
            .child(userID)
            .child(mContext.getString(R.string.username_firebase))
            .setValue(username)

    }
    fun updateDescription(descripton: String){
        myRef.child(mContext.getString(firebase_user_details))
            .child(userID)
            .child(mContext.getString(R.string.description_firebase))
            .setValue(descripton)

    }


}





