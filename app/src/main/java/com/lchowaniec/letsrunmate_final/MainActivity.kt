package com.lchowaniec.letsrunmate_final

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.lchowaniec.letsrunmate_final.Login_Register.StartActivity
import com.lchowaniec.letsrunmate_final.Run.RunActivityController
import com.lchowaniec.letsrunmate_final.utils.BottomNaviViewHelper
import com.lchowaniec.letsrunmate_final.utils.ImageLoader
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity(),OnMapReadyCallback,PermissionsListener {
    private lateinit var mAuth: FirebaseAuth
    private var ACTIVITY_NUM = 0
    private var permissionManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
   // private lateinit var scrollView:HorizontalScrollView = findViewById(R.id.scrollViewRuns)


    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(applicationContext,getString(R.string.mapboxkey))
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )





        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
       val mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_page_toolbar)
        setSupportActionBar(mToolbar)
        initImageLoader()
        supportActionBar?.title = "Let's run mate!   "
        mAuth = FirebaseAuth.getInstance()

        setupBottomNavigationBar()
        scrollViewRuns.isHorizontalScrollBarEnabled = false
        basic_run_btn.setOnClickListener {
            val intent = Intent(this,RunActivityController::class.java)
            startActivity(intent)
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if(currentUser == null) {
            showStartScreen()
        }else{
            updateOnline()
        }
        mapView.onStart()
       // scrollView.scrollTo(100,0)

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.Builder().fromUri(
                "mapbox://styles/lukasz-ch/ck59kuwvd0diw1cru5hrl6f7d")){
            enableLocationComponent(it)
        }
    }
    private fun updateOnline(){
        val ref = FirebaseDatabase.getInstance().reference
        val time = System.currentTimeMillis()
        ref.child(getString(R.string.firebase_user_details))
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(getString(R.string.last_online))
            .setValue(time)
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style){
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(
                    ContextCompat.getColor(this,
                        R.color.black
                    ))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this,loadedMapStyle)
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
            permissionManager.requestLocationPermissions(this)

        }
    }

    override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<String>,grantResults:IntArray){
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this,"Can't user location service", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted){
            enableLocationComponent(mapboxMap.style!!)
        }else{
            Toast.makeText(this,"Please turn on location services", Toast.LENGTH_LONG).show()
            finish()
        }
    }



    private fun showStartScreen(){
        val startIntent = Intent(this,
            StartActivity::class.java)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(startIntent)

    }

    private fun initImageLoader(){
        val imageLoader = ImageLoader(this)
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(imageLoader.getConfig())

    }

    
    private fun setupBottomNavigationBar(){

        val bottomNaviBar : BottomNavigationView = findViewById(R.id.bottomNaviViewBar)
        BottomNaviViewHelper().setupBottomNaviView(bottomNaviBar)
        BottomNaviViewHelper().enableNavigationBar(baseContext,bottomNaviBar)
        val menu: Menu = bottomNaviBar.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true




    }




}
