package com.covid.vaccination.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryVaccination(
    @Json(name = "country")
    val country: String,

    @Json(name = "iso_code")
    val isoCode: String,

    @Json(name = "data")
    val dataOfCountry: List<CountryVaccinationData>
)

@JsonClass(generateAdapter = true)
data class CountryVaccinationData(
    @Json(name = "date")
    val date: String? = null,

    @Json(name = "total_vaccinations")
    val totalVaccinations: Long? = null,

    @Json(name = "total_vaccinations_per_hundred")
    val totalVaccinationsPerHundred: Double? = null,

    @Json(name = "daily_vaccinations_raw")
    val dailyVaccinationRaw: Long? = null,

    @Json(name = "daily_vaccinations")
    val dailyVaccinations: Long? = null,

    @Json(name = "daily_vaccinations_per_million")
    val dailyVaccinationsPerMillion: Double? = null,

    @Json(name = "people_vaccinated")
    val peopleVaccinated: Long? = null,

    @Json(name = "people_vaccinated_per_hundred")
    val peopleVaccinatedPerHundred: Double? = null,

    @Json(name = "people_fully_vaccinated")
    val peopleFullyVaccinated: Long? = null,

    @Json(name = "people_fully_vaccinated_per_hundred")
    val peopleFullyVaccinatedPerHundred: Double? = null,
)