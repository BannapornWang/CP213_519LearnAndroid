package com.tripmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tripmate.navigation.NavGraph
import com.tripmate.ui.theme.TripMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripMateTheme {
                NavGraph()
            }
        }
    }
}
