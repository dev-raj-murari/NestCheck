package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.CreditLedgerRecord
import com.nestcheck.app.data.model.CreditScreenTimeWallet
import com.nestcheck.app.data.model.CreditTransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditScreenTimeRepository @Inject constructor() {

    private val _wallet = MutableStateFlow(
        CreditScreenTimeWallet(
            totalCredits = 180,
            activeRemainingSeconds = 90 * 60L, // 90 mins (180 credits / 2)
            isTrackingActive = true,
            conversionRateCreditsPerMin = 2,
            todayEarnedCredits = 60,
            todayBurnedCredits = 40,
            lastSyncTimestamp = System.currentTimeMillis()
        )
    )
    val wallet: StateFlow<CreditScreenTimeWallet> = _wallet.asStateFlow()

    private var burnCarrySeconds = 0L

    private val _ledger = MutableStateFlow(
        listOf(
            CreditLedgerRecord(
                id = UUID.randomUUID().toString(),
                amount = 40,
                transactionType = CreditTransactionType.TASK_COMPLETED,
                title = "Completed Math Revision Assignment",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 3,
                equivalentMinutes = 20
            ),
            CreditLedgerRecord(
                id = UUID.randomUUID().toString(),
                amount = -30,
                transactionType = CreditTransactionType.SCREEN_TIME_BURN,
                title = "YouTube Kids & Minecraft Session (15 mins)",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 2,
                equivalentMinutes = -15
            ),
            CreditLedgerRecord(
                id = UUID.randomUUID().toString(),
                amount = 20,
                transactionType = CreditTransactionType.HOMEWORK_DONE,
                title = "Cleaned Study Desk & Bag Pack",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 5,
                equivalentMinutes = 10
            )
        )
    )
    val ledger: StateFlow<List<CreditLedgerRecord>> = _ledger.asStateFlow()

    fun convertCreditsToMinutes(credits: Int): Int = credits / 2

    fun convertMinutesToCredits(minutes: Int): Int = minutes * 2

    fun addCredits(amount: Int, type: CreditTransactionType, title: String) {
        val current = _wallet.value
        val newCredits = current.totalCredits + amount
        val additionalSeconds = (amount / 2) * 60L
        val newRemainingSeconds = (current.activeRemainingSeconds + additionalSeconds).coerceAtLeast(0L)

        _wallet.value = current.copy(
            totalCredits = newCredits,
            activeRemainingSeconds = newRemainingSeconds,
            todayEarnedCredits = current.todayEarnedCredits + amount,
            lastSyncTimestamp = System.currentTimeMillis()
        )

        val record = CreditLedgerRecord(
            id = UUID.randomUUID().toString(),
            amount = amount,
            transactionType = type,
            title = title,
            timestamp = System.currentTimeMillis(),
            equivalentMinutes = amount / 2
        )
        _ledger.value = listOf(record) + _ledger.value
    }

    /**
     * Deducts elapsed real-time usage (e.g. 1 second or 30 seconds).
     * 2 Credits = 1 Minute (60 seconds) -> 1 Credit burned every 30 seconds.
     */
    fun deductElapsedSeconds(seconds: Long) {
        val current = _wallet.value
        if (!current.isTrackingActive || current.activeRemainingSeconds <= 0 || seconds <= 0) return

        val consumed = minOf(seconds, current.activeRemainingSeconds)
        val newRemainingSeconds = current.activeRemainingSeconds - consumed
        // 2 credits per 60 seconds = 1 credit per 30 seconds. Per-second ticks used to round
        // down to 0 credits every time, so carry the leftover seconds between ticks.
        burnCarrySeconds += consumed
        val burnedCreditsThisTick = (burnCarrySeconds / SECONDS_PER_CREDIT).toInt()
        burnCarrySeconds %= SECONDS_PER_CREDIT
        val newTotalCredits = (current.totalCredits - burnedCreditsThisTick).coerceAtLeast(0)

        _wallet.value = current.copy(
            activeRemainingSeconds = newRemainingSeconds,
            totalCredits = newTotalCredits,
            todayBurnedCredits = current.todayBurnedCredits + burnedCreditsThisTick,
            lastSyncTimestamp = System.currentTimeMillis()
        )
    }

    fun redeemCreditsForScreenTime(creditsToSpend: Int): Int {
        val current = _wallet.value
        val affordable = creditsToSpend.coerceIn(0, current.totalCredits)
        val addedMinutes = affordable / 2
        // Only spend credits that actually convert to whole minutes (odd credit isn't wasted).
        val spend = addedMinutes * 2
        val addedSeconds = addedMinutes * 60L

        _wallet.value = current.copy(
            totalCredits = current.totalCredits - spend,
            activeRemainingSeconds = current.activeRemainingSeconds + addedSeconds,
            todayBurnedCredits = current.todayBurnedCredits + spend,
            lastSyncTimestamp = System.currentTimeMillis()
        )

        val record = CreditLedgerRecord(
            id = UUID.randomUUID().toString(),
            amount = -spend,
            transactionType = CreditTransactionType.MANUAL_REDEEM,
            title = "Redeemed $spend credits for $addedMinutes mins screen time",
            timestamp = System.currentTimeMillis(),
            equivalentMinutes = addedMinutes
        )
        _ledger.value = listOf(record) + _ledger.value
        return addedMinutes
    }

    fun toggleTracking(isActive: Boolean) {
        _wallet.value = _wallet.value.copy(isTrackingActive = isActive)
    }

    fun setRemainingMinutesDirect(minutes: Int) {
        val current = _wallet.value
        val newSeconds = (minutes * 60L).coerceAtLeast(0L)
        val correspondingCredits = minutes * 2
        _wallet.value = current.copy(
            activeRemainingSeconds = newSeconds,
            totalCredits = correspondingCredits,
            lastSyncTimestamp = System.currentTimeMillis()
        )
    }

    private companion object {
        const val SECONDS_PER_CREDIT = 30L
    }
}
