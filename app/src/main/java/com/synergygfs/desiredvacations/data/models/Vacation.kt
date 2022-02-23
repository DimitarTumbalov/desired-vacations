package com.synergygfs.desiredvacations.data.models

import java.util.*

data class Vacation(
    var id: Int = -1,
    val name: String = "",
    val hotelName: String = "",
    val location: String = "",
    val date: Date? = null,
    val necessaryMoneyAmount: Int? = null,
    val description: String = "",
    val imageName: String? = null
)