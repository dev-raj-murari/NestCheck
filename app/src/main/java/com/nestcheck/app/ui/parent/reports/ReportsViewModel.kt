package com.nestcheck.app.ui.parent.reports

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.repository.ReportRepository
import com.nestcheck.app.data.repository.WeeklyActivityReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    val weeklyReport: StateFlow<WeeklyActivityReport> = reportRepository.weeklyReport

    fun refreshReport() {
        reportRepository.refreshReport()
    }
}
