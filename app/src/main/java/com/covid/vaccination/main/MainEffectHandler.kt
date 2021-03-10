package com.covid.vaccination.main

import com.covid.vaccination.database.VaccinationDataDao
import com.covid.vaccination.network.OwidEndpoint
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.ObservableTransformer
import javax.inject.Inject

interface MainEffectHandler {
    fun create(consumer: Consumer<ViewEffect>): ObservableTransformer<Effect, Event>
}

class DictEffectHandlerImpl @Inject constructor(
    private val endpoint: OwidEndpoint,
    private val vaccDataDao: VaccinationDataDao
) : MainEffectHandler {

    override fun create(consumer: Consumer<ViewEffect>): ObservableTransformer<Effect, Event> {
        return RxMobius.subtypeEffectHandler<Effect, Event>()
            .addTransformer(MainEffect.LoadData::class.java) {
                it.flatMap {
                    vaccDataDao.getVaccinationDataForCountry("SWE")
                        .map {
                            MainEvent.DataLoaded(it)
                        }
                }
            }
            .build()
    }
}