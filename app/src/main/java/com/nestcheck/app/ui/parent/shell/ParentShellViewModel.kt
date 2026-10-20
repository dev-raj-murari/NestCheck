package com.nestcheck.app.ui.parent.shell

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.repository.ProfileRepository
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ParentShellViewModel @Inject constructor(
    val syncManager: RealtimeFamilySyncManager,
    val profileRepository: ProfileRepository
) : ViewModel()
