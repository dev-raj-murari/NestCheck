package com.nestcheck.app.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncItemStatus {
    PENDING, SYNCING, SYNCED, FAILED
}

data class SyncQueueItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val eventType: String,
    val payloadPreview: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: SyncItemStatus = SyncItemStatus.PENDING,
    val retryCount: Int = 0
)

data class SyncEngineState(
    val isOnline: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncTimestamp: Long = System.currentTimeMillis() - 300000,
    val autoSyncEnabled: Boolean = true,
    val totalSyncedCount: Int = 142
)

@Singleton
class OfflineSyncRepository @Inject constructor() {

    private val _engineState = MutableStateFlow(SyncEngineState())
    val engineState: StateFlow<SyncEngineState> = _engineState.asStateFlow()

    private val _queueItems = MutableStateFlow(
        listOf(
            SyncQueueItem(eventType = "STEP_UPDATE", payloadPreview = "steps: 5420, calories: 238", status = SyncItemStatus.PENDING),
            SyncQueueItem(eventType = "MOOD_LOG", payloadPreview = "mood: GREAT, note: 'Science project'", status = SyncItemStatus.PENDING),
            SyncQueueItem(eventType = "LOCATION_PING", payloadPreview = "lat: 12.9716, lng: 77.5946", status = SyncItemStatus.PENDING)
        )
    )
    val queueItems: StateFlow<List<SyncQueueItem>> = _queueItems.asStateFlow()

    fun enqueueItem(eventType: String, payload: String) {
        val item = SyncQueueItem(eventType = eventType, payloadPreview = payload)
        _queueItems.value = listOf(item) + _queueItems.value
    }

    fun toggleNetworkConnectivity(online: Boolean) {
        _engineState.value = _engineState.value.copy(isOnline = online)
    }

    fun toggleAutoSync(enabled: Boolean) {
        _engineState.value = _engineState.value.copy(autoSyncEnabled = enabled)
    }

    suspend fun flushQueueNow(): Int {
        if (!_engineState.value.isOnline) return 0
        _engineState.value = _engineState.value.copy(isSyncing = true)
        delay(1200) // simulate transmission

        val pendingCount = _queueItems.value.count { it.status == SyncItemStatus.PENDING }
        _queueItems.value = _queueItems.value.map { it.copy(status = SyncItemStatus.SYNCED) }
        _engineState.value = _engineState.value.copy(
            isSyncing = false,
            lastSyncTimestamp = System.currentTimeMillis(),
            totalSyncedCount = _engineState.value.totalSyncedCount + pendingCount
        )
        return pendingCount
    }

    fun clearSynced() {
        _queueItems.value = _queueItems.value.filter { it.status != SyncItemStatus.SYNCED }
    }
}
