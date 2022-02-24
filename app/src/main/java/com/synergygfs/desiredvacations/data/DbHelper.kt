package com.synergygfs.desiredvacations.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.synergygfs.desiredvacations.UiUtils
import com.synergygfs.desiredvacations.data.VacationsContract.VacationEntity
import com.synergygfs.desiredvacations.data.models.Vacation
import java.util.*

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var sQLiteDb: SQLiteDatabase? = null

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_VACATION_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply discard the data and start over
        db.execSQL(SQL_DELETE_VACATION_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    private fun getDb(): SQLiteDatabase? {
        if (sQLiteDb == null)
            sQLiteDb = this.writableDatabase

        return sQLiteDb
    }

    fun insert(tableName: String, values: ContentValues): Long? {
        return getDb()?.insert(tableName, null, values)
    }

    @SuppressLint("Range")
    fun getVacationById(vacationId: Int): Vacation? {
        val selection = "${BaseColumns._ID} LIKE ?"
        val selectionArgs = arrayOf(vacationId.toString())

        val cursor =
            getDb()?.query(
                VacationEntity.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

        if (cursor?.moveToFirst() == true) {
            val id = cursor.getInt(cursor.getColumnIndex("_id"))
            val name = cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_NAME))
            val location =
                cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_LOCATION))
            val date = UiUtils.convertStringToDate(
                cursor.getString(
                    cursor.getColumnIndex(VacationEntity.COLUMN_NAME_DATE)
                )
            )
            val hotelName =
                cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_HOTEL_NAME))
            val necessaryMoneyAmount =
                cursor.getInt(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_NECESSARY_MONEY_AMOUNT))
            val description =
                cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_DESCRIPTION))
            val imageName =
                cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_IMAGE_NAME))

            val vacation = Vacation(
                id,
                name,
                location,
                date!!,
                hotelName,
                necessaryMoneyAmount,
                description,
                imageName
            )

            cursor.close()

            return vacation
        } else {
            cursor?.close()
            return null
        }
    }

    @SuppressLint("Range")
    fun getAllVacations(): Vector<Vacation> {
        val cursor =
            getDb()?.query(
                VacationEntity.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                "${VacationEntity.COLUMN_NAME_NAME} ASC"
            )

        val vacationsCollection = Vector<Vacation>()

        while (cursor?.moveToNext() == true) {
            try {
                val id = cursor.getInt(cursor.getColumnIndex("_id"))
                val name = cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_NAME))
                val location =
                    cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_LOCATION))
                val date = UiUtils.convertStringToDate(
                    cursor.getString(
                        cursor.getColumnIndex(VacationEntity.COLUMN_NAME_DATE)
                    )
                )
                val hotelName =
                    cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_HOTEL_NAME))
                val necessaryMoneyAmount =
                    cursor.getInt(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_NECESSARY_MONEY_AMOUNT))
                val description =
                    cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_DESCRIPTION))
                val imageName =
                    cursor.getString(cursor.getColumnIndex(VacationEntity.COLUMN_NAME_IMAGE_NAME))

                val vacation = Vacation(
                    id,
                    name,
                    location,
                    date!!,
                    hotelName,
                    necessaryMoneyAmount,
                    description,
                    imageName
                )
                vacationsCollection.add(vacation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        cursor?.close()

        return vacationsCollection
    }

    fun updateVacation(values: ContentValues): Int? {
        val selection = "${BaseColumns._ID} LIKE ?"
        val selectionArgs = arrayOf(values.getAsString("_id"))
        return getDb()?.update(VacationEntity.TABLE_NAME, values, selection, selectionArgs)
    }

    fun deleteVacationById(id: Int): Int? {
        val selection = "${BaseColumns._ID} LIKE ?"
        val selectionArgs = arrayOf(id.toString())
        return getDb()?.delete(VacationEntity.TABLE_NAME, selection, selectionArgs)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "DesiredVacations.db"

        private const val SQL_CREATE_VACATION_ENTRIES =
            "CREATE TABLE IF NOT EXISTS ${VacationEntity.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${VacationEntity.COLUMN_NAME_NAME} TEXT NOT NULL," +
                    "${VacationEntity.COLUMN_NAME_LOCATION} TEXT NOT NULL," +
                    "${VacationEntity.COLUMN_NAME_DATE} TEXT NOT NULL," +
                    "${VacationEntity.COLUMN_NAME_HOTEL_NAME} TEXT," +
                    "${VacationEntity.COLUMN_NAME_NECESSARY_MONEY_AMOUNT} INTEGER," +
                    "${VacationEntity.COLUMN_NAME_DESCRIPTION} TEXT," +
                    "${VacationEntity.COLUMN_NAME_IMAGE_NAME} TEXT )"

        private const val SQL_DELETE_VACATION_ENTRIES =
            "DROP TABLE IF EXISTS ${VacationEntity.TABLE_NAME}"
    }
}