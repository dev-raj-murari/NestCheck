package com.nestcheck.app.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TamperMonitorReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        when (action) {
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val isAirplaneMode = intent.getBooleanExtra("state", false)
                if (isAirplaneMode) {
                    Log.w("NestCheckTamper", "TAMPER ALERT: Airplane mode enabled on monitored child device")
                    NestCheckNotificationManager(context).showTamperAlert("Airplane mode was enabled on monitored child device.")
                }
            }
            Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                Log.w("NestCheckTamper", "TAMPER ALERT: System clock manipulated on monitored child device")
                NestCheckNotificationManager(context).showTamperAlert("Device system clock or timezone was manipulated.")
            }
        }
    }
}
