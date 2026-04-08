package com.tripmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MapRouteView(places: List<String>, route: String) {
    // Mocking an embedded Google Maps view
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Map, contentDescription = "Map Segment", modifier = Modifier.size(48.dp), tint = Color(0xFF1E88E5))
            Text(text = "Map Route View: $route", color = Color.DarkGray)
            Text(text = "${places.size} stops", color = Color.Gray)
        }
    }
}
