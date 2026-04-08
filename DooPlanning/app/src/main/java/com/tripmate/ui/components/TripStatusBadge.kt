package com.tripmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TripStatusBadge(status: String) {
    val backgroundColor = when(status) {
        "Upcoming" -> MaterialTheme.colorScheme.primary
        "Ongoing" -> MaterialTheme.colorScheme.secondary
        "Completed" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }
    Box(
        modifier = Modifier
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
