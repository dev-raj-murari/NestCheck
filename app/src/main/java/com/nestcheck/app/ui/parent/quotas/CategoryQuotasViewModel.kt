package com.nestcheck.app.ui.parent.quotas

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.AppCategory
import com.nestcheck.app.data.model.CategoryQuota
import com.nestcheck.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryQuotasViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    val quotas: StateFlow<List<CategoryQuota>> = habitRepository.categoryQuotas

    fun updateCategoryQuota(category: AppCategory, limitMinutes: Int) {
        habitRepository.updateCategoryLimit(category, limitMinutes)
    }
}
