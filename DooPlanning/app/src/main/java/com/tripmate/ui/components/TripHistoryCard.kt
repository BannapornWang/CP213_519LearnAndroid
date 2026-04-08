package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripmate.data.models.TripRecord

@Composable
fun TripHistoryCard(trip: TripRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = trip.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = trip.destination, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${trip.startDate} - ${trip.endDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
                TripStatusBadge(status = trip.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (trip.status == "Upcoming") {
                Text(text = "อีก 15 วัน!", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            
            BudgetComparisonBar(budget = trip.budget, spent = trip.actualSpent)
            
            if (trip.status == "Completed") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ความทรงจำ: ${trip.memories.size} รูป/บันทึก", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
