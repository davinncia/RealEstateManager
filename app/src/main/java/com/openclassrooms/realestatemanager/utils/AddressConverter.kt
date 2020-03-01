package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

//TODO NINO 3: GeoCoder, "static" function. Best practice package level ?
class AddressConverter {

    companion object {

        const val MAX_RESPONSES = 1

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
        }
    }