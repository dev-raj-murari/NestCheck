package com.nestcheck.app.ui.parent.quotas

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.CategoryQuota
import com.nestcheck.app.ui.theme.*

@Composable
fun CategoryQuotasScreen(
    navController: NavController,
    viewModel: CategoryQuotasViewModel = hiltViewModel()
) {
    val quotas by viewModel.quotas.collectAsState()

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
                text = "Category Quotas",
                color = CyberTextBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Set daily maximum time limits for specific app categories. When a quota is reached, only apps in that category lock.",
            color = CyberTextDim,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
            items(quotas, key = { it.category.name }) { quota ->
                CategoryQuotaCard(
                    quota = quota,
                    onLimitChange = { newLimit -> viewModel.updateCategoryQuota(quota.category, newLimit) }
                )
            }
        }
    }
}

@Composable
private fun CategoryQuotaCard(
    quota: CategoryQuota,
    onLimitChange: (Int) -> Unit
) {
    var sliderValue by remember(quota.dailyLimitMinutes) { mutableFloatStateOf(quota.dailyLimitMinutes.toFloat()) }
    val progress = if (quota.dailyLimitMinutes > 0) (quota.usedMinutes.toFloat() / quota.dailyLimitMinutes.toFloat()).coerceIn(0f, 1f) else 0f
    val isExhausted = quota.usedMinutes >= quota.dailyLimitMinutes && quota.dailyLimitMinutes > 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(1.dp, if (isExhausted) AlertRed else CyberBorder, RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = quota.category.displayName, color = CyberTextBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (isExhausted) "QUOTA EXHAUSTED" else "${quota.usedMinutes}m / ${sliderValue.toInt()}m",
                    color = if (isExhausted) AlertRed else SuccessGreen,
                    fontSize = 12.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isExhausted) AlertRed else White,
                trackColor = CyberBorder
            )

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    onLimitChange(it.toInt())
                },
                valueRange = 0f..180f,
                
                colors = SliderDefaults.colors(
                    thumbColor = AccentBlue,
                    activeTrackColor = AccentBlue,
                    inactiveTrackColor = Color(0xFFCBD5E1)
                )
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Blocked (0m)", color = CyberTextDim, fontSize = 10.sp)
                Text("3 hours max", color = CyberTextDim, fontSize = 10.sp)
            }
        }
    }
}
