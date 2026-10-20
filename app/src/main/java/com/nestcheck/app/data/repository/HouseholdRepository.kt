package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.HouseholdChild
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class HouseholdRepository @Inject constructor() {

    private val _children = MutableStateFlow<List<HouseholdChild>>(
        listOf(
            HouseholdChild(
                id = "child_1",
                name = "Leo",
                age = 10,
                grade = "5th Grade",
                avatarColorHex = 0xFF00E5FF,
                deviceName = "Samsung Galaxy Tab A9",
                isOnline = true,
                batteryPct = 78,
                screenTimeMinutes = 95,
                screenTimeLimitMinutes = 150,
                locationSummary = "Home (Safe Zone)",
                stepsToday = 6420,
                pendingAlertsCount = 0,
                pairingCode = "NC-8492",
                isDeviceLocked = false
            ),
            HouseholdChild(
                id = "child_2",
                name = "Maya",
                age = 14,
                grade = "9th Grade",
                avatarColorHex = 0xFFFF4081,
                deviceName = "Google Pixel 7a",
                isOnline = true,
                batteryPct = 42,
                screenTimeMinutes = 185,
                screenTimeLimitMinutes = 210,
                locationSummary = "Oakridge School (Geofence)",
                stepsToday = 8930,
                pendingAlertsCount = 2,
                pairingCode = "NC-3918",
                isDeviceLocked = false
            ),
            HouseholdChild(
                id = "child_3",
                name = "Sam",
                age = 7,
                grade = "2nd Grade",
                avatarColorHex = 0xFF00E676,
                deviceName = "Lenovo Tab M10",
                isOnline = false,
                batteryPct = 95,
                screenTimeMinutes = 40,
                screenTimeLimitMinutes = 60,
                locationSummary = "Home (Last ping 1h ago)",
                stepsToday = 3120,
                pendingAlertsCount = 0,
                pairingCode = "NC-7201",
                isDeviceLocked = false
            )
        )
    )
    val children: StateFlow<List<HouseholdChild>> = _children.asStateFlow()

    private val _selectedChildId = MutableStateFlow<String>("child_1")
    val selectedChildId: StateFlow<String> = _selectedChildId.asStateFlow()

    fun selectChild(childId: String) {
        _selectedChildId.value = childId
    }

    fun getSelectedChild(): HouseholdChild? {
        return _children.value.find { it.id == _selectedChildId.value } ?: _children.value.firstOrNull()
    }

    fun addChild(name: String, age: Int, grade: String, deviceName: String): HouseholdChild {
        val newId = "child_${System.currentTimeMillis()}"
        val pairingCode = "NC-${Random.nextInt(1000, 9999)}"
        val colors = listOf(0xFF00E5FF, 0xFFFF4081, 0xFF00E676, 0xFFFFB300, 0xFFB388FF)
        val randomColor = colors[Random.nextInt(colors.size)]

        val newChild = HouseholdChild(
            id = newId,
            name = name.trim(),
            age = age,
            grade = grade.trim().ifBlank { "Primary" },
            avatarColorHex = randomColor,
            deviceName = deviceName.trim().ifBlank { "Child Device" },
            isOnline = true,
            batteryPct = 100,
            screenTimeMinutes = 0,
            screenTimeLimitMinutes = if (age <= 8) 60 else if (age <= 12) 120 else 180,
            locationSummary = "Pairing Pending",
            stepsToday = 0,
            pendingAlertsCount = 0,
            pairingCode = pairingCode,
            isDeviceLocked = false
        )

        _children.value = _children.value + newChild
        _selectedChildId.value = newId
        return newChild
    }

    fun removeChild(childId: String) {
        _children.value = _children.value.filter { it.id != childId }
        if (_selectedChildId.value == childId) {
            _selectedChildId.value = _children.value.firstOrNull()?.id ?: ""
        }
    }
}
