package com.covid.vaccination.main

import com.covid.vaccination.database.VaccinationDataDao
import com.covid.vaccination.database.models.VaccinationData
import com.covid.vaccination.network.OwidEndpoint
import com.covid.vaccination.prefs.PrefHelper
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.ObservableTransformer
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface MainEffectHandler {
    fun create(consumer: Consumer<ViewEffect>): ObservableTransformer<Effect, Event>
}

class DictEffectHandlerImpl @Inject constructor(
    private val endpoint: OwidEndpoint,
    private val vaccDataDao: VaccinationDataDao,
    private val prefHelper: PrefHelper
) : MainEffectHandler {

    override fun create(consumer: Consumer<ViewEffect>): ObservableTransformer<Effect, Event> {
        return RxMobius.subtypeEffectHandler<Effect, Event>()
            .addTransformer(MainEffect.LoadData::class.java) {
                it.switchMap { event ->
                    vaccDataDao.getAllVaccinationData()
                        .map {
                            it.groupBy { it.country }
                                .map { it.value.last() }
                                .filter { it.country.contains(event.query, ignoreCase = true) }
                                .filter { it.peopleFullyVaccinatedPerHundred != null }
                        }
                        .map {
                            MainEvent.DataLoaded(event.query, it, prefHelper.getLastRefreshDate())
                        }
                }
            }
            .addTransformer(MainEffect.RefreshDataFromNetwork::class.java) {
                it.flatMap {
                    endpoint.getVaccinationData()
                        .toObservable()
                        .map<MainEvent> {
                            val vaccinationData = it.flatMap { countryVaccination ->
                                countryVaccination.dataOfCountry.mapNotNull {
                                    VaccinationData.fromVaccinationResponse(countryVaccination, it)
                                }
                            }
                            // Insert the data in the database
                            vaccDataDao.insertVaccinationData(vaccinationData)
                            prefHelper.setLastRefresh()
                            MainEvent.DataRefreshed
                        }
                        .onErrorReturnItem(MainEvent.DataRefreshFailed)
                }
            }
            .addConsumer(MainEffect.ErrorWhileRefreshingData::class.java) {
                consumer.accept(MainViewEffect.ErrorWhileRefreshingData)
            }
            .build()
    }
}