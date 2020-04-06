package com.openclassrooms.realestatemanager.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData


class NetworkRepository(context: Context) {

    var isConnected = MutableLiveData<Boolean>()

    private val networkRequest = NetworkRequest.Builder()
            //.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

    private var networkCallback = object : ConnectivityManager.NetworkCallback(){

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnected.postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isConnected.postValue(false)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            isConnected.postValue(false)
        }
    }

    init {
        val connectivityManager =
                context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }



    companion object {
        private var INSTANCE: NetworkRepository? = null

        fun getInstance(context: Context): NetworkRepository{
            if (INSTANCE == null){
                synchronized(NetworkRepository){
                    if (INSTANCE == null){
                        INSTANCE = NetworkRepository(context)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}