package com.synergygfs.desiredvacations.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Vacation(
    var id: Int = -1,
    val name: String,
    val location: String,
    val date: Date,
    val hotelName: String? = null,
    val necessaryMoneyAmount: Int? = null,
    val description: String? = null,
    val imageName: String? = null
) : Parcelable