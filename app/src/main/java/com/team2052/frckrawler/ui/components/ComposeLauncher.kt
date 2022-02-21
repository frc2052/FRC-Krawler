package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.*

//TODO: Implement a way of asynchronously awaiting a return value from a ComposableLauncher launch

/**
 * A simple wrapper function for calling a composable function from outside an @Composable context.
 */
@Composable
fun <T>ComposableLauncher(
    modifier: Modifier = Modifier,
    content: @Composable ComposeLauncherScope<T>.() -> Unit,
): ComposeLauncher<T> {
    var launched by rememberSaveable { mutableStateOf(false) }
    val composeLauncher = remember {
        mutableStateOf(ComposeLauncher<T> { state -> launched = state })
    }

    val composeLauncherScope = remember { ComposeLauncherScope<T>(composeLauncher.value) }
    if (launched) {
        Box(modifier = modifier, content = { content(composeLauncherScope) })
    }

    return composeLauncher.value
}

open class ComposeLauncher<T>(private val onLaunchedStateChanged: (Boolean) -> Unit) {
    private var returnValue: T? = null
    private var complete = false

    suspend fun launch(onCompletion: (T?) -> Unit = { }) = coroutineScope {
        onLaunchedStateChanged(true)
        while (!complete) {
            delay(500)
        }
        onCompletion(returnValue)
    }

    fun complete(returnValue: Any?) {
        onLaunchedStateChanged(false)
        this.returnValue = returnValue as T
        complete = true
    }
}

open class ComposeLauncherScope<T>(private val composeLauncher: ComposeLauncher<*>) {
    fun complete(returnValue: T) {
        composeLauncher.complete(returnValue)
    }
}

//@Composable
//fun SideEventImpl() {
//
//    val bluetoothEnableLauncher = ComposeLauncher {
//        true
//    }
//
//    bluetoothEnableLauncher.launch()
//
//}
//
//@Composable
//fun <T>ComposeLauncher(
//    modifier: Modifier = Modifier,
//    content: @Composable ComposeLauncherScope.() -> T,
//): ComposeLauncher<T> {
//    var launched by rememberSaveable { mutableStateOf(false) }
//    val composeLauncher = rememberSaveable {
//        mutableStateOf(
//            ComposeLauncher<T>(differential = 0) { state -> launched = state }
//        )
//    }
//
//    val composeLauncherScope = rememberSaveable { ComposeLauncherScope() }
//    if (launched) {
//        Box(modifier = modifier, content = { content(composeLauncherScope) })
//    }
//
//    return composeLauncher.value
//}
//
//open class ComposeLauncher<T>(
//    private val differential: Int,
//    private val onLaunchedStateChanged: (Boolean, () -> T) -> Unit,
//) {
//    fun launch(): T {
//        onLaunchedStateChanged(true)
//    }
//}
//
//open class ComposeLauncherScope() {
//
//}
