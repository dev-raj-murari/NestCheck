package com.nestcheck.app.ui.parent.appcontrol

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.AppCategory
import com.nestcheck.app.data.model.AppControlStatus
import com.nestcheck.app.data.model.AppUsageDetail
import com.nestcheck.app.data.repository.AppControlRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class AppFilterPill(val label: String) {
    ALL("All"),
    GAMES("Games"),
    SOCIAL("Social"),
    BLOCKED("Blocked")
}

@HiltViewModel
class AppControlViewModel @Inject constructor(
    private val appControlRepository: AppControlRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow(AppFilterPill.ALL)
    val selectedFilter: StateFlow<AppFilterPill> = _selectedFilter

    val filteredApps: StateFlow<List<AppUsageDetail>> = combine(
        appControlRepository.installedApps,
        _searchQuery,
        _selectedFilter
    ) { apps, query, filter ->
        apps.filter { app ->
            val matchesQuery = query.isBlank() ||
                app.appName.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                AppFilterPill.ALL -> true
                AppFilterPill.GAMES -> app.category == AppCategory.GAMING
                AppFilterPill.SOCIAL -> app.category == AppCategory.SOCIAL
                AppFilterPill.BLOCKED -> app.status == AppControlStatus.BLOCKED
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterSelected(filter: AppFilterPill) {
        _selectedFilter.value = filter
    }

    fun updateAppStatus(packageName: String, newStatus: AppControlStatus) {
        appControlRepository.updateAppStatus(packageName, newStatus)
    }

    fun setAppLimit(packageName: String, limitMinutes: Int) {
        appControlRepository.setAppLimit(packageName, limitMinutes)
    }

    fun toggleAppBlock(packageName: String) {
        appControlRepository.toggleAppBlock(packageName)
    }
}
