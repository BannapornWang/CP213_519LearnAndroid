package com.tripmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RouteMapPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray, shape = MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Map, contentDescription = "Map Preview", modifier = Modifier.size(48.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Smart Route Map Preview", color = Color.DarkGray)
        }
    }
}
