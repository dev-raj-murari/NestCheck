package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.EmergencyCategory
import com.nestcheck.app.data.model.EmergencyDispatchRecord
import com.nestcheck.app.data.model.EmergencyHubConfig
import com.nestcheck.app.data.model.FastDialEmergencyContact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyHubRepository @Inject constructor() {

    private val _contacts = MutableStateFlow(
        listOf(
            FastDialEmergencyContact(
                id = "contact_mom",
                name = "Mom (Caregiver)",
                roleTitle = "Primary Guardian & Contact",
                phoneNumber = "+1 (555) 019-2831",
                category = EmergencyCategory.FAMILY,
                isPrimary = true,
                isTollFree = false
            ),
            FastDialEmergencyContact(
                id = "contact_dad",
                name = "Dad (Caregiver)",
                roleTitle = "Secondary Guardian",
                phoneNumber = "+1 (555) 019-2832",
                category = EmergencyCategory.FAMILY,
                isPrimary = false,
                isTollFree = false
            ),
            FastDialEmergencyContact(
                id = "contact_112",
                name = "National Emergency 112",
                roleTitle = "Police, Fire & All-in-One Response",
                phoneNumber = "112",
                category = EmergencyCategory.NATIONAL_EMERGENCY,
                isPrimary = false,
                isTollFree = true
            ),
            FastDialEmergencyContact(
                id = "contact_1098",
                name = "Childline 1098 Helpline",
                roleTitle = "24x7 Free Child Protection & SOS",
                phoneNumber = "1098",
                category = EmergencyCategory.SAFETY_PATROL,
                isPrimary = false,
                isTollFree = true
            ),
            FastDialEmergencyContact(
                id = "contact_108",
                name = "Ambulance & Paramedics",
                roleTitle = "Emergency Medical Trauma Dispatch",
                phoneNumber = "108",
                category = EmergencyCategory.MEDICAL,
                isPrimary = false,
                isTollFree = true
            ),
            FastDialEmergencyContact(
                id = "contact_1091",
                name = "Women & Child Distress",
                roleTitle = "Rapid Response Safety Squad",
                phoneNumber = "1091",
                category = EmergencyCategory.SAFETY_PATROL,
                isPrimary = false,
                isTollFree = true
            ),
            FastDialEmergencyContact(
                id = "contact_doc",
                name = "Dr. Anita Roy",
                roleTitle = "Family Pediatrician & Clinic",
                phoneNumber = "+1 (555) 019-9944",
                category = EmergencyCategory.MEDICAL,
                isPrimary = false,
                isTollFree = false
            )
        )
    )
    val contacts: StateFlow<List<FastDialEmergencyContact>> = _contacts.asStateFlow()

    private val _dispatchLogs = MutableStateFlow(
        listOf(
            EmergencyDispatchRecord(
                id = UUID.randomUUID().toString(),
                contactName = "Mom (Caregiver)",
                phoneNumber = "+1 (555) 019-2831",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 5,
                wasGpsBroadcast = true,
                status = "Emergency Voice Call Initiated & GPS SMS Sent"
            ),
            EmergencyDispatchRecord(
                id = UUID.randomUUID().toString(),
                contactName = "National Emergency 112",
                phoneNumber = "112",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 48,
                wasGpsBroadcast = true,
                status = "Routine Safety Test Completed"
            )
        )
    )
    val dispatchLogs: StateFlow<List<EmergencyDispatchRecord>> = _dispatchLogs.asStateFlow()

    private val _config = MutableStateFlow(EmergencyHubConfig())
    val config: StateFlow<EmergencyHubConfig> = _config.asStateFlow()

    fun dispatchCall(contact: FastDialEmergencyContact): EmergencyDispatchRecord {
        val record = EmergencyDispatchRecord(
            id = UUID.randomUUID().toString(),
            contactName = contact.name,
            phoneNumber = contact.phoneNumber,
            timestamp = System.currentTimeMillis(),
            wasGpsBroadcast = _config.value.autoSendLocationSmsOnCall,
            status = if (_config.value.autoSendLocationSmsOnCall)
                "Fast-Dial Dispatched + GPS SMS Broadcast to Caregivers"
            else
                "Fast-Dial Dispatched via Device Telephony"
        )
        _dispatchLogs.value = listOf(record) + _dispatchLogs.value
        _config.value = _config.value.copy(
            totalEmergencyDispatches = _config.value.totalEmergencyDispatches + 1
        )
        return record
    }

    fun broadcastSosToAll(): List<EmergencyDispatchRecord> {
        val primaryContacts = _contacts.value.filter { it.category == EmergencyCategory.FAMILY || it.phoneNumber == "112" }
        val newRecords = primaryContacts.map { c ->
            EmergencyDispatchRecord(
                id = UUID.randomUUID().toString(),
                contactName = c.name,
                phoneNumber = c.phoneNumber,
                timestamp = System.currentTimeMillis(),
                wasGpsBroadcast = true,
                status = "CRITICAL SOS BROADCAST: Live Coordinates & Emergency Ping Dispatched"
            )
        }
        _dispatchLogs.value = newRecords + _dispatchLogs.value
        _config.value = _config.value.copy(
            totalEmergencyDispatches = _config.value.totalEmergencyDispatches + newRecords.size
        )
        return newRecords
    }

    fun toggleCountdown(enabled: Boolean) {
        _config.value = _config.value.copy(isCountdownEnabled = enabled)
    }

    fun toggleAutoLocationSms(enabled: Boolean) {
        _config.value = _config.value.copy(autoSendLocationSmsOnCall = enabled)
    }
}
