package com.synergygfs.desiredvacations

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import java.util.*


class ParcelableUtils {
    companion object {

        fun marshall(parcelable: Parcelable): ByteArray {
            val parcel = Parcel.obtain()
            parcelable.writeToParcel(parcel, 0)
            val bytes = parcel.marshall()
            parcel.recycle()
            return bytes
        }

        private fun unmarshall(bytes: ByteArray): Parcel {
            val parcel = Parcel.obtain()
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0) // This is extremely important!
            return parcel
        }

        fun <T> unmarshall(bytes: ByteArray, creator: Creator<T>): T {
            val parcel = unmarshall(bytes)
            val result = creator.createFromParcel(parcel)
            parcel.recycle()
            return result
        }

        fun Parcel.writeDate(date: Date?) {
            writeLong(date?.time ?: -1)
        }

        fun Parcel.readDate(): Date? {
            val long = readLong()
            return if (long != -1L) Date(long) else null
        }

    }
}