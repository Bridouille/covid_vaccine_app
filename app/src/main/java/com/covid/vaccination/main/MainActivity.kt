package com.covid.vaccination.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.Snapshot.Companion.observe
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import com.covid.vaccination.main.ui.MainContent
import com.spotify.mobius.android.LiveQueue
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

fun Long.formatToShortNumber(): String {
    return when {
        this >= 1000000000 -> String.format("%.2fB", this / 1000000000.0)
        this >= 1000000 -> String.format("%.2fM", this / 1000000.0)
        this >= 1000 -> String.format("%.2fK", this / 1000.0)
        else -> this.toString()
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

@Composable
fun <R, T : R> LiveQueue<T>.observeAsState(initial: R?): State<R?> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(initial) }
    DisposableEffect(this, lifecycleOwner) {
        val observer = Observer<T> { state.value = it }
        setObserver(lifecycleOwner, observer)
        onDispose { clearObserver() }
    }
    return state
}