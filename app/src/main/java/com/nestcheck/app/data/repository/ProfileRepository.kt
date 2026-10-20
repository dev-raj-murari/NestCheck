package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.model.ParentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor() {

    private val defaultChildren = listOf(
        ChildProfile(
            uid = "child_leo",
            parentUid = "parent_1",
            name = "Leo",
            age = 11,
            gender = "Boy",
            weight = 36.0f,
            height = 142.0f,
            grade = "Grade 6",
            dateOfBirth = "2015-05-15",
            avatarUrl = "🦊",
            dailyStepGoal = 6500
        ),
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

    private val _parentProfile = MutableStateFlow(
        ParentUser(
            uid = "parent_1",
            email = "devraj@nestcheck.app",
            name = "Dev Raj",
            roleInHouse = "Father",
            phone = "+91 98765 43210",
            emergencyContactPhone = "+91 98765 43210",
            children = defaultChildren
        )
    )
    val parentProfile: StateFlow<ParentUser> = _parentProfile.asStateFlow()

    private val _childProfile = MutableStateFlow(defaultChildren.first())
    val childProfile: StateFlow<ChildProfile> = _childProfile.asStateFlow()

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
        if (found != null) {
            _childProfile.value = found
        } else {
            val newKid = ChildProfile(name = name)
            _childProfile.value = newKid
        }
    }

    fun selectChild(child: ChildProfile) {
        _childProfile.value = child
    }

    fun updateChildAvatar(avatar: String) {
        val updated = _childProfile.value.copy(avatarUrl = avatar)
        _childProfile.value = updated
        val updatedList = _parentProfile.value.children.map {
            if (it.uid == updated.uid || it.name == updated.name) updated else it
        }
        _parentProfile.value = _parentProfile.value.copy(children = updatedList)
    }

    fun setChildren(kids: List<ChildProfile>) {
        _parentProfile.value = _parentProfile.value.copy(children = kids)
        if (kids.isNotEmpty()) {
            _childProfile.value = kids.first()
        }
    }

    fun addChild(child: ChildProfile) {
        val current = _parentProfile.value.children.toMutableList()
        current.add(child)
        _parentProfile.value = _parentProfile.value.copy(children = current)
        _childProfile.value = child
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
            dateOfBirth = dateOfBirth,
            avatarUrl = avatarUrl
        )
        _childProfile.value = updated
        val updatedList = _parentProfile.value.children.map {
            if (it.uid == updated.uid || it.name == updated.name) updated else it
        }
        _parentProfile.value = _parentProfile.value.copy(children = updatedList)
    }
}
