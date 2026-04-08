package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WeatherWidget(location: String = "Bangkok", dates: String = "Today") {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.WbSunny, contentDescription = "Weather", tint = Color(0xFFFF9800), modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = "32°C", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
