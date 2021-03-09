package com.covid.vaccination

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.covid.vaccination.database.VaccinationDataDao
import com.covid.vaccination.database.models.VaccinationData
import com.covid.vaccination.network.OwidEndpoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var endpoint: OwidEndpoint
    @Inject lateinit var vaccDataDao: VaccinationDataDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launchWhenCreated {
            val resp = endpoint.getVaccinationData()
            val vaccinationData = resp.flatMap { countryVaccination ->
                countryVaccination.dataOfCountry.mapNotNull {
                    VaccinationData.fromVaccinationResponse(countryVaccination, it)
                }
            }
            vaccDataDao.insertVaccinationData(vaccinationData)
            Timber.d(vaccinationData.toString())
        }
    }
}