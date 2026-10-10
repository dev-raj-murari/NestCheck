package com.nestcheck.app.data.model

data class ParentUser(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val roleInHouse: String = "Father", // e.g. Father, Mother, Guardian
    val phone: String = "",
    val children: List<ChildProfile> = emptyList()
)

data class KidUser(
    val uid: String = "",
    val name: String = "",
    val parentEmail: String = "",
    val age: Int = 10,
    val gender: String = "Boy"
)
