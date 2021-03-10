package com.covid.vaccination.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.covid.vaccination.database.models.VaccinationData
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDataDao {

    companion object {
        const val TABLE_NAME = "vaccination_data"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVaccinationData(list: List<VaccinationData>)

    @Query("SELECT * FROM $TABLE_NAME WHERE iso_code = :isoCode")
    fun getVaccinationDataForCountry(isoCode: String) : Observable<List<VaccinationData>>
}