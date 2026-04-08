package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tripmate.data.models.BookingInfo

@Composable
fun BookingInfoCard(booking: BookingInfo) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Filled.AirplaneTicket, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = booking.type, style = MaterialTheme.typography.titleMedium)
                Text(text = "${booking.provider} | ${booking.dateTime}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Ref: ${booking.confirmationCode}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            Button(onClick = { /* Copy */ }) { Text("Copy") }
        }
    }
}
