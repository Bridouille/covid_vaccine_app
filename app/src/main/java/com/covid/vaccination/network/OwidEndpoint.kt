package com.covid.vaccination.network

import com.covid.vaccination.network.models.CountryVaccination
import retrofit2.http.GET

interface OwidEndpoint {

    @GET("/data/vaccinations/vaccinations.json")
    suspend fun getVaccinationData() : List<CountryVaccination>
}