package com.synergygfs.desiredvacations

import java.util.*

class UiUtils {

    companion object {
        fun convertStringToDate(dateString: String): Date? {
            return Constants.formatter.parse(dateString)
        }

        fun convertDateToString(date: Date): String {
            return Constants.formatter.format(date).toString()
        }
    }

}