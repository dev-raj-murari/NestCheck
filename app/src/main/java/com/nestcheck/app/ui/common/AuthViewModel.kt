package com.nestcheck.app.ui.common

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.nestcheck.app.core.PinHasher
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

/**
 * Registration only collects what is needed to create the account.
 * Height / weight live in Child Profile (parent dashboard) and are optional there.
 */
data class KidDraft(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val age: String = "",
    val gender: String = "Boy",
    val dob: String = "",
    val pin: String = ""
)

data class AuthUiState(
    val userType: UserType = UserType.PARENT,
    val isRegistering: Boolean = false,

    // Parent details (no prefilled credentials / personal data)
    val parentEmail: String = "",
    val parentPassword: String = "",
    val parentName: String = "",
    val parentRoleInHouse: String = "Father",
    val parentPhone: String = "",

    // Child entry fields (entered during parent registration)
    val kidName: String = "",
    val kidAge: String = "",
    val kidGender: String = "Boy",
    val kidDob: String = "",
    val kidPin: String = "",

    // List of added children
    val addedKids: List<KidDraft> = emptyList(),

    // Kid direct login
    val kidLoginName: String = "",
    val kidLoginPass: String = "",

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

    private var kidFailedAttempts = 0
    private var kidLockedUntil = 0L

    private fun update(block: AuthUiState.() -> AuthUiState) {
        _uiState.value = _uiState.value.block()
    }

    fun setUserType(type: UserType) = update { copy(userType = type, error = null) }
    fun setRegistering(isRegistering: Boolean) = update { copy(isRegistering = isRegistering, error = null) }

    // Parent fields
    fun onParentEmailChange(value: String) = update { copy(parentEmail = value, error = null) }
    fun onParentPasswordChange(value: String) = update { copy(parentPassword = value, error = null) }
    fun onParentNameChange(value: String) = update { copy(parentName = value, error = null) }
    fun onParentRoleChange(value: String) = update { copy(parentRoleInHouse = value, error = null) }

    // Kid fields (parent registration flow)
    fun onKidNameChange(value: String) = update { copy(kidName = value, error = null) }
    fun onKidAgeChange(value: String) = update { copy(kidAge = value, error = null) }
    fun onKidGenderChange(value: String) = update { copy(kidGender = value, error = null) }
    fun onKidDobChange(value: String) = update { copy(kidDob = value, error = null) }
    fun onKidPinChange(value: String) = update { copy(kidPin = value.filter { it.isDigit() }.take(4), error = null) }

    fun addKidToList() {
        val s = _uiState.value
        val age = s.kidAge.toIntOrNull()
        when {
            s.kidName.isBlank() -> return fail("Enter your child's name.")
            age == null || age !in 3..17 -> return fail("Enter a valid age (3-17).")
            s.kidPin.length != 4 -> return fail("Set a 4-digit PIN for your child.")
            s.addedKids.any { it.name.equals(s.kidName.trim(), ignoreCase = true) } ->
                return fail("You already added a child with that name.")
        }
        update {
            copy(
                addedKids = addedKids + KidDraft(
                    name = kidName.trim(), age = kidAge, gender = kidGender, dob = kidDob, pin = kidPin
                ),
                kidName = "", kidAge = "", kidDob = "", kidPin = "", error = null
            )
        }
    }

    fun removeKid(id: String) = update { copy(addedKids = addedKids.filter { it.id != id }) }

    // Kid login fields
    fun onKidLoginNameChange(value: String) = update { copy(kidLoginName = value, error = null) }
    fun onKidLoginPassChange(value: String) = update { copy(kidLoginPass = value, error = null) }

    private fun fail(message: String) = update { copy(isLoading = false, error = message) }

    private fun friendlyAuthError(e: Throwable): String = when (e) {
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException -> "Wrong email or password."
        is FirebaseAuthUserCollisionException -> "That email is already registered. Try logging in."
        else -> "Couldn't reach the server. Check your connection and try again."
    }

    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    // ---- Auth actions -------------------------------------------------------------------

    fun loginParent() {
        val s = _uiState.value
        if (!isValidEmail(s.parentEmail)) return fail("Enter a valid email.")
        if (s.parentPassword.length < 8) return fail("Password must be at least 8 characters.")

        viewModelScope.launch {
            update { copy(isLoading = true, error = null) }
            authRepository.login(s.parentEmail, s.parentPassword).fold(
                onSuccess = { update { copy(isLoading = false, isLoggedIn = true, loggedInUserType = UserType.PARENT) } },
                onFailure = { e -> fail(friendlyAuthError(e)) }
            )
        }
    }

    fun registerParent() {
        val s = _uiState.value
        when {
            s.parentName.isBlank() -> return fail("Enter your name.")
            !isValidEmail(s.parentEmail) -> return fail("Enter a valid email.")
            s.parentPassword.length < 8 -> return fail("Password must be at least 8 characters.")
            s.addedKids.isEmpty() -> return fail("Add at least one child (you can edit details later).")
        }

        viewModelScope.launch {
            update { copy(isLoading = true, error = null) }

            // Account first - only touch local profile data once the account really exists.
            val result = authRepository.register(s.parentEmail, s.parentPassword, s.parentName)
            if (result.isFailure) return@launch fail(friendlyAuthError(result.exceptionOrNull()!!))

            val kidsToSave = s.addedKids.mapIndexed { idx, k ->
                val uid = "child_${idx + 1}"
                ChildProfile(
                    uid = uid,
                    parentUid = "parent_1",
                    name = k.name,
                    age = k.age.toIntOrNull() ?: 10,
                    gender = k.gender,
                    dateOfBirth = k.dob,
                    // Not collected at signup. Parent adds these later in Child Profile.
                    height = 0f,
                    weight = 0f,
                    bmi = 0f,
                    pinHash = PinHasher.hash(uid, k.pin)
                )
            }
            profileRepository.updateParentProfile(
                name = s.parentName.trim(),
                email = s.parentEmail.trim(),
                roleInHouse = s.parentRoleInHouse,
                phone = s.parentPhone
            )
            profileRepository.setChildren(kidsToSave)

            update { copy(isLoading = false, isLoggedIn = true, loggedInUserType = UserType.PARENT, parentPassword = "") }
        }
    }

    /**
     * Kids can only log in as a child a parent already created, with that child's PIN.
     * Same error for "no such kid" and "wrong PIN" so names can't be enumerated.
     */
    fun loginKid() {
        val s = _uiState.value
        val now = System.currentTimeMillis()
        if (now < kidLockedUntil) {
            return fail("Too many tries. Wait ${(kidLockedUntil - now) / 1000 + 1}s.")
        }

        val child = profileRepository.parentProfile.value.children
            .firstOrNull { it.name.equals(s.kidLoginName.trim(), ignoreCase = true) }

        if (child == null || !PinHasher.matches(child.uid, s.kidLoginPass, child.pinHash)) {
            kidFailedAttempts++
            if (kidFailedAttempts >= 5) {
                kidLockedUntil = now + 30_000L
                kidFailedAttempts = 0
            }
            return fail("Wrong name or PIN. Ask a parent to add you or reset your PIN.")
        }

        kidFailedAttempts = 0
        update {
            copy(isLoading = false, isLoggedIn = true, loggedInUserType = UserType.KID, kidLoginPass = "", error = null)
        }
    }

    fun logout() = update { copy(isLoggedIn = false, loggedInUserType = null, parentPassword = "", kidLoginPass = "") }
}
