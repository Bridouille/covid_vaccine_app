package com.covid.vaccination.main.ui

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.covid.vaccination.R
import com.covid.vaccination.database.models.VaccinationData
import com.covid.vaccination.main.*
import com.covid.vaccination.utils.extFunctions.formatToShortNumber
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainContent(
    viewState: ViewState,
    viewEffect: ViewEffect?,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    eventSender: (Event) -> Unit,
) {
    Timber.d("viewEffect: $viewEffect - query = ${viewState.query}")

    // Creates a CoroutineScope bound to the MainContent's lifecycle
    val scope = rememberCoroutineScope()
    viewEffect?.let {
        when (it) {
            is MainViewEffect.ErrorWhileRefreshingData -> {
                val errStr = stringResource(id = R.string.error_while_fetching_data)
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(errStr)
                }
            }
        }
    }

    val lastRefreshedStr = if (viewState.isRefreshingData) {
        stringResource(id = R.string.refreshing)
    } else {
        val lastRefreshed = viewState.lastRefreshed?.let {
            DateUtils.getRelativeTimeSpanString(it, Date().time,
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        } ?: stringResource(id = R.string.never)
        stringResource(id = R.string.last_refreshed_x, lastRefreshed)
    }

    val textState = remember { mutableStateOf(TextFieldValue(viewState.query)) }

    MaterialTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                Toolbar(
                    title = stringResource(id = R.string.app_name),
                    subtitle = lastRefreshedStr,
                    onRefresh = { eventSender.invoke(MainEvent.RefreshClicked) }
                )
            },
            content = {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp
                    )) {
                    TextField(
                        value = textState.value,
                        modifier = Modifier.fillMaxWidth().padding(
                           vertical = 8.dp
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.search_country))
                        },
                        leadingIcon = {
                            Icon(Icons.Rounded.Search, contentDescription = "search icon")
                        },
                        onValueChange = {
                            textState.value = it
                            eventSender.invoke(MainEvent.QueryChanged(it.text))
                        }
                    )
                    VaccinationDataList(viewState.data)
                }
            }
        )
    }
}

@Composable
fun VaccinationDataList(list: List<VaccinationData>) {
    LazyColumn(
        content = {
            items(list) { vaccData ->
                VaccinationDataRow(vaccData)
            }
        }
    )
}

@Composable
fun VaccinationDataRow(vaccData: VaccinationData) {
    val updatedAgo = DateUtils.getRelativeTimeSpanString(
        SimpleDateFormat("yyyy-MM-dd").parse(vaccData.date).time, Date().time,
        DateUtils.SECOND_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
    val dataUpdatedStr = stringResource(id = R.string.data_updated_x, updatedAgo)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier
            .absolutePadding(right = 8.dp)
            .clip(RoundedCornerShape(10.dp))) {
            Box(modifier = Modifier
                .wrapContentWidth()
                .background(MaterialTheme.colors.secondary)) {
                Text(
                    text = "${vaccData.peopleFullyVaccinatedPerHundred}%",
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(
                            horizontal = 8.dp, vertical = 4.dp
                        )
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = vaccData.country, style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold)
                Text(text = dataUpdatedStr, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.caption, textAlign = TextAlign.End)
            }
            Text(text = stringResource(id = R.string.total_vaccinations_x, vaccData.totalVaccinations.formatToShortNumber()), style = MaterialTheme.typography.body1)
            vaccData.peopleVaccinated?.let {
                Text(text = stringResource(id = R.string.vaccinated_x, it.formatToShortNumber()), style = MaterialTheme.typography.body1)
            }
            vaccData.peopleFullyVaccinated?.let {
                Text(text = stringResource(id = R.string.fully_vaccinated_x, it.formatToShortNumber()), style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Composable
@Preview
fun VaccinationDataRowPrev(@PreviewParameter(VaccinationDataProvider::class) vaccData: VaccinationData) {
    val updatedAgo = DateUtils.getRelativeTimeSpanString(
        SimpleDateFormat("yyyy-MM-dd").parse(vaccData.date).time, Date().time,
        DateUtils.SECOND_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
    val dataUpdatedStr = stringResource(id = R.string.data_updated_x, updatedAgo)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier
            .absolutePadding(right = 8.dp)
            .clip(RoundedCornerShape(10.dp))) {
            Box(modifier = Modifier
                .wrapContentWidth()
                .background(MaterialTheme.colors.secondary)) {
                Text(
                    text = "${vaccData.peopleFullyVaccinatedPerHundred}%",
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(
                            horizontal = 8.dp, vertical = 4.dp
                        )
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = vaccData.country, style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold)
                Text(text = dataUpdatedStr, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.caption, textAlign = TextAlign.End)
            }
            Text(text = stringResource(id = R.string.total_vaccinations_x, vaccData.totalVaccinations.formatToShortNumber()), style = MaterialTheme.typography.body1)
            vaccData.peopleVaccinated?.let {
                Text(text = stringResource(id = R.string.vaccinated_x, it.formatToShortNumber()), style = MaterialTheme.typography.body1)
            }
            vaccData.peopleFullyVaccinated?.let {
                Text(text = stringResource(id = R.string.fully_vaccinated_x, it.formatToShortNumber()), style = MaterialTheme.typography.body1)
            }
        }
    }
}

class VaccinationDataProvider: PreviewParameterProvider<VaccinationData> {
    override val values = sequenceOf(
        VaccinationData(1,"France", "FR", "2021-03-05", 5432, 5.2),
        VaccinationData(2,"Sweden", "SWE", "2021-03-05", 4532, 6.3)
    )
    override val count: Int = values.count()
}

@Composable
fun Toolbar(title: String, subtitle: String, onRefresh: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(text = title)
                Text(text = subtitle, style = MaterialTheme.typography.subtitle2)
            }
        },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Rounded.Refresh, contentDescription = "refresh button")
            }
        },
        elevation = 12.dp
    )
}