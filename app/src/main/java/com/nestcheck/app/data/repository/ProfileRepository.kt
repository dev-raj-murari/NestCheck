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

    private val _parentProfile = MutableStateFlow(
        ParentUser(
            uid = "parent_1",
            email = "devraj@nestcheck.app",
            name = "Dev Raj",
            roleInHouse = "Father",
            phone = "+91 98765 43210",
            children = listOf(
                ChildProfile(
                    uid = "child_1",
                    parentUid = "parent_1",
                    name = "Aarav",
                    age = 10,
                    gender = "Boy",
                    weight = 34.0f,
                    height = 140.0f,
                    dateOfBirth = "2016-05-15"
                )
            )
        )
    )
    val parentProfile: StateFlow<ParentUser> = _parentProfile.asStateFlow()

    private val _childProfile = MutableStateFlow(
        ChildProfile(
            uid = "child_1",
            parentUid = "parent_1",
            name = "Aarav",
            age = 10,
            gender = "Boy",
            weight = 34.0f,
            height = 140.0f,
            dateOfBirth = "2016-05-15"
        )
    )
    val childProfile: StateFlow<ChildProfile> = _childProfile.asStateFlow()

    fun updateParentProfile(name: String, email: String, roleInHouse: String = "Father", phone: String = "") {
        _parentProfile.value = _parentProfile.value.copy(
            name = name,
            email = email,
            roleInHouse = roleInHouse,
            phone = phone
        )
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
        dateOfBirth: String = ""
    ) {
        _childProfile.value = _childProfile.value.copy(
            name = name,
            age = age,
            gender = gender,
            weight = weight,
            height = height,
            dateOfBirth = dateOfBirth
        )
    }
}
