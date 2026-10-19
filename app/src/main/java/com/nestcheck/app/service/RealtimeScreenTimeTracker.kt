package com.nestcheck.app.service

import com.nestcheck.app.data.repository.CreditScreenTimeRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeScreenTimeTracker @Inject constructor(
    private val creditRepository: CreditScreenTimeRepository
) {
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var trackingJob: Job? = null

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _isCurfewLocked = MutableStateFlow(false)
    val isCurfewLocked: StateFlow<Boolean> = _isCurfewLocked.asStateFlow()

    init {
        startRealtimeTracking()
    }

    fun startRealtimeTracking() {
        if (trackingJob?.isActive == true) return
        _isTracking.value = true
        creditRepository.toggleTracking(true)

        trackingJob = serviceScope.launch {
            var secondCounter = 0L
            while (isActive) {
                delay(1000L) // 1 second real-time tick
                secondCounter++

                // Deduct 1 second from active remaining screen time
                creditRepository.deductElapsedSeconds(1L)

                val wallet = creditRepository.wallet.value
                if (wallet.activeRemainingSeconds <= 0L) {
                    _isCurfewLocked.value = true
                } else {
                    _isCurfewLocked.value = false
                }
            }
        }
    }

    fun pauseRealtimeTracking() {
        trackingJob?.cancel()
        _isTracking.value = false
        creditRepository.toggleTracking(false)
    }

    fun resumeRealtimeTracking() {
        startRealtimeTracking()
    }
}
