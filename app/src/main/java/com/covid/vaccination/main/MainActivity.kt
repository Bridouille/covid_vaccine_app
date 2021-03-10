package com.covid.vaccination.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.covid.vaccination.main.ui.MainContent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var mainVMFact: MainVMFactory
    val mainVM: MainVM by viewModels { mainVMFact }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { MainScreen(mainVM) }
    }
}

@Composable
fun MainScreen(mainVM: MainVM) {
    val vs by mainVM.models.observeAsState()

    // This is black magic to me
    val ve = remember { mutableStateOf<ViewEffect?>(null) }
    mainVM.viewEffects.setObserver( LocalLifecycleOwner.current, {
        ve.value = it
    })

    vs?.let {
        MainContent(it, ve.value) {
            mainVM.dispatchEvent(it)
        }

        // re-sets the view effects after a recomposition
        // black magic again
        SideEffect { ve.value = null }
    }
}