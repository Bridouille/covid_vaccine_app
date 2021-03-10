package com.covid.vaccination.main

import com.covid.vaccination.main.MainEffect.ErrorWhileRefreshingData
import com.covid.vaccination.main.MainEffect.RefreshDataFromNetwork
import com.covid.vaccination.main.MainEvent.*
import com.spotify.mobius.Next

object MainLogic {

    fun update(currentState: ViewState, event: Event): Next<ViewState, Effect> {
        return when (event) {
            is DataLoaded -> onDataLoaded(currentState, event)
            is RefreshClicked -> onRefreshClicked(currentState)
            is DataRefreshed -> onDataRefreshed(currentState)
            is DataRefreshFailed -> onDataRefreshFailed(currentState)
            is QueryChanged -> onQueryChanged(currentState, event)
        }
    }

    private fun onDataLoaded(
        currentState: ViewState,
        event: DataLoaded
    ): Next<ViewState, Effect> {
        return Next.next(currentState.copy(
            query = event.query,
            data = event.data,
            lastRefreshed = event.lastRefreshed
        ))
    }

    private fun onRefreshClicked(
        currentState: ViewState
    ): Next<ViewState, Effect> {
        return Next.next(
            currentState.copy(isRefreshingData = true),
            setOf(RefreshDataFromNetwork)
        )
    }

    private fun onDataRefreshed(
        currentState: ViewState
    ): Next<ViewState, Effect> {
        return Next.next(currentState.copy(isRefreshingData = false))
    }

    private fun onDataRefreshFailed(
        currentState: ViewState
    ): Next<ViewState, Effect> {
        return Next.next(
            currentState.copy(isRefreshingData = false),
            setOf(ErrorWhileRefreshingData)
        )
    }

    private fun onQueryChanged(
        currentState: ViewState,
        event: QueryChanged
    ): Next<ViewState, Effect> {
        return Next.next(
            currentState.copy(query = event.query),
            setOf(MainEffect.LoadData(event.query))
        )
    }
}