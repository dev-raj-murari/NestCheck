package com.nestcheck.app.ui.parent.ai

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.repository.AIDigest
import com.nestcheck.app.data.repository.AIDigestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AIDigestViewModel @Inject constructor(
    private val digestRepository: AIDigestRepository
) : ViewModel() {

    val digest: StateFlow<AIDigest> = digestRepository.digest

    fun refreshDigest() {
        digestRepository.regenerateDigest()
    }
}
