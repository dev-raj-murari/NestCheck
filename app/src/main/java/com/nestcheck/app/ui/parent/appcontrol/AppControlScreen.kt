package com.nestcheck.app.ui.parent.appcontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.AppControlStatus
import com.nestcheck.app.data.model.AppUsageDetail
import com.nestcheck.app.ui.theme.*

@Composable
fun AppControlScreen(
    navController: NavController,
    viewModel: AppControlViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val apps by viewModel.filteredApps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberTextBright
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "App Management",
                color = CyberTextBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search installed packages...", color = CyberTextDim, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = CyberTextDim) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberCard,
                unfocusedContainerColor = CyberCard,
                focusedBorderColor = White,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = CyberTextBright,
                unfocusedTextColor = CyberTextBright
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // FILTER PILLS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppFilterPill.values().forEach { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) White else CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(6.dp))
                        .clickable { viewModel.onFilterSelected(filter) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.label,
                        color = if (isSelected) Black else CyberTextDim,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // APPS LIST
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(apps, key = { it.packageName }) { app ->
                AppControlItemCard(
                    app = app,
                    onStatusSelected = { newStatus -> viewModel.updateAppStatus(app.packageName, newStatus) },
                    onLimitChanged = { newLimit -> viewModel.setAppLimit(app.packageName, newLimit) }
                )
            }
        }
    }
}

@Composable
private fun AppControlItemCard(
    app: AppUsageDetail,
    onStatusSelected: (AppControlStatus) -> Unit,
    onLimitChanged: (Int) -> Unit
) {
    var sliderValue by remember(app.dailyLimitMinutes) {
        mutableFloatStateOf(if (app.dailyLimitMinutes > 0) app.dailyLimitMinutes.toFloat() else 60f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(
                1.dp,
                if (app.status == AppControlStatus.BLOCKED) AlertRed.copy(alpha = 0.5f) else CyberBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        color = CyberTextBright,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${app.category.displayName} • ${app.packageName}",
                        color = CyberTextDim,
                        fontSize = 11.sp
                    )
                }

                // 3-WAY STATUS SELECTOR
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppControlStatus.values().forEach { status ->
                        val isCurrent = app.status == status
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isCurrent) {
                                        when (status) {
                                            AppControlStatus.ALLOWED -> SuccessGreen
                                            AppControlStatus.TIME_LIMITED -> WarningAmber
                                            AppControlStatus.BLOCKED -> AlertRed
                                        }
                                    } else {
                                        CyberBlack
                                    }
                                )
                                .border(0.5.dp, CyberBorder, RoundedCornerShape(4.dp))
                                .clickable { onStatusSelected(status) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = status.displayName,
                                color = if (isCurrent) Black else CyberTextDim,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // IF TIME LIMITED: SHOW SLIDER
            if (app.status == AppControlStatus.TIME_LIMITED) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CyberBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Quota:", color = CyberTextDim, fontSize = 11.sp)
                    Text(
                        text = "${sliderValue.toInt()} mins",
                        color = White,
                        fontSize = 12.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        onLimitChanged(it.toInt())
                    },
                    valueRange = 15f..180f,

                    colors = SliderDefaults.colors(
                        thumbColor = WarningAmber,

                        activeTrackColor = WarningAmber,
                        inactiveTrackColor = Color(0xFFCBD5E1)


                    )
                )
            }
        }
    }
}
