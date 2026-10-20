package com.nestcheck.app.data.model

data class ChildProfile(
    val uid: String = "child_1",
    val parentUid: String = "parent_1",
    val name: String = "Leo",
    val age: Int = 11,
    val gender: String = "Boy",
    val weight: Float = 34.0f, // in kg
    val height: Float = 140.0f, // in cm
    val grade: String = "Grade 6",
    val dateOfBirth: String = "2015-05-15",
    val avatarUrl: String = "🦊",
    val bmi: Float = 17.3f,
    val dailyStepGoal: Int = 6000,
    val pinHash: String = "" // salted hash of the kid's login PIN (salt = uid)
)

data class ChildAvatarOption(
    val emoji: String,
    val label: String,
    val colorHex: String
)

object ChildAvatars {
    val PRESETS = listOf(
        ChildAvatarOption("🦊", "Fox", "#FF6D00"),
        ChildAvatarOption("🦁", "Lion", "#FFA000"),
        ChildAvatarOption("🐼", "Panda", "#263238"),
        ChildAvatarOption("🚀", "Rocket", "#0B57D0"),
        ChildAvatarOption("🎨", "Artist", "#7B1FA2"),
        ChildAvatarOption("⚽", "Soccer", "#2E7D32"),
        ChildAvatarOption("🎮", "Gamer", "#C2185B"),
        ChildAvatarOption("🦄", "Unicorn", "#D81B60"),
        ChildAvatarOption("🦉", "Owl", "#455A64"),
        ChildAvatarOption("⭐", "Star", "#F57F17")
    )
    const val DEFAULT_AVATAR = "🦊"
}
