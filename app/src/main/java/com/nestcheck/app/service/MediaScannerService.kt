package com.nestcheck.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.nestcheck.app.data.repository.SafetyRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaScannerService : Service() {

    @Inject
    lateinit var safetyRepository: SafetyRepository

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MediaScannerService", "On-device Content Safety scanner active.")
        serviceScope.launch {
            // Simulated scan of background media buffers
            safetyRepository.simulateMediaScan("System Media Service", "cache_stream_buffer.png", 0.62f)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
