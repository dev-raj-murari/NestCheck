package com.nestcheck.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nestcheck.app.data.repository.SafetyRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppInstallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var safetyRepository: SafetyRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            val packageName = intent.data?.schemeSpecificPart ?: return
            Log.d("AppInstallReceiver", "New app package detected: $packageName. Placing into quarantine.")
            
            // Auto quarantine incoming application
            safetyRepository.simulateNewInstall(
                appName = packageName.substringAfterLast(".").replaceFirstChar { it.uppercase() },
                packageName = packageName,
                permissions = listOf("android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"),
                contentRating = "Pending Audit"
            )
        }
    }
}
