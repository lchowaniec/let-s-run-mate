package com.lchowaniec.letsrunmate_final.History


import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.fragment_history_summary.*

/**
 * A simple [Fragment] subclass.
 */
open class HistorySummaryFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mPace: TextView
    private lateinit var mKcal: TextView
    private lateinit var mTime: TextView
    private lateinit var mDistance: TextView
    lateinit var mProgressBar:ProgressBar
    private lateinit var mapboxMap: MapboxMap
    var pointArray:ArrayList<com.mapbox.geojson.Point> = ArrayList()
    private lateinit var mToolbar:androidx.appcompat.widget.Toolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View =  inflater.inflate(R.layout.fragment_history_summary, container, false)
        val activity = arguments!!.getSerializable("Activity")
        //text fields
        mToolbar = view.findViewById(R.id.historysummary_app_bar)
        mPace = view.findViewById(R.id.show_pace)
        mKcal = view.findViewById(R.id.show_kcal)
        mTime = view.findViewById(R.id.show_time)
        mDistance= view.findViewById(R.id.distance_tv)
        mProgressBar = view.findViewById(R.id.historysummary_progress_bar)
        mProgressBar.visibility  = View.VISIBLE

        //font
        val myFont = ResourcesCompat.getFont(getActivity()!!.applicationContext,R.font.bangers)
        val splits = view.findViewById<TextView>(R.id.summary_splitstext)
        splits.typeface = myFont
        mDistance.typeface = myFont
        val obj: Activity = activity as Activity
        setActivityDetails(obj)

        //mapbox
        Mapbox.getInstance(getActivity()!!.applicationContext, getString(R.string.mapboxkey))
        val mMapView = view.findViewById<MapView>(R.id.mapViewSummaryHistory)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)



        return view
    }
    private fun setActivityDetails(mActivity: Activity) {

        val activityDetails = mActivity
        mPace.text = activityDetails.avgPace
        mTime.text = activityDetails.duration_time
        mDistance.text = activityDetails.distance
        mKcal.text = activityDetails.kcal.toString()
        pointArray = FirebaseHelper(activity!!.applicationContext).allCoordinates(activityDetails.url)
        mToolbar.title = activityDetails.date

    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        Log.d(TAG,"A TU W OGOLE JESTEM ?")

        mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/lukasz-ch/ck59kuwvd0diw1cru5hrl6f7d"), object : Style.OnStyleLoaded {


            override fun onStyleLoaded(style: Style) {
                    Log.d(TAG, "NO KURDE ON STYLELOADED JUZ$pointArray")
                    style.addSource(object : GeoJsonSource("line-source", FeatureCollection.fromFeatures(arrayOf<Feature>(
                        Feature.fromGeometry(

                            LineString.fromLngLats(pointArray)
                        )

                    )
                    )
                    ){})
                    style.addLayer(
                        LineLayer("linelayer","line-source").withProperties(
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

                mProgressBar.visibility = View.GONE
            }}   )}


    override fun onStart() {
        super.onStart()
        mapViewSummaryHistory.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapViewSummaryHistory.onStop()

    }

    override fun onResume() {
        super.onResume()
        mapViewSummaryHistory.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapViewSummaryHistory.onPause()
    }



    override fun onLowMemory() {
        super.onLowMemory()
        mapViewSummaryHistory.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapViewSummaryHistory.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewSummaryHistory.onSaveInstanceState(outState)
    }


}
