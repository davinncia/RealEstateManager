package com.openclassrooms.realestatemanager.model_ui

import com.google.android.gms.maps.model.LatLng

data class PropertyMarker(val latLng: LatLng, val title: String, val id: Int)