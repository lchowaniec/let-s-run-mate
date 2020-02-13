package com.lchowaniec.letsrunmate_final.Run


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.MainActivity
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.R.font.bangers
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.fragment_run.*
import java.io.File
import java.io.FileOutputStream


/**
 * A simple [Fragment] subclass.
 */
class RunFragment : Fragment() {
    var activityCounter: Int =0
    //firebase config
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper

    private val PERMISSION_REQUEST = 10
    //file saving
    private lateinit var file_name:String
    lateinit var fos:FileOutputStream
    var kcal:Int = 0
    var distance: Float = 0F
    var distance2:Float = 0f
    private lateinit var timer: Chronometer
   // lateinit var locationManager: LocationManager
    private var locationGps: Location? = null
    private var lastLocationGps : Location? = null
    private lateinit var pauseButton:Button
    private lateinit var resumeButton: Button
    private lateinit var stopButton: Button
    lateinit var distance_run : TextView
    private var pauseOffset:Long = 0
    var distance_run_save:Boolean= true
    lateinit var showPace:TextView
    lateinit var showDistance:TextView
    var timeSeconds:Float =0F
    private lateinit var showTime:TextView
    lateinit var points: ArrayList<com.mapbox.geojson.Point>
    var mIter:Int = 0



    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_run, container, false)
        //Firebase
        distance_run_save = true

        setupFirebaseAuth()
        timer = view.findViewById(R.id.show_time)
        timer.typeface = getFont(activity!!.applicationContext,bangers)
        showTime= view.findViewById(R.id.show_time)
        showPace = view.findViewById(R.id.show_pace)
        showDistance = view.findViewById(R.id.show_kcal)
        timer.start()
        points = ArrayList()
        distance = 0F
        distance2 = 0f






        pauseButton = view.findViewById(R.id.pause_run_button)
        stopButton = view.findViewById(R.id.stop_run_btn)
        resumeButton = view.findViewById(R.id.resume_run_btn)
        distance_run = view.findViewById(R.id.distance_run_viewer)
        stopButton.visibility = View.GONE
        resumeButton.visibility = View.GONE


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
               getLocation()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            getLocation()
        }


        pauseButton.setOnClickListener {
            pauseButton.visibility = View.GONE
            stopButton.visibility = View.VISIBLE
            resumeButton.visibility = View.VISIBLE
            timer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - timer.base
            distance_run_save = false




        }

        resumeButton.setOnClickListener {
            pauseButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE
            resumeButton.visibility = View.GONE
            timer.base = SystemClock.elapsedRealtime() - pauseOffset
            timer.start()
            distance_run_save = true
        }
        stopButton.setOnClickListener {
            timer.stop()
            distance_run_save = false
            timer.base = SystemClock.elapsedRealtime() - pauseOffset
            val key = activityCounter +1

            Log.d("TAG", "TEN DZIWNY KLUCZ$key")

           // FirebaseHelper(activity!!.applicationContext).sendTrackFile(file_name,uri)
            if(points.size >1110 || distance>10.1){
                fragment_run_pop.visibility = View.VISIBLE
                fragment_run_button_yes.setOnClickListener {
                    val intent = Intent(activity!!.applicationContext,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity!!.finish()

                }
                fragment_run_button_no.setOnClickListener {
                    fragment_run_pop.visibility = View.GONE
                }



            }else{
                file_name = "activity$key"
                activity!!.applicationContext.openFileOutput(file_name,MODE_PRIVATE).use {
                    for(p:com.mapbox.geojson.Point in points){
                        it.write("${p.longitude()}\n".toByteArray())
                        it.write("${p.latitude()}\n".toByteArray())

                    }
                }
                val file = File(activity!!.applicationContext.filesDir,file_name)
                val uri:Uri = Uri.fromFile(file)

                val keytoSend = FirebaseHelper(activity!!.applicationContext).addNewActivity(showTime.text.toString(),distance_run.text.toString(),showPace.text.toString(),file_name,uri,kcal)


                val intent = Intent(activity!!.applicationContext,SummaryActivity::class.java)
                intent.putExtra("key", keytoSend)
                //intent.putExtra("url",url)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity!!.finish()

            }






        }






        return view
    }
    private fun getTimeInSeconds():Float{
        timeSeconds = ((SystemClock.elapsedRealtime() - timer.base)/1000).toFloat()
        Log.d(TAG, "timeSeconds: $timeSeconds")
        return timeSeconds

    }





    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        if(distance_run_save) {
            if (hasGps || hasNetwork) {

                if (hasGps) {
                    Log.d("CodeAndroidLocation", "hasGps")
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        3000,
                        0F,
                        object :
                            LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    Log.d("OnLocationChanged: ", "Tu jestem")
                                    lastLocationGps = locationGps
                                    locationGps = location
                                }

                                if (distance2 < 1.0) {
                                    if (distance_run_save) {
                                        Log.d(TAG, "Jestem w true")
                                        distance += locationGps!!.distanceTo(lastLocationGps) / 1000
                                        distance2 += locationGps!!.distanceTo(lastLocationGps)
                                        points.add(com.mapbox.geojson.Point.fromLngLat(
                                            locationGps!!.longitude,
                                        locationGps!!.latitude))
                                        Log.d(TAG, "RunFragment points: $points")


                                        // seconds += 3
                                        timeSeconds = getTimeInSeconds()


                                    }

                                } else {
                                    if (distance_run_save) {
                                        Log.d(TAG, "Jestem w true 2")
                                        distance += locationGps!!.distanceTo(lastLocationGps) / 1000
                                        distance_run.text = String.format("%.2f", distance)
                                        points.add(com.mapbox.geojson.Point.fromLngLat(
                                            locationGps!!.longitude,
                                            locationGps!!.latitude))

                                        timeSeconds = getTimeInSeconds()
                                        kcal = (1 * 80 * distance).toInt()
                                        showPace.text = String.format(
                                            "%.2f",
                                            60 / (distance / (timeSeconds / 3600))
                                        )
                                        showDistance.text = timeSeconds.toString()
                                        Log.d(TAG, "sekundy: " + distance / timeSeconds)


                                    }


                                }

                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {

                            }

                            override fun onProviderEnabled(provider: String?) {

                            }

                            override fun onProviderDisabled(provider: String?) {

                            }

                        })

                    val localGpsLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (localGpsLocation != null)
                        locationGps = localGpsLocation
                }

            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(activity!!.applicationContext,permissionArray[i]) == PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                getLocation()

        }

    }


    private fun setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference
        mFirebaseHelper = FirebaseHelper(activity!!.baseContext)


        mAuthListener = FirebaseAuth.AuthStateListener {
            fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = firebaseAuth.currentUser

                if (user != null) {
                    //User is signed in
                    Log.d(ImageLoader.TAG,"Signed_in")


                } else {
                    // User is signed out
                    Log.d(ImageLoader.TAG,"Signed_out")
                }
                // ...
            }
            myRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                   // activityCounter = FirebaseHelper(activity!!.applicationContext).activityCounter(dataSnapshot)


                }

            })



        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)

    }

    // FIREBASE CONFIG

/*
    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.finish()

    }

*/


}
