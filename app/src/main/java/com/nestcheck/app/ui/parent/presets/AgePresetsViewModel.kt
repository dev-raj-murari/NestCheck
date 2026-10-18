package com.nestcheck.app.ui.parent.presets

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.AgeGroupPreset
import com.nestcheck.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AgePresetsViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    fun applyPreset(preset: AgeGroupPreset) {
        habitRepository.applyAgePreset(preset)
    }
}
