package com.nestcheck.app.data.model

data class ParentUser(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val children: List<String> = emptyList() // child UIDs
)
