package com.covid.vaccination.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.covid.vaccination.database.models.VaccinationData
import com.covid.vaccination.di.DatabaseModule.DB_VERSION

@Database(
    entities = [
        VaccinationData::class
    ],
    version = DB_VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vaccinationDataDao() : VaccinationDataDao
}