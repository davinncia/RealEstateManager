package com.openclassrooms.realestatemanager.property_map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.property_details.DetailsActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MapsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        viewModel  = ViewModelProvider(this, ViewModelFactory(this.application)).get(MapsViewModel::class.java)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        checkLocationPermission()

        mMap.setOnInfoWindowClickListener {
            navigateToPropertyDetails(it)
        }

        viewModel.propertiesLiveData.observe(this, Observer {
            placeMarkers(it)
        })
    }

    private fun activateLocalization(){

            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationButtonClickListener(this)

    }


    private fun placeMarkers(properties: List<Property>) {

        for (item in properties){
            val latLng = LatLng(item.address.latitude, item.address.longitude)
            mMap.addMarker(MarkerOptions()
                    .position(latLng)
                    .title(item.type))
                    .tag = item.id
        }
    }


    private fun navigateToPropertyDetails(marker: Marker){
        Toast.makeText(this, "Navigating...", Toast.LENGTH_SHORT).show()
        val strId = marker.tag.toString()
        viewModel.selectProperty(strId.toInt())
        //Return to last DetailsActivity in task & cleaning BackStack
        startActivity(DetailsActivity.newIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }


    //--------------------------------------------------------------------------------------//
    //                                    P E R M I S S I O N
    //--------------------------------------------------------------------------------------//

    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Not granted yet
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        } else
            activateLocalization()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //Not granted, exit map then
                    finish()
                } else
                    activateLocalization()
            }
        }
    }

    //--------------------------------------------------------------------------------------//
    //                                   C O M P A N I O N
    //--------------------------------------------------------------------------------------//

    companion object {
        const val LOCATION_REQUEST_CODE = 100

        fun newIntent(context: Context) = Intent(context, MapsActivity::class.java)
    }

}
