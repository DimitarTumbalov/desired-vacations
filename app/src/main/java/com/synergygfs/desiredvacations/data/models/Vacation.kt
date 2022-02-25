package com.synergygfs.desiredvacations.data.models

import android.os.Parcel
import android.os.Parcelable
import com.synergygfs.desiredvacations.ParcelableUtils.Companion.readDate
import com.synergygfs.desiredvacations.ParcelableUtils.Companion.writeDate
import java.util.*

data class Vacation(
    var id: Int = -1,
    val name: String,
    val location: String,
    val date: Date,
    val hotelName: String? = null,
    val necessaryMoneyAmount: Int? = null,
    val description: String? = null,
    val imageName: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDate()!!,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeDate(date)
        parcel.writeString(hotelName)
        parcel.writeValue(necessaryMoneyAmount)
        parcel.writeString(description)
        parcel.writeString(imageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vacation> {
        override fun createFromParcel(parcel: Parcel): Vacation {
            return Vacation(parcel)
        }

        override fun newArray(size: Int): Array<Vacation?> {
            return arrayOfNulls(size)
        }
    }

}

