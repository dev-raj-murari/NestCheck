package com.nestcheck.app.data.repository

import android.content.SharedPreferences
import com.nestcheck.app.data.local.NestCheckPreferences
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.model.ParentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val preferences: NestCheckPreferences
) {

    private fun buildDefaultChildren(): List<ChildProfile> {
        val active = preferences.getPersistedChildProfile()
        return listOf(
            active,
            ChildProfile(
                uid = "child_olivia",
                parentUid = "parent_1",
                name = "Olivia",
                age = 9,
                gender = "Girl",
                weight = 30.0f,
                height = 132.0f,
                grade = "Grade 4",
                dateOfBirth = "2017-08-20",
                avatarUrl = "🦄",
                dailyStepGoal = 6000
            ),
            ChildProfile(
                uid = "child_aarav",
                parentUid = "parent_1",
                name = "Aarav",
                age = 10,
                gender = "Boy",
                weight = 34.0f,
                height = 140.0f,
                grade = "Grade 5",
                dateOfBirth = "2016-05-15",
                avatarUrl = "🚀",
                dailyStepGoal = 6000
            )
        )
    }

    private val _parentProfile = MutableStateFlow(
        ParentUser(
            uid = "parent_1",
            email = preferences.parentEmail,
            name = preferences.parentName,
            roleInHouse = preferences.activeParentRole,
            phone = preferences.fatherPhone,
            emergencyContactPhone = preferences.emergencyContactPhone,
            children = buildDefaultChildren()
        )
    )
    val parentProfile: StateFlow<ParentUser> = _parentProfile.asStateFlow()

    private val _childProfile = MutableStateFlow(preferences.getPersistedChildProfile())
    val childProfile: StateFlow<ChildProfile> = _childProfile.asStateFlow()

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == NestCheckPreferences.KEY_ACTIVE_CHILD_NAME ||
            key == NestCheckPreferences.KEY_ACTIVE_CHILD_AVATAR ||
            key == NestCheckPreferences.KEY_ACTIVE_CHILD_AGE ||
            key == NestCheckPreferences.KEY_ACTIVE_CHILD_GENDER ||
            key == NestCheckPreferences.KEY_ACTIVE_CHILD_WEIGHT ||
            key == NestCheckPreferences.KEY_ACTIVE_CHILD_HEIGHT
        ) {
            val persisted = preferences.getPersistedChildProfile()
            _childProfile.value = persisted
            val updatedList = _parentProfile.value.children.map {
                if (it.uid == persisted.uid || it.name.equals(persisted.name, ignoreCase = true)) persisted else it
            }
            _parentProfile.value = _parentProfile.value.copy(children = updatedList)
        }
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    fun updateParentProfile(
        name: String,
        email: String,
        roleInHouse: String = "Father",
        phone: String = "",
        emergencyPhone: String = "+91 98765 43210"
    ) {
        _parentProfile.value = _parentProfile.value.copy(
            name = name,
            email = email,
            roleInHouse = roleInHouse,
            phone = phone,
            emergencyContactPhone = if (emergencyPhone.isNotBlank()) emergencyPhone else phone
        )
    }

    fun selectChildByName(name: String) {
        val found = _parentProfile.value.children.find { it.name.equals(name, ignoreCase = true) }
        val child = found ?: ChildProfile(name = name)
        _childProfile.value = child
        preferences.saveChildProfile(child)
    }

    fun selectChild(child: ChildProfile) {
        _childProfile.value = child
        preferences.saveChildProfile(child)
    }

    fun updateChildAvatar(avatar: String) {
        val updated = _childProfile.value.copy(avatarUrl = avatar)
        _childProfile.value = updated
        preferences.saveChildProfile(updated)
        val updatedList = _parentProfile.value.children.map {
            if (it.uid == updated.uid || it.name.equals(updated.name, ignoreCase = true)) updated else it
        }
        _parentProfile.value = _parentProfile.value.copy(children = updatedList)
    }

    fun setChildren(kids: List<ChildProfile>) {
        _parentProfile.value = _parentProfile.value.copy(children = kids)
        if (kids.isNotEmpty()) {
            _childProfile.value = kids.first()
            preferences.saveChildProfile(kids.first())
        }
    }

    fun addChild(child: ChildProfile) {
        val current = _parentProfile.value.children.toMutableList()
        current.add(child)
        _parentProfile.value = _parentProfile.value.copy(children = current)
        _childProfile.value = child
        preferences.saveChildProfile(child)
    }

    fun updateChildProfile(
        name: String,
        age: Int,
        gender: String,
        weight: Float,
        height: Float,
        dateOfBirth: String = "",
        avatarUrl: String = "🦊"
    ) {
        val updated = _childProfile.value.copy(
            name = name,
            age = age,
            gender = gender,
            weight = weight,
            height = height,
            dateOfBirth = if (dateOfBirth.isNotBlank()) dateOfBirth else _childProfile.value.dateOfBirth,
            avatarUrl = avatarUrl
        )
        _childProfile.value = updated
        preferences.saveChildProfile(updated)
        val updatedList = _parentProfile.value.children.map {
            if (it.uid == updated.uid || it.name.equals(name, ignoreCase = true) || it.name.equals(_childProfile.value.name, ignoreCase = true)) updated else it
        }
        _parentProfile.value = _parentProfile.value.copy(children = updatedList)
    }
}
