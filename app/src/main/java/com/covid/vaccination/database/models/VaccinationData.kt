package com.covid.vaccination.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.covid.vaccination.database.VaccinationDataDao
import com.covid.vaccination.network.models.CountryVaccination
import com.covid.vaccination.network.models.CountryVaccinationData

/**
 * Data comes from
 * https://github.com/owid/covid-19-data/tree/master/public/data/vaccinations
 * https://covid.ourworldindata.org/data/vaccinations/vaccinations.json
 */
@Entity(
    tableName = VaccinationDataDao.TABLE_NAME,
    indices = [
        Index(
            value = ["iso_code", "date"],
            unique = true
        )
    ]
)
data class VaccinationData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "iso_code")
    val isoCode: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "total_vaccinations")
    val totalVaccinations: Long,

    @ColumnInfo(name = "total_vaccinations_per_hundred")
    val totalVaccinationsPerHundred: Double,

    @ColumnInfo(name = "daily_vaccinations_raw")
    val dailyVaccinationRaw: Long? = null,

    @ColumnInfo(name = "daily_vaccinations")
    val dailyVaccinations: Long? = null,

    @ColumnInfo(name = "daily_vaccinations_per_million")
    val dailyVaccinationsPerMillion: Double? = null,

    @ColumnInfo(name = "people_vaccinated")
    val peopleVaccinated: Long? = null,

    @ColumnInfo(name = "people_vaccinated_per_hundred")
    val peopleVaccinatedPerHundred: Double? = null,

    @ColumnInfo(name = "people_fully_vaccinated")
    val peopleFullyVaccinated: Long? = null,

    @ColumnInfo(name = "people_fully_vaccinated_per_hundred")
    val peopleFullyVaccinatedPerHundred: Double? = null,
) {

    companion object {
        fun fromVaccinationResponse(
            countryVaccination: CountryVaccination,
            it: CountryVaccinationData
        ) : VaccinationData? {
            if (it.date == null ||
                it.totalVaccinations == null ||
                it.totalVaccinationsPerHundred == null
            ) return null

            return VaccinationData(
                country = countryVaccination.country,
                isoCode = countryVaccination.isoCode,
                date = it.date,
                totalVaccinations = it.totalVaccinations,
                totalVaccinationsPerHundred = it.totalVaccinationsPerHundred,
                dailyVaccinationRaw = it.dailyVaccinationRaw,
                dailyVaccinations = it.dailyVaccinations,
                dailyVaccinationsPerMillion = it.dailyVaccinationsPerMillion,
                peopleVaccinated = it.peopleVaccinated,
                peopleVaccinatedPerHundred = it.peopleVaccinatedPerHundred,
                peopleFullyVaccinated = it.peopleFullyVaccinated,
                peopleFullyVaccinatedPerHundred = it.peopleFullyVaccinatedPerHundred
            )
        }
    }
}