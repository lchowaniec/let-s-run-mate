package com.lchowaniec.letsrunmate_final.Run


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lchowaniec.letsrunmate_final.R
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.fragment_map.*

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), OnMapReadyCallback, PermissionsListener {
    lateinit var mapboxMap: MapboxMap
    private var permissionManager = PermissionsManager(this)




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View =  inflater.inflate(R.layout.fragment_map, container, false)
        val mapview = view.findViewById<MapView>(R.id.mapview)

        Mapbox.getInstance(activity!!.applicationContext, getString(R.string.mapboxkey))
        mapview.onCreate(savedInstanceState)

        mapview.getMapAsync(this)









        return view
    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.Builder().fromUri(
                "mapbox://styles/lukasz-ch/ck59kuwvd0diw1cru5hrl6f7d")){
            enableLocationComponent(it)
        }
    }
    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style){
        if(PermissionsManager.areLocationPermissionsGranted(activity)){
            val customLocationComponentOptions = LocationComponentOptions.builder(activity!!.applicationContext)
                .trackingGesturesManagement(true)
                .accuracyColor(
                    ContextCompat.getColor(activity!!.applicationContext,
                        R.color.black
                    ))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(activity!!.applicationContext,loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            // get instant and next adjust settings
            mapboxMap.locationComponent.apply {

                //Aktywacja LocationComponent z ustawieniami powyżej
                activateLocationComponent(locationComponentActivationOptions)

                //Ustawianie LocationComponent na 'visible'
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
            }
        }else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(activity)

        }
    }

    override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<String>,grantResults:IntArray){
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(activity!!.applicationContext,"Can't user location service", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted){
            enableLocationComponent(mapboxMap.style!!)
        }else{
            Toast.makeText(activity!!.applicationContext,"Please turn on location services", Toast.LENGTH_LONG).show()
            requireActivity().finish()
        }
    }

     override fun onStart() {
        super.onStart()
        mapview.onStart()
    }

     override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

     override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

     override fun onStop() {
        super.onStop()
        mapview.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapview.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapview.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapview.onSaveInstanceState(outState)
    }


}
