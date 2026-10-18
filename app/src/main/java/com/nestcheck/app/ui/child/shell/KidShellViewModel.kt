package com.nestcheck.app.ui.child.shell

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KidShellViewModel @Inject constructor(
    val syncManager: RealtimeFamilySyncManager
) : ViewModel()
