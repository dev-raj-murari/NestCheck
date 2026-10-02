package com.nestcheck.app.data.model

data class CreditBalance(
    val childUid: String = "",
    val balance: Int = 0, // 1 credit = 5 min screen time
    val lifetimeEarned: Int = 0,
    val lifetimeSpent: Int = 0
)

data class CreditTransaction(
    val id: String = "",
    val childUid: String = "",
    val amount: Int = 0, // positive = earn, negative = spend
    val reason: String = "",
    val timestamp: Long = 0L
)
