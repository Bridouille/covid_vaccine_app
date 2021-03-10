package com.covid.vaccination.main

import com.covid.vaccination.main.MainEvent.DataLoaded
import com.spotify.mobius.Next

object MainLogic {

    fun update(currentState: ViewState, event: Event): Next<ViewState, Effect> {
        return when (event) {
            is DataLoaded -> onDataLoaded(currentState, event)
        }
    }

    private fun onDataLoaded(
        currentState: ViewState,
        event: DataLoaded
    ): Next<ViewState, Effect> {
        return Next.next(currentState.copy(
            data = event.data
        ))
    }
}