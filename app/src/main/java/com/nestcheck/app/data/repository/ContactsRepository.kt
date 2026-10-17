package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.CommunicationType
import com.nestcheck.app.data.model.ContactRelationship
import com.nestcheck.app.data.model.TrustedContact
import com.nestcheck.app.data.model.UnknownCommunicationAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepository @Inject constructor() {

    // ========================================================================
    // F31: TRUSTED CONTACTS WHITELIST DIRECTORY
    // ========================================================================

    private val _contacts = MutableStateFlow<List<TrustedContact>>(
        listOf(
            TrustedContact(
                id = "tc_1",
                name = "Sarah Davis (Mom)",
                phoneNumber = "+1 (555) 234-5678",
                email = "sarah.davis@nestcheck.safe",
                relationship = ContactRelationship.MOM,
                isCallAllowed = true,
                isSmsAllowed = true
            ),
            TrustedContact(
                id = "tc_2",
                name = "Michael Davis (Dad)",
                phoneNumber = "+1 (555) 234-5679",
                email = "michael.davis@nestcheck.safe",
                relationship = ContactRelationship.DAD,
                isCallAllowed = true,
                isSmsAllowed = true
            ),
            TrustedContact(
                id = "tc_3",
                name = "Mr. Henderson (Math Tutor)",
                phoneNumber = "+1 (555) 876-5432",
                email = "henderson.tutor@academy.edu",
                relationship = ContactRelationship.TUTOR,
                isCallAllowed = true,
                isSmsAllowed = false
            ),
            TrustedContact(
                id = "tc_4",
                name = "Lucas Vance (Classmate)",
                phoneNumber = "+1 (555) 345-6789",
                email = "lucas.vance@school.edu",
                relationship = ContactRelationship.FRIEND,
                isCallAllowed = true,
                isSmsAllowed = true
            )
        )
    )
    val contacts: StateFlow<List<TrustedContact>> = _contacts.asStateFlow()

    private val _unknownAlerts = MutableStateFlow<List<UnknownCommunicationAlert>>(
        listOf(
            UnknownCommunicationAlert(
                id = "unk_1",
                senderAddress = "+1 (800) 492-0199",
                communicationType = CommunicationType.CALL,
                snippet = "Incoming voice call blocked: Unverified corporate VoIP caller ID",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 40,
                isBlocked = true,
                isReviewed = false
            ),
            UnknownCommunicationAlert(
                id = "unk_2",
                senderAddress = "+1 (917) 555-8392",
                communicationType = CommunicationType.SMS,
                snippet = "Blocked SMS text: 'Hey, are you free this afternoon? Click this link...'",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
                isBlocked = true,
                isReviewed = false
            )
        )
    )
    val unknownAlerts: StateFlow<List<UnknownCommunicationAlert>> = _unknownAlerts.asStateFlow()

    fun addContact(
        name: String,
        phone: String,
        email: String,
        relationship: ContactRelationship,
        callAllowed: Boolean,
        smsAllowed: Boolean
    ) {
        val newContact = TrustedContact(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = phone,
            email = email,
            relationship = relationship,
            isCallAllowed = callAllowed,
            isSmsAllowed = smsAllowed
        )
        _contacts.value = _contacts.value + newContact
    }

    fun toggleCallPermission(id: String) {
        _contacts.value = _contacts.value.map {
            if (it.id == id) it.copy(isCallAllowed = !it.isCallAllowed) else it
        }
    }

    fun toggleSmsPermission(id: String) {
        _contacts.value = _contacts.value.map {
            if (it.id == id) it.copy(isSmsAllowed = !it.isSmsAllowed) else it
        }
    }

    fun deleteContact(id: String) {
        _contacts.value = _contacts.value.filterNot { it.id == id }
    }

    fun simulateUnknownCommunication(type: CommunicationType) {
        val randomNum = (1000..9999).random()
        val sender = "+1 (555) 902-$randomNum"
        val snippet = if (type == CommunicationType.CALL) {
            "Voice call from unverified caller outside trusted whitelist directory"
        } else {
            "Incoming SMS from unknown number: 'Hey Alex, check this invite out!'"
        }
        val alert = UnknownCommunicationAlert(
            id = UUID.randomUUID().toString(),
            senderAddress = sender,
            communicationType = type,
            snippet = snippet,
            timestamp = System.currentTimeMillis(),
            isBlocked = true,
            isReviewed = false
        )
        _unknownAlerts.value = listOf(alert) + _unknownAlerts.value
    }

    fun approveAndWhitelist(alertId: String, name: String, relationship: ContactRelationship) {
        val alert = _unknownAlerts.value.find { it.id == alertId } ?: return
        addContact(
            name = name.ifBlank { "Approved Contact (${alert.senderAddress})" },
            phone = alert.senderAddress,
            email = "",
            relationship = relationship,
            callAllowed = true,
            smsAllowed = true
        )
        _unknownAlerts.value = _unknownAlerts.value.map {
            if (it.id == alertId) it.copy(isReviewed = true, isBlocked = false) else it
        }
    }

    fun dismissAlert(alertId: String) {
        _unknownAlerts.value = _unknownAlerts.value.filterNot { it.id == alertId }
    }
}
