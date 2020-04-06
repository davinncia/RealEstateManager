package com.openclassrooms.realestatemanager.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Criteria (
        val minPrice: Int,
        val maxPrice: Int,
        var city: String,
        //To be interpreted via SQL later
        //(true, true) -> sold | (true, false) -> sold & onSale | (false, false) -> onSale
        var isSold: ArrayList<Boolean>,
        val minArea: Int,
        val maxArea: Int,
        //0 when not changed
        val minCreationTime: Long,
        val sellingTime: Long,
        val minPictureNbr: Int,
        val poiNames: List<String>
) : Parcelable
