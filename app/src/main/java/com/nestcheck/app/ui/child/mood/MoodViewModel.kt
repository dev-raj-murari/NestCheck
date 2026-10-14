package com.nestcheck.app.ui.child.mood

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.MoodEntry
import com.nestcheck.app.data.model.MoodType
import com.nestcheck.app.data.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {

    val moodHistory: StateFlow<List<MoodEntry>> = healthRepository.moodHistory

    private val _selectedMood = MutableStateFlow(MoodType.GOOD)
    val selectedMood: StateFlow<MoodType> = _selectedMood.asStateFlow()

    private val _noteText = MutableStateFlow("")
    val noteText: StateFlow<String> = _noteText.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun selectMood(mood: MoodType) {
        _selectedMood.value = mood
        _saveSuccess.value = false
    }

    fun updateNote(note: String) {
        _noteText.value = note
    }

    fun saveMoodEntry() {
        healthRepository.recordMood(_selectedMood.value, _noteText.value)
        _noteText.value = ""
        _saveSuccess.value = true
    }
}
