package com.nestcheck.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.repository.AuthRepository
import com.nestcheck.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class UserType {
    PARENT,
    KID
}

data class KidDraft(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val age: String = "",
    val gender: String = "Boy",
    val dob: String = "",
    val height: String = "",
    val weight: String = ""
)

data class AuthUiState(
    val userType: UserType = UserType.PARENT,
    val isRegistering: Boolean = false,

    // Parent details
    val parentEmail: String = "devraj@nestcheck.app",
    val parentPassword: String = "password123",
    val parentName: String = "Dev Raj",
    val parentRoleInHouse: String = "Father",
    val parentPhone: String = "+91 98765 43210",

    // Child entry fields (entered during parent registration)
    val kidName: String = "",
    val kidAge: String = "",
    val kidGender: String = "Boy",
    val kidDob: String = "",
    val kidHeight: String = "",
    val kidWeight: String = "",

    // List of added children
    val addedKids: List<KidDraft> = listOf(
        KidDraft(
            name = "Aarav",
            age = "10",
            gender = "Boy",
            dob = "2016-05-15",
            height = "140",
            weight = "34"
        )
    ),

    // Kid direct login
    val kidLoginName: String = "Aarav",
    val kidLoginPass: String = "1234",

    // State (No dashboard after login)
    val isLoggedIn: Boolean = false,
    val loggedInUserType: UserType? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setUserType(type: UserType) {
        _uiState.value = _uiState.value.copy(userType = type, error = null)
    }

    fun setRegistering(isRegistering: Boolean) {
        _uiState.value = _uiState.value.copy(isRegistering = isRegistering, error = null)
    }

    // Parent fields
    fun onParentEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(parentEmail = value, error = null)
    }

    fun onParentPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(parentPassword = value, error = null)
    }

    fun onParentNameChange(value: String) {
        _uiState.value = _uiState.value.copy(parentName = value, error = null)
    }

    fun onParentRoleChange(value: String) {
        _uiState.value = _uiState.value.copy(parentRoleInHouse = value, error = null)
    }

    // Kid fields
    fun onKidNameChange(value: String) {
        _uiState.value = _uiState.value.copy(kidName = value, error = null)
    }

    fun onKidAgeChange(value: String) {
        _uiState.value = _uiState.value.copy(kidAge = value, error = null)
    }

    fun onKidGenderChange(value: String) {
        _uiState.value = _uiState.value.copy(kidGender = value, error = null)
    }

    fun onKidDobChange(value: String) {
        _uiState.value = _uiState.value.copy(kidDob = value, error = null)
    }

    fun onKidHeightChange(value: String) {
        _uiState.value = _uiState.value.copy(kidHeight = value, error = null)
    }

    fun onKidWeightChange(value: String) {
        _uiState.value = _uiState.value.copy(kidWeight = value, error = null)
    }

    fun addKidToList() {
        val s = _uiState.value
        if (s.kidName.isBlank()) return
        val newKid = KidDraft(
            name = s.kidName,
            age = s.kidAge,
            gender = s.kidGender,
            dob = s.kidDob,
            height = s.kidHeight,
            weight = s.kidWeight
        )
        _uiState.value = s.copy(
            addedKids = s.addedKids + newKid,
            kidName = "",
            kidAge = "",
            kidDob = "",
            kidHeight = "",
            kidWeight = ""
        )
    }

    fun removeKid(id: String) {
        _uiState.value = _uiState.value.copy(
            addedKids = _uiState.value.addedKids.filter { it.id != id }
        )
    }

    // Kid Login fields
    fun onKidLoginNameChange(value: String) {
        _uiState.value = _uiState.value.copy(kidLoginName = value, error = null)
    }

    fun onKidLoginPassChange(value: String) {
        _uiState.value = _uiState.value.copy(kidLoginPass = value, error = null)
    }

    // Auth actions
    fun loginParent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.login(_uiState.value.parentEmail, _uiState.value.parentPassword)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isLoggedIn = true,
                loggedInUserType = UserType.PARENT
            )
        }
    }

    fun registerParent() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Convert added kids to ChildProfile models
            val kidsToSave = s.addedKids.mapIndexed { idx, k ->
                ChildProfile(
                    uid = "child_${idx + 1}",
                    parentUid = "parent_1",
                    name = k.name,
                    age = k.age.toIntOrNull() ?: 10,
                    gender = k.gender,
                    dateOfBirth = k.dob,
                    height = k.height.toFloatOrNull() ?: 140f,
                    weight = k.weight.toFloatOrNull() ?: 34f
                )
            }

            profileRepository.updateParentProfile(
                name = s.parentName.ifBlank { "Parent" },
                email = s.parentEmail,
                roleInHouse = s.parentRoleInHouse,
                phone = s.parentPhone
            )
            profileRepository.setChildren(kidsToSave)
            authRepository.register(s.parentEmail, s.parentPassword, s.parentName)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isLoggedIn = true,
                loggedInUserType = UserType.PARENT
            )
        }
    }

    fun loginKid() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isLoggedIn = true,
                loggedInUserType = UserType.KID
            )
        }
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
            loggedInUserType = null
        )
    }
}
