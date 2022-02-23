package com.synergygfs.desiredvacations.data.models

import java.util.*

data class Vacation(
    var id: Int = -1,
    val name: String,
    val location: String,
    val hotelName: String? = null,
    val necessaryMoneyAmount: Int? = null,
    val description: String? = null,
    val date: Date,
    val imageName: String? = null
)