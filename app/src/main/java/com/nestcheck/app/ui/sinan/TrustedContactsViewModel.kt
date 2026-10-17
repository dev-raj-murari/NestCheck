package com.nestcheck.app.ui.sinan

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.CommunicationType
import com.nestcheck.app.data.model.ContactRelationship
import com.nestcheck.app.data.model.TrustedContact
import com.nestcheck.app.data.model.UnknownCommunicationAlert
import com.nestcheck.app.data.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TrustedContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    val contacts: StateFlow<List<TrustedContact>> = contactsRepository.contacts
    val unknownAlerts: StateFlow<List<UnknownCommunicationAlert>> = contactsRepository.unknownAlerts

    private val _isAddContactDialogOpen = MutableStateFlow(false)
    val isAddContactDialogOpen: StateFlow<Boolean> = _isAddContactDialogOpen.asStateFlow()

    fun openAddContactDialog() {
        _isAddContactDialogOpen.value = true
    }

    fun closeAddContactDialog() {
        _isAddContactDialogOpen.value = false
    }

    fun addContact(
        name: String,
        phone: String,
        email: String,
        relationship: ContactRelationship,
        callAllowed: Boolean,
        smsAllowed: Boolean
    ) {
        if (name.isNotBlank() && phone.isNotBlank()) {
            contactsRepository.addContact(name, phone, email, relationship, callAllowed, smsAllowed)
            _isAddContactDialogOpen.value = false
        }
    }

    fun toggleCallPermission(id: String) {
        contactsRepository.toggleCallPermission(id)
    }

    fun toggleSmsPermission(id: String) {
        contactsRepository.toggleSmsPermission(id)
    }

    fun deleteContact(id: String) {
        contactsRepository.deleteContact(id)
    }

    fun simulateUnknownCall() {
        contactsRepository.simulateUnknownCommunication(CommunicationType.CALL)
    }

    fun simulateUnknownSms() {
        contactsRepository.simulateUnknownCommunication(CommunicationType.SMS)
    }

    fun approveAndWhitelist(alertId: String, name: String, relationship: ContactRelationship) {
        contactsRepository.approveAndWhitelist(alertId, name, relationship)
    }

    fun dismissAlert(alertId: String) {
        contactsRepository.dismissAlert(alertId)
    }
}
