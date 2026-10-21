package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.CreditBalance
import com.nestcheck.app.data.model.CreditTransaction
import com.nestcheck.app.data.model.RewardPrivilege
import com.nestcheck.app.data.local.NestCheckPreferences
import com.nestcheck.app.data.model.CreditTransactionType
import com.nestcheck.app.data.model.RewardType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepository @Inject constructor(
    private val screenTimeRepository: ScreenTimeRepository,
    private val creditScreenTimeRepository: CreditScreenTimeRepository,
    private val preferences: NestCheckPreferences
) {

    private val _creditBalance = MutableStateFlow(
        CreditBalance(
            childUid = "child_1",
            balance = preferences.childCredits,
            lifetimeEarned = 260,
            lifetimeSpent = 120
        )
    )
    val creditBalance: StateFlow<CreditBalance> = _creditBalance.asStateFlow()

    private val _transactions = MutableStateFlow(
        listOf(
            CreditTransaction(
                id = UUID.randomUUID().toString(),
                childUid = "child_1",
                amount = 20,
                reason = "Math Homework Completed",
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            CreditTransaction(
                id = UUID.randomUUID().toString(),
                childUid = "child_1",
                amount = -8,
                reason = "Redeemed +40m Screen Time",
                timestamp = System.currentTimeMillis() - 86400000L
            ),
            CreditTransaction(
                id = UUID.randomUUID().toString(),
                childUid = "child_1",
                amount = 15,
                reason = "Cleaned Bedroom Chore",
                timestamp = System.currentTimeMillis() - 3600000L * 5
            ),
            CreditTransaction(
                id = UUID.randomUUID().toString(),
                childUid = "child_1",
                amount = -4,
                reason = "Redeemed +20m Screen Time",
                timestamp = System.currentTimeMillis() - 3600000L * 2
            )
        )
    )
    val transactions: StateFlow<List<CreditTransaction>> = _transactions.asStateFlow()

    val availableRewards: List<RewardPrivilege> = listOf(
        RewardPrivilege(
            id = "rew_20m",
            title = "+20 Mins Screen Time",
            costCredits = 4,
            bonusMinutes = 20,
            description = "Instant 20 minutes bonus added to today's limit",
            type = RewardType.SCREEN_TIME_MINUTES
        ),
        RewardPrivilege(
            id = "rew_40m",
            title = "+40 Mins Screen Time",
            costCredits = 8,
            bonusMinutes = 40,
            description = "Instant 40 minutes bonus added to today's limit",
            type = RewardType.SCREEN_TIME_MINUTES
        ),
        RewardPrivilege(
            id = "rew_bedtime",
            title = "15 Mins Bedtime Delay",
            costCredits = 3,
            bonusMinutes = 15,
            description = "Extend evening bedtime curfew by 15 minutes",
            type = RewardType.BEDTIME_DELAY
        ),
        RewardPrivilege(
            id = "rew_movie",
            title = "Weekend Movie Night",
            costCredits = 20,
            bonusMinutes = 120,
            description = "Special family movie privilege approval",
            type = RewardType.CUSTOM_PRIVILEGE
        )
    )

    fun awardCredits(amount: Int, reason: String) {
        creditScreenTimeRepository.addCredits(amount, CreditTransactionType.TASK_COMPLETED, reason)
        val current = _creditBalance.value
        val newBalance = creditScreenTimeRepository.wallet.value.totalCredits
        val newEarned = current.lifetimeEarned + amount
        _creditBalance.value = current.copy(
            balance = newBalance,
            lifetimeEarned = newEarned
        )

        val newTx = CreditTransaction(
            id = UUID.randomUUID().toString(),
            childUid = current.childUid,
            amount = amount,
            reason = reason,
            timestamp = System.currentTimeMillis()
        )
        _transactions.value = listOf(newTx) + _transactions.value
    }

    fun redeemReward(reward: RewardPrivilege): Boolean {
        val current = _creditBalance.value
        if (current.balance < reward.costCredits) {
            return false
        }

        val newBalance = current.balance - reward.costCredits
        val newSpent = current.lifetimeSpent + reward.costCredits
        _creditBalance.value = current.copy(
            balance = newBalance,
            lifetimeSpent = newSpent
        )

        val newTx = CreditTransaction(
            id = UUID.randomUUID().toString(),
            childUid = current.childUid,
            amount = -reward.costCredits,
            reason = "Redeemed: ${reward.title}",
            timestamp = System.currentTimeMillis()
        )
        _transactions.value = listOf(newTx) + _transactions.value

        if (reward.type == RewardType.SCREEN_TIME_MINUTES) {
            screenTimeRepository.addBonusMinutes(reward.bonusMinutes)
        }

        return true
    }
}
