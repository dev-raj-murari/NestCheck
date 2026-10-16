package com.nestcheck.app.ui.parent.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.NestCheckNotification
import com.nestcheck.app.data.model.NotificationType
import com.nestcheck.app.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class NotificationFilter {
    ALL, ALERTS, WELLBEING, SYSTEM
}

@HiltViewModel
class NotificationHubViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(NotificationFilter.ALL)
    val selectedFilter: StateFlow<NotificationFilter> = _selectedFilter.asStateFlow()

    val filteredNotifications: StateFlow<List<NestCheckNotification>> = combine(
        notificationRepository.notifications,
        _selectedFilter
    ) { notifs, filter ->
        when (filter) {
            NotificationFilter.ALL -> notifs
            NotificationFilter.ALERTS -> notifs.filter {
                it.type in listOf(NotificationType.SOS, NotificationType.LOW_BATTERY, NotificationType.NSFW_ALERT, NotificationType.DEVICE_TAMPER)
            }
            NotificationFilter.WELLBEING -> notifs.filter {
                it.type in listOf(NotificationType.STEP_GOAL_COMPLETE, NotificationType.SCREEN_TIME_EXCEEDED, NotificationType.HOMEWORK_COMPLETE)
            }
            NotificationFilter.SYSTEM -> notifs.filter {
                it.type in listOf(NotificationType.INFO, NotificationType.GEOFENCE_ENTER, NotificationType.GEOFENCE_EXIT, NotificationType.APP_INSTALL_REQUEST)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: NotificationFilter) {
        _selectedFilter.value = filter
    }

    fun markAsRead(id: String) {
        notificationRepository.markAsRead(id)
    }

    fun markAllAsRead() {
        notificationRepository.markAllAsRead()
    }

    fun dismiss(id: String) {
        notificationRepository.dismissNotification(id)
    }

    fun clearAll() {
        notificationRepository.clearAll()
    }
}
