package com.lchowaniec.letsrunmate_final.Run


import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.snapshotter.MapSnapshotter
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.fragment_summary.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class SummaryFragment : Fragment(),OnMapReadyCallback {

    private lateinit var mapboxMap: MapboxMap





    //firebase config
    //firebase config
    private  var activity_h:Activity = Activity()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseHelper: FirebaseHelper
    //tv config
    private lateinit var mPace:TextView
    private lateinit var mKcal:TextView
    private lateinit var mTime:TextView
    private lateinit var mDistance:TextView
    lateinit var key:String
    lateinit var point:String
    var arrayTest:ArrayList<Pair<Double,Double>> = ArrayList()
    var pointArray: ArrayList<Point> = ArrayList()
    private lateinit var mUrl:String
    private lateinit var mProgressBar:ProgressBar
    private lateinit var mMapView:MapView
    lateinit var  mapSnapshotter: MapSnapshotter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_summary, container, false)
        //param key
        key = arguments!!.getString("key")!!
        //Firebase
        mProgressBar = view.findViewById(R.id.summary_progress_bar)
        mProgressBar.visibility = View.VISIBLE
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference
        mFirebaseHelper = FirebaseHelper(activity!!.baseContext)
        //text fields
        mPace = view.findViewById(R.id.show_pace)
        mKcal = view.findViewById(R.id.show_kcal)
        mTime = view.findViewById(R.id.show_time)
        mDistance= view.findViewById(R.id.distance_tv)
        //font
        val myFont = ResourcesCompat.getFont(activity!!.applicationContext,R.font.bangers)
        val splits = view.findViewById<TextView>(R.id.summary_splitstext)
        splits.typeface = myFont
        mDistance.typeface = myFont
        setupFirebaseAuth()
        //mapbox
        mMapView = view.findViewById(R.id.mapViewSummary)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)









        return view
    }
    open fun screen(view:View):Bitmap{
        val bitmap = Bitmap.createBitmap(mMapView.width,mMapView.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }



    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        lateinit var mapSnapshotter:MapSnapshotter


        mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/lukasz-ch/ck59kuwvd0diw1cru5hrl6f7d"), object : Style.OnStyleLoaded {


            override fun onStyleLoaded(style: Style) {
                Handler().postDelayed({
                    Log.d(TAG, "NO KURDE ON STYLELOADED JUZ$pointArray")
                    style.addSource(object : GeoJsonSource("line-source", FeatureCollection.fromFeatures(arrayOf<Feature>(
                        Feature.fromGeometry(

                            LineString.fromLngLats(pointArray)
                        )

                    )
                    )
                    ){})

                    style.addLayer(LineLayer("linelayer","line-source").withProperties(
                       // PropertyFactory.lineDasharray(arrayOf<Float>(0.01f,2f)),
                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                        PropertyFactory.lineWidth(5f),
                        PropertyFactory.lineColor(Color.parseColor("#e55e5e"))




                    ))

                    val locationOne = LatLng(pointArray[0].latitude(), pointArray[0].longitude())
                    val locationTwo = LatLng(
                        pointArray[pointArray.size-1].latitude(),
                        pointArray[pointArray.size-1].longitude())
                    val coordinates = LatLngBounds.Builder()
                        .include(locationOne)
                        .include(locationTwo)
                        .build()
                    mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(coordinates,250),10)
                    Handler().postDelayed({
mapboxMap.snapshot {
    val bitmap = it

    lateinit var  bmpUri:Uri
    val file = File(activity!!.applicationContext.filesDir,"${System.currentTimeMillis()}.png")
    val out :FileOutputStream
    try{
        out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG,100,out)
        try{
            out.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        bmpUri = Uri.fromFile(file)
        mFirebaseHelper.addPhoto(bmpUri,key)
    }catch (e: FileNotFoundException){
        e.printStackTrace()
    }

}

                    },2000)





                   /* val bit = mapboxMap.style!!.getImage("line-source")
                    mapboxMap.snapshot { snapshot ->
                    Handler().postDelayed(Runnable {

                                val bitmap = bit
                                lateinit var bmpUri:Uri
                                val file = File(activity!!.applicationContext.filesDir,"${System.currentTimeMillis()}.png")
                                val out :FileOutputStream
                                try{
                            out = FileOutputStream(file)
                            bitmap!!.compress(Bitmap.CompressFormat.PNG,100,out)
                            try{
                                out.close()
                            }catch (e:IOException){
                                e.printStackTrace()
                            }
                            bmpUri = Uri.fromFile(file)
                         mFirebaseHelper.addPhoto(bmpUri,key)
                        }catch (e: FileNotFoundException){
                            e.printStackTrace()
                        }





                    },2000)*/
                    //}















                },5000)
                }})}



    private fun setActivityDetails(activity:Activity ){

        mPace.text = activity.avgPace
        mKcal.text = activity.caption
        mTime.text = activity.duration_time
        mDistance.text = String.format("%.2f", activity.distance)
        mKcal.text = activity.kcal.toString()
        mUrl = activity.url
        Log.d(TAG, "TUTAJ TERAZ SPRAWDZAM$mUrl")
        if(mUrl =="") {
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                   activity_h = mFirebaseHelper.getActivityDetails(dataSnapshot, activity.activity_id)
                    setActivityDetails(activity_h)


            }

            })
        }else{

            pointArray = mFirebaseHelper.allCoordinates(mUrl)
            Log.d(TAG, "summaryFragment: $pointArray")
            Mapbox.getInstance(context!!, getString(R.string.mapboxkey))



        }







    }

    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()


        mAuthListener = FirebaseAuth.AuthStateListener {
            fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = firebaseAuth.currentUser

                if (user != null) {
                    //User is signed in
                    Log.d(ImageLoader.TAG, "Signed_in")

                } else {
                    // User is signed out
                    Log.d(ImageLoader.TAG, "Signed_out")
                }
                // ...
            }
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    activity_h = FirebaseHelper(activity!!.applicationContext).getActivityDetails(dataSnapshot,key)
                    setActivityDetails(activity_h)




                }

            })
        }
    }


    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
        mapViewSummary.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
        mapViewSummary.onStop()

    }

    override fun onResume() {
        super.onResume()
        mapViewSummary.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapViewSummary.onPause()
    }



    override fun onLowMemory() {
        super.onLowMemory()
        mapViewSummary.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapViewSummary.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewSummary.onSaveInstanceState(outState)
    }


}
