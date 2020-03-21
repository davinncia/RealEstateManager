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
        //poi
) /*:Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!,
            arrayListOf<Boolean>().apply {
                //parcel.readList(this, Boolean::class.java.classLoader)
                parcel.readArrayList(Boolean::class.java.classLoader)
            },
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(minPrice)
        parcel.writeInt(maxPrice)
        parcel.writeString(city)
        parcel.writeList(isSold as List<Boolean>)
        parcel.writeInt(minArea)
        parcel.writeInt(maxArea)
        parcel.writeLong(creationTime)
        parcel.writeLong(sellingTime)
        parcel.writeInt(minPictureNbr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Criteria> {
        override fun createFromParcel(parcel: Parcel): Criteria {
            return Criteria(parcel)
        }

        override fun newArray(size: Int): Array<Criteria?> {
            return arrayOfNulls(size)
        }
    }
}
*/ : Parcelable
