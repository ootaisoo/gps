package com.example.gpstested

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class LocationActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_ACCESS_FINE_LOCATION = 1
        const val YES = "Yes"
        const val NO = "No"
    }

    lateinit var locationManager : LocationManager

    val locationNetworkProvider: String = LocationManager.NETWORK_PROVIDER
    val locationGPSProvider: String = LocationManager.GPS_PROVIDER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initLocationManager()
        requestLocationPermissions()
        checkLocationStatus()
        setInitialLocationValues()
        setLocationListener();
    }

    private fun initLocationManager() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun requestLocationPermissions() {
        if (!locationAccessPermitted()) {

            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    private fun locationAccessPermitted(): Boolean {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }

    private fun checkLocationStatus() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.gps_disabled)
            .setCancelable(false)
            .setPositiveButton(YES, { dialog, id ->
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            })
            .setNegativeButton(NO, { dialog, id ->
                dialog.cancel()
            })
        val alert = builder.create()
        alert.show()
    }

    private fun setInitialLocationValues() {

        var lastKnownNetworkLocation: Location? = locationManager.getLastKnownLocation(locationNetworkProvider)
        var lastKnownGPSLocation: Location? = locationManager.getLastKnownLocation(locationGPSProvider)
        setCoordinates(lastKnownNetworkLocation)
    }

    private fun setCoordinates(location: Location?) {
        val longitudeText = location?.longitude.toString()
        val latitudeText = location?.latitude.toString()
        val altitudeText = location?.altitude.toString()

        longitude.setText(longitudeText)
        latitude.setText(latitudeText)
        altitude.setText(altitudeText)
    }

    private fun setLocationListener() {

        val locationListener = object : LocationListener {

            override fun onLocationChanged(location: Location?) {
                Log.e("onLocationChanged", location.toString())
                setCoordinates(location)
            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }
        }

        locationManager.requestLocationUpdates(locationNetworkProvider, 0, 0f, locationListener)
        locationManager.requestLocationUpdates(locationGPSProvider, 0, 0f, locationListener)
    }
}