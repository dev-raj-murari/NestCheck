package com.nestcheck.app.ui.parent.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.Typography

@Composable
fun ParentDashboardScreen(navController: NavController) {
    val items = listOf(
        "Screen Time" to Screen.ScreenTime.route,
        "App Control" to Screen.AppControl.route,
        "Location" to Screen.Location.route,
        "Content Filters" to Screen.ContentFilter.route,
        "Reports" to Screen.Reports.route,
        "Credits" to Screen.Credits.route,
        "Child Profile" to Screen.ChildProfile.route
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Parent Dashboard", style = Typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { (label, route) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .border(1.dp, Color.Black)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, style = Typography.bodyLarge)
                }
            }
        }
    }
}
