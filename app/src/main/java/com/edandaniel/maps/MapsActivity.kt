package com.edandaniel.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.edandaniel.maps.utils.PermissionUtils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

     private lateinit var mMap: GoogleMap

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    val locationPermissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun initLocationListener(){
        locationListener = object :  LocationListener {
            override fun onLocationChanged(location: Location?) {
                val myPosition = LatLng(location?.latitude!!, location?.longitude)
                addMarker(myPosition, "!")
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,12f))
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                requestLocationUpdates()
            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        PermissionUtils.validatePermission(locationPermissions.toTypedArray(),this,1)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private fun addMarker(latLgn: LatLng, title: String){
        mMap.addMarker(MarkerOptions().position(latLgn).title(title))
    }

    /*private fun addMarker(latLgn: LatLng, title: String){
        mMap.addMarker(MarkerOptions().position(latLgn).title(title).snippet(getFormattedAddress(latLgn)))
    }*/

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        initLocationListener()
        requestLocationUpdates()
        // Add a marker in Sydney and move the camera
        val fiapPaulista = LatLng(-23.5641095,-46.6545986)
        val fiapAclimacao = LatLng(-23.5671913,-46.62336)
        val fiapVilaOlimpia = LatLng(-23.595060,-46.685333)

        /*
        mMap.setOnMapClickListener {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            //addMarker(it,address[0].thoroughfare) //street name
            addMarker(it,address[0].thoroughfare,address[0].getAddressLine(0).toString()) // thoroughfare = street name
        }*/

        mMap.setOnMapClickListener {
            addMarker(it,getFormattedAddress(it))
        }

        mMap.setOnMapLongClickListener {
            addMarker(it,getFormattedAddress(it))
        }

        mMap.addMarker(MarkerOptions()
                .position(fiapPaulista)
                .title("Fiap Paulista")
                .snippet("Click here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

        mMap.addMarker(MarkerOptions()
                .position(fiapAclimacao)
                .title("Fiap Aclimação")
                .snippet(getFormattedAddress(fiapAclimacao))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.school)))

        addMarker(fiapVilaOlimpia,"Fiap Vila Olimpia")

        mMap.addCircle(CircleOptions()
                .center(fiapPaulista)
                .radius(200.0)
                .fillColor(Color.argb(0.5f,99f,33f,99f)))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiapPaulista,12f))
    }

    private fun getFormattedAddress(latLng: LatLng): String {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        val endereco = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        return "${endereco[0].thoroughfare}, ${endereco[0].subThoroughfare} " +
                "${endereco[0].subLocality}, ${endereco[0].locality} - " +
                "${endereco[0].postalCode}"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (answer in grantResults){
            if(answer == PackageManager.PERMISSION_DENIED){
                Toast.makeText(applicationContext, "NO LOCATION ACCESS PERMISSION",Toast.LENGTH_LONG).show()
            }else{
                requestLocationUpdates()
            }
        }
    }

    private fun requestLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener)
        }
    }
}
