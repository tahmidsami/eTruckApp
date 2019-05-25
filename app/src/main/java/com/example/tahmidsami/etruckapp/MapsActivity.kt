package com.example.tahmidsami.etruckapp

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private val CAMERA_REQUEST_CODE =1

    private var mMap: GoogleMap? = null
    private var mMarker: Marker? = null

    private var latitude:Double = 0.toDouble()
    private var longitude:Double = 0.toDouble()

    private lateinit var mLastLocation:Location

    //Location
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    lateinit var locationRequest:LocationRequest
    lateinit var locationCallback:LocationCallback

    companion object {
        private const val MY_PERMISION_CODE: Int = 1000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
            buildLocationRequest()
            buildLocationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }

        //Navigation bar
        bottom_navigation_view.setOnNavigationItemReselectedListener { item ->
            when(item.itemId){
                R.id.action_post_photo -> {
                    var openCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(openCamera, CAMERA_REQUEST_CODE)
                }
                R.id.action_see_photos -> {}
            }
            true
        }

    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0!!.locations.size-1) // Get last location

                if (mMarker != null) mMarker!!.remove()

                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latlng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                        .position(latlng)
                        .title("You are here!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mMarker = mMap!!.addMarker(markerOptions)

                //Move camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(13f))

            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocationPermission():Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISION_CODE)
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISION_CODE)
            return false
        }
        else
            return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            MY_PERMISION_CODE->{
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        if (checkLocationPermission())
                            mMap!!.isMyLocationEnabled = true
                }
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Init Google play services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mMap!!.isMyLocationEnabled = true
            }
        }
        else
            mMap!!.isMyLocationEnabled = true

        //Enable zoom control
        mMap!!.uiSettings.isZoomControlsEnabled = true
    }
}
