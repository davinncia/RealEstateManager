package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.io.IOException


class AddressConverter {

    /**
     * Util function converting a string address into a LatLng object.
     * Returns LatLng(0.0, 0.0) if didn't find a match
     */
    fun getLatLng(context: Context, strAddress: String): LatLng {

        val geoCoder = Geocoder(context)
        val address: List<Address>
        var latLng = LatLng(0.0, 0.0)

        try {
            address = geoCoder.getFromLocationName(strAddress, MAX_RESPONSES)
            if (address.isEmpty()) {
                Log.e("Error", "No address found")
                return latLng
            }

            latLng = LatLng(address[0].latitude, address[0].longitude)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return latLng
    }

    companion object {
        private const val MAX_RESPONSES = 1
    }
}