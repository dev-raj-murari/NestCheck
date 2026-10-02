package com.nestcheck.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nestcheck.app.navigation.NestCheckApp
import com.nestcheck.app.ui.theme.NestCheckTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NestCheckTheme {
                NestCheckApp()
            }
        }
    }
}
