package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class TimelineActivity(val time: String, val title: String, val subtitle: String, val isCompleted: Boolean, val travelToNextMins: Int? = null)

@Composable
fun TimelineView(activities: List<TimelineActivity>, showTravelTime: Boolean = true) {
    Column {
        activities.forEachIndexed { index, activity ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
                    Text(text = activity.time, style = MaterialTheme.typography.labelMedium)
                    Icon(Icons.Filled.Circle, contentDescription = null, tint = if(activity.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.size(12.dp))
                    
                    if (index < activities.size - 1) {
                        Divider(color = Color.LightGray, modifier = Modifier.height(40.dp).width(2.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = activity.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = activity.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    
                    if (showTravelTime && activity.travelToNextMins != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Filled.DirectionsWalk, contentDescription = "Travel", modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${activity.travelToNextMins} mins to next", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
