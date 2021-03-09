package com.covid.vaccination.main

import androidx.lifecycle.*
import com.covid.vaccination.database.VaccinationDataDao
import com.covid.vaccination.database.models.VaccinationData
import com.covid.vaccination.network.OwidEndpoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainVMFactory @Inject constructor(
    private val endpoint: OwidEndpoint,
    private val vaccDataDao: VaccinationDataDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainVM(endpoint, vaccDataDao) as T
    }
}

class MainVM constructor(
    private val endpoint: OwidEndpoint,
    private val vaccDataDao: VaccinationDataDao
) : ViewModel() {

    private val vs = MutableLiveData(ViewState())
    val viewState: LiveData<ViewState> = vs

    init {
        refreshData()
        viewModelScope.launch {
            vaccDataDao.getVaccinationDataForCountry("SWE")
                .distinctUntilChanged()
                .collect { list ->
                    vs.value = vs.value?.copy(data = list)
                }
        }
    }

    fun refreshData() {
        // TODO: how to ensure only one network call at any time?
        // TODO: how to handle errors properly?
        viewModelScope.launch {
            vs.value = vs.value.toLoading()
            val resp = endpoint.getVaccinationData()
            val vaccinationData = resp.flatMap { countryVaccination ->
                countryVaccination.dataOfCountry.mapNotNull {
                    VaccinationData.fromVaccinationResponse(countryVaccination, it)
                }
            }
            vaccDataDao.insertVaccinationData(vaccinationData)
            vs.value = vs.value.toLoading(false)
        }
    }
}

data class ViewState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val data: List<VaccinationData> = listOf()
)

internal fun ViewState?.toLoading(
    loading: Boolean = true
) = this?.copy(isLoading = loading)