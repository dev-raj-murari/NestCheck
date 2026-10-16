package com.nestcheck.app.ui.parent.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.repository.OfflineSyncRepository
import com.nestcheck.app.data.repository.SyncEngineState
import com.nestcheck.app.data.repository.SyncQueueItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfflineSyncViewModel @Inject constructor(
    private val syncRepository: OfflineSyncRepository
) : ViewModel() {

    val engineState: StateFlow<SyncEngineState> = syncRepository.engineState
    val queueItems: StateFlow<List<SyncQueueItem>> = syncRepository.queueItems

    fun toggleNetwork(online: Boolean) {
        syncRepository.toggleNetworkConnectivity(online)
    }

    fun toggleAutoSync(enabled: Boolean) {
        syncRepository.toggleAutoSync(enabled)
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            syncRepository.flushQueueNow()
        }
    }

    fun addMockEvent() {
        syncRepository.enqueueItem("BATTERY_STATUS", "level: 78%, charging: false")
    }

    fun clearSynced() {
        syncRepository.clearSynced()
    }
}
