package com.covid.vaccination.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var mainVMFact: MainVMFactory
    val mainVM: MainVM by viewModels { mainVMFact }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(mainVM)
        }
    }
}

@Composable
fun MainScreen(mainVM: MainVM) {
    val vs by mainVM.models.observeAsState()

    vs?.let {
        MainContent(it) {
            mainVM.dispatchEvent(it)
        }
    }
}

@Composable
fun MainContent(
    viewState: ViewState,
    eventSender: (Event) -> Unit
) {
    MaterialTheme {
        val typography = MaterialTheme.typography
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //.clip(shape = RoundedCornerShape(4.dp)),

            Text("A day in Shark Fin ${viewState.isRefreshingData} -> ${viewState.data.size}",
                style = typography.h6)
            Text("Davenport, California",
                style = typography.body2)
            Text("December 2018",
                style = typography.body2)
        }
    }
}