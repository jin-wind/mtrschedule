package com.jinwind.mtrschedule.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Composable
fun <T> LiveData<T>.observeAsStateValue(initial: T?): androidx.compose.runtime.State<T?> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<T?>(value ?: initial) }
    val currentLiveData = rememberUpdatedState(this)
    DisposableEffect(lifecycleOwner, currentLiveData.value) {
        val observer = Observer<T> { state.value = it }
        currentLiveData.value.observe(lifecycleOwner, observer)
        onDispose { currentLiveData.value.removeObserver(observer) }
    }
    return state
}

@Composable
fun <T : Any> LiveData<T>.observeAsStateNotNull(initial: T): androidx.compose.runtime.State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(value ?: initial) }
    val currentLiveData = rememberUpdatedState(this)
    DisposableEffect(lifecycleOwner, currentLiveData.value) {
        val observer = Observer<T> { state.value = it ?: initial }
        currentLiveData.value.observe(lifecycleOwner, observer)
        onDispose { currentLiveData.value.removeObserver(observer) }
    }
    return state
}
