package com.synergygfs.desiredvacations.data

import android.provider.BaseColumns

object VacationsContract {

    object VacationEntity : BaseColumns {
        const val TABLE_NAME = "desired_vacations"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_HOTEL_NAME = "hotel_name"
        const val COLUMN_NAME_LOCATION = "location"
        const val COLUMN_NAME_DATE = "date"
        const val COLUMN_NAME_NECESSARY_MONEY_AMOUNT = "necessary_money_amount"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_IMAGE_NAME = "image_name"
    }

}