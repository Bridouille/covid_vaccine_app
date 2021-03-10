package com.covid.vaccination.main

import androidx.lifecycle.*
import com.covid.vaccination.database.VaccinationDataDao
import com.covid.vaccination.database.models.VaccinationData
import com.covid.vaccination.network.OwidEndpoint
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
    private val effectHandler: MainEffectHandler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MobiusLoopViewModel.create(
            provideLoopFactory(),
            MainViewState(),
            { model -> First.first(model, setOf(MainEffect.LoadData)) }
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
    data class DataLoaded(val data: List<VaccinationData>) : MainEvent()
}

sealed class MainEffect {
    object LoadData : MainEffect()
}

sealed class MainViewEffect {
    object ErrorWhileRefreshingData : MainViewEffect()
}

data class MainViewState(
    val isRefreshingData: Boolean = false,

    val data: List<VaccinationData> = listOf()
)