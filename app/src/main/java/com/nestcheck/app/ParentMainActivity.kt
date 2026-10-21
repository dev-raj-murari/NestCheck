package com.nestcheck.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nestcheck.app.navigation.NestCheckApp
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.NestCheckTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParentMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        setContent {
            NestCheckTheme {
                NestCheckApp(startDestination = Screen.ParentLogin.route)
            }
        }
    }
}
