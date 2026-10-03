package com.nestcheck.app.data.repository

import com.nestcheck.app.core.BMICalculator
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
            phone = "+91 98765 43210"
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
            grade = "Grade 5",
            bmi = BMICalculator.calculateBMI(34.0f, 140.0f),
            dailyStepGoal = BMICalculator.calculateStepGoal(10, BMICalculator.calculateBMI(34.0f, 140.0f))
        )
    )
    val childProfile: StateFlow<ChildProfile> = _childProfile.asStateFlow()

    fun updateParentProfile(name: String, email: String, phone: String = "") {
        _parentProfile.value = _parentProfile.value.copy(
            name = name,
            email = email,
            phone = phone
        )
    }

    fun updateChildProfile(
        name: String,
        age: Int,
        gender: String,
        weight: Float,
        height: Float,
        grade: String = "Grade 5"
    ) {
        val calculatedBmi = BMICalculator.calculateBMI(weight, height)
        val calculatedGoal = BMICalculator.calculateStepGoal(age, calculatedBmi)
        _childProfile.value = _childProfile.value.copy(
            name = name,
            age = age,
            gender = gender,
            weight = weight,
            height = height,
            grade = grade,
            bmi = calculatedBmi,
            dailyStepGoal = calculatedGoal
        )
    }
}
