package com.nestcheck.app.data.model

data class DeviceStatus(
    val childUid: String = "",
    val batteryLevel: Int = 0,
    val isCharging: Boolean = false,
    val isWifiConnected: Boolean = false,
    val isMobileDataOn: Boolean = false,
    val lastActiveTimestamp: Long = 0L,
    val isDeviceLocked: Boolean = false
)
