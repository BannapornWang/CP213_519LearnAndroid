package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tripmate.data.models.DayPlan

@Composable
fun ItineraryPreviewCard(dayPlan: DayPlan) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "วันที่: ${dayPlan.date}", style = MaterialTheme.typography.titleMedium)
            Text(text = "งบประมาณประจำวัน: ${dayPlan.dayBudget} THB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            
            Spacer(modifier = Modifier.height(8.dp))
            dayPlan.activities.forEach { activity ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Filled.Info, contentDescription = "Activity", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = activity.place, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${activity.startTime} - ${activity.endTime} | ${activity.duration}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
