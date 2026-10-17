package com.nestcheck.app.ui.parent.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.CoParentMember
import com.nestcheck.app.data.model.CoParentRole
import com.nestcheck.app.data.model.HouseholdInviteCode
import com.nestcheck.app.data.repository.FamilySharingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FamilySharingUiState(
    val members: List<CoParentMember> = emptyList(),
    val activeInvite: HouseholdInviteCode? = null,
    val showInviteDialog: Boolean = false,
    val inviteeName: String = "",
    val inviteeEmail: String = "",
    val selectedRole: CoParentRole = CoParentRole.GUARDIAN,
    val toastMessage: String? = null
)

@HiltViewModel
class FamilySharingViewModel @Inject constructor(
    private val repository: FamilySharingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FamilySharingUiState())
    val uiState: StateFlow<FamilySharingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.members.collect { list ->
                _uiState.value = _uiState.value.copy(members = list)
            }
        }
        viewModelScope.launch {
            repository.activeInvite.collect { invite ->
                _uiState.value = _uiState.value.copy(activeInvite = invite)
            }
        }
    }

    fun openInviteDialog() {
        _uiState.value = _uiState.value.copy(
            showInviteDialog = true,
            inviteeName = "",
            inviteeEmail = "",
            selectedRole = CoParentRole.GUARDIAN
        )
    }

    fun closeInviteDialog() {
        _uiState.value = _uiState.value.copy(showInviteDialog = false)
    }

    fun setInviteeName(name: String) { _uiState.value = _uiState.value.copy(inviteeName = name) }
    fun setInviteeEmail(email: String) { _uiState.value = _uiState.value.copy(inviteeEmail = email) }
    fun setSelectedRole(role: CoParentRole) { _uiState.value = _uiState.value.copy(selectedRole = role) }

    fun sendEmailInvite() {
        val s = _uiState.value
        if (s.inviteeEmail.isBlank()) return
        repository.inviteByEmail(
            name = s.inviteeName.ifBlank { "Invited Parent" },
            email = s.inviteeEmail,
            role = s.selectedRole
        )
        _uiState.value = _uiState.value.copy(
            showInviteDialog = false,
            toastMessage = "Invite sent to ${s.inviteeEmail} with role ${s.selectedRole.displayName}."
        )
    }

    fun generateNewCode() {
        val invite = repository.generateNewInviteCode(_uiState.value.selectedRole)
        _uiState.value = _uiState.value.copy(
            toastMessage = "New Household Code Generated: ${invite.code}"
        )
    }

    fun removeMember(memberId: String) {
        repository.removeMember(memberId)
        _uiState.value = _uiState.value.copy(toastMessage = "Member access revoked.")
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
