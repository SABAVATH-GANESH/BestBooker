package com.example.bestbooker

import kotlinx.coroutines.*

class Debouncer(private val delayMs: Long = 500) {
    private var job: Job? = null

    fun submit(scope: CoroutineScope, block: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch {
            delay(delayMs)
            block()
        }
    }
}