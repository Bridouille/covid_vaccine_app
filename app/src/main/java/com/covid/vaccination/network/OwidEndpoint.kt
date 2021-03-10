package com.covid.vaccination.network

import com.covid.vaccination.network.models.CountryVaccination
import io.reactivex.Single
import retrofit2.http.GET

interface OwidEndpoint {

    @GET("/data/vaccinations/vaccinations.json")
    fun getVaccinationData() : Single<List<CountryVaccination>>
}