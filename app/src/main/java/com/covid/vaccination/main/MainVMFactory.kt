package com.covid.vaccination.main

import androidx.lifecycle.*
import com.covid.vaccination.database.models.VaccinationData
import com.covid.vaccination.prefs.PrefHelper
import com.spotify.mobius.First
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.Update
import com.spotify.mobius.android.AndroidLogger
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.functions.Function
import com.spotify.mobius.rx2.RxMobius
import javax.inject.Inject

typealias MainVM = MobiusLoopViewModel<ViewState, Event, Effect, ViewEffect>

class MainVMFactory @Inject constructor(
    private val effectHandler: MainEffectHandler,
    private val prefHelper: PrefHelper
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MobiusLoopViewModel.create(
            provideLoopFactory(),
            MainViewState(),
            { model ->
                val setOfEffects = mutableSetOf<MainEffect>()

                First.first(model, setOfEffects.apply {
                    add(MainEffect.LoadData())
                    if (prefHelper.lastRefreshMoreThanADayAgo()) {
                        add(MainEffect.RefreshDataFromNetwork)
                    }
                })
            }
        ) as T
    }

    private fun provideLoopFactory() =
        Function<Consumer<ViewEffect>, MobiusLoop.Factory<ViewState, Event, Effect>> { consumer ->
            RxMobius.loop(
                Update(MainLogic::update),
                effectHandler.create(consumer)
            ).logger(AndroidLogger.tag("main-mobius"))
        }
}

internal typealias Event = MainEvent
internal typealias Effect = MainEffect
internal typealias ViewEffect = MainViewEffect
internal typealias ViewState = MainViewState

sealed class MainEvent {
    object RefreshClicked : MainEvent()
    object DataRefreshed : MainEvent()
    object DataRefreshFailed : MainEvent()

    data class QueryChanged(val query: String) : MainEvent()

    data class DataLoaded(
        val query: String,
        val data: List<VaccinationData>,
        val lastRefreshed: Long?
    ) : MainEvent()
}

sealed class MainEffect {
    object RefreshDataFromNetwork : MainEffect()
    object ErrorWhileRefreshingData : MainEffect()
    data class LoadData(val query: String = "") : MainEffect()
}

sealed class MainViewEffect {
    object ErrorWhileRefreshingData : MainViewEffect()
}

data class MainViewState(
    val query: String = "",
    val isRefreshingData: Boolean = false,
    val lastRefreshed: Long? = null,

    val data: List<VaccinationData> = listOf()
)