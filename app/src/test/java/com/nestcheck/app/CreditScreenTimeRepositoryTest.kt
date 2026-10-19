package com.nestcheck.app

import com.nestcheck.app.data.model.CreditReasonCategory
import com.nestcheck.app.data.model.CreditRequestStatus
import com.nestcheck.app.data.model.CreditScreenTimeWallet
import com.nestcheck.app.data.model.CreditTransactionType
import com.nestcheck.app.data.repository.CreditRequestRepository
import com.nestcheck.app.data.repository.CreditScreenTimeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreditScreenTimeRepositoryTest {

    private lateinit var creditRepository: CreditScreenTimeRepository
    private lateinit var creditRequestRepository: CreditRequestRepository

    @Before
    fun setUp() {
        creditRepository = CreditScreenTimeRepository()
        creditRequestRepository = CreditRequestRepository(creditRepository)
    }

    @Test
    fun testCreditToScreenTimeConversionRule() {
        // Strict formula: 2 credits = 1 min
        val initialWallet = creditRepository.wallet.value
        assertEquals("Conversion rate must be 2 credits per minute", 2, CreditScreenTimeWallet.CREDITS_PER_MINUTE)
        assertEquals(
            "Initial credits should correctly calculate equivalent minutes",
            initialWallet.totalEarnedCredits / 2,
            initialWallet.screenTimeMinutesEquivalent
        )
    }

    @Test
    fun testRedeemCreditsForScreenTime() {
        val initialSeconds = creditRepository.wallet.value.activeRemainingSeconds
        val initialBalance = creditRepository.wallet.value.creditBalance

        // Redeem 30 credits (should yield 15 minutes = 900 seconds)
        val addedMinutes = creditRepository.redeemCreditsForScreenTime(30)
        assertEquals(15, addedMinutes)

        val updatedWallet = creditRepository.wallet.value
        assertEquals("Balance should decrease by 30 credits", initialBalance - 30, updatedWallet.creditBalance)
        assertEquals("Active screen time should increase by 900 seconds (15 mins)", initialSeconds + 900L, updatedWallet.activeRemainingSeconds)
    }

    @Test
    fun testSecondBySecondDeductionAndZeroLock() {
        // Set remaining minutes to 1 (60 seconds)
        creditRepository.setRemainingMinutesDirect(1)
        assertEquals(60L, creditRepository.wallet.value.activeRemainingSeconds)
        assertFalse(creditRepository.wallet.value.isCurfewLocked)

        // Deduct 30 seconds
        creditRepository.deductElapsedSeconds(30L)
        assertEquals(30L, creditRepository.wallet.value.activeRemainingSeconds)
        assertFalse(creditRepository.wallet.value.isCurfewLocked)

        // Deduct another 30 seconds -> drops to 0, locking device
        creditRepository.deductElapsedSeconds(30L)
        assertEquals(0L, creditRepository.wallet.value.activeRemainingSeconds)
        assertTrue("Device should lock when screen time is exhausted", creditRepository.wallet.value.isCurfewLocked)
    }

    @Test
    fun testCreditRequestApprovalWorkflow() {
        val initialCredits = creditRepository.wallet.value.creditBalance

        // Alex submits request for 40 credits (20 mins)
        val submitted = creditRequestRepository.submitRequest(
            taskTitle = "Practice Piano for 45 mins",
            creditsRequested = 40,
            category = CreditReasonCategory.EXTRA_CHORE,
            justification = "Completed grade 4 piece"
        )

        assertEquals(CreditRequestStatus.PENDING, submitted.status)
        assertEquals(20, submitted.screenTimeMinutesEquivalent)

        // Parent approves request
        creditRequestRepository.approveRequest(submitted.id)

        val approved = creditRequestRepository.requests.value.first { it.id == submitted.id }
        assertEquals(CreditRequestStatus.APPROVED, approved.status)

        // Child wallet must reflect +40 credits
        val updatedCredits = creditRepository.wallet.value.creditBalance
        assertEquals(initialCredits + 40, updatedCredits)

        // Verify transaction logged in ledger
        val lastTx = creditRepository.ledger.value.first()
        assertEquals(40, lastTx.amount)
        assertEquals(CreditTransactionType.PARENT_GRANTED, lastTx.transactionType)
    }
}
