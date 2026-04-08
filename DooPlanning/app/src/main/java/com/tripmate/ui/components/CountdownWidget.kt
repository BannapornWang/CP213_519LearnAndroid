package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CountdownWidget(targetDate: String, daysLeft: Int) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp).padding(8.dp)) {
        CircularProgressIndicator(
            progress = { (daysLeft / 30f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$daysLeft", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = "Days", style = MaterialTheme.typography.labelSmall)
        }
    }
}
