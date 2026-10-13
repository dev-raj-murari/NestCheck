package com.nestcheck.app.ui.child.credits

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.CreditBalance
import com.nestcheck.app.data.model.CreditTransaction
import com.nestcheck.app.data.model.RewardPrivilege
import com.nestcheck.app.data.repository.CreditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MyCreditsViewModel @Inject constructor(
    private val creditRepository: CreditRepository
) : ViewModel() {

    val creditBalance: StateFlow<CreditBalance> = creditRepository.creditBalance
    val transactions: StateFlow<List<CreditTransaction>> = creditRepository.transactions
    val availableRewards: List<RewardPrivilege> = creditRepository.availableRewards

    fun redeemReward(reward: RewardPrivilege): Boolean {
        return creditRepository.redeemReward(reward)
    }
}
