package com.nestcheck.app.ui.sinan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.SafeSearchConfig
import com.nestcheck.app.data.model.SafeSearchQueryLog
import com.nestcheck.app.data.model.SearchEngineType
import com.nestcheck.app.data.repository.SafeSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SafeSearchViewModel @Inject constructor(
    private val safeSearchRepository: SafeSearchRepository
) : ViewModel() {

    val config: StateFlow<SafeSearchConfig> = safeSearchRepository.config
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SafeSearchConfig())

    val logs: StateFlow<List<SafeSearchQueryLog>> = safeSearchRepository.logs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = mutableStateOf("solar system planets 3d model")
    val searchQuery: State<String> = _searchQuery

    private val _selectedEngine = mutableStateOf(SearchEngineType.GOOGLE)
    val selectedEngine: State<SearchEngineType> = _selectedEngine

    private val _lastResult = mutableStateOf<SafeSearchQueryLog?>(null)
    val lastResult: State<SafeSearchQueryLog?> = _lastResult

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSelectEngine(engine: SearchEngineType) {
        _selectedEngine.value = engine
    }

    fun toggleEngine(engine: SearchEngineType, enabled: Boolean) {
        viewModelScope.launch {
            safeSearchRepository.toggleEngineEnforcement(engine, enabled)
        }
    }

    fun toggleTamperLock(enabled: Boolean) {
        viewModelScope.launch {
            safeSearchRepository.setTamperLock(enabled)
        }
    }

    fun toggleBlockIncognito(enabled: Boolean) {
        viewModelScope.launch {
            safeSearchRepository.setBlockIncognito(enabled)
        }
    }

    fun toggleYouTubeRestricted(enabled: Boolean) {
        viewModelScope.launch {
            safeSearchRepository.setYouTubeRestricted(enabled)
        }
    }

    fun simulateSearch(overrideQuery: String? = null) {
        val q = overrideQuery ?: _searchQuery.value
        if (overrideQuery != null) {
            _searchQuery.value = overrideQuery
        }
        val res = safeSearchRepository.simulateSearch(q, _selectedEngine.value)
        _lastResult.value = res
    }
}
