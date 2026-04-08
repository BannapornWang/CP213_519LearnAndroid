package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VibeSelectorChips() {
    val vibes = listOf("ธรรมชาติ", "คาเฟ่", "ถ่ายรูป", "ประวัติศาสตร์", "ผจญภัย", "ปาร์ตี้")
    var selectedVibes by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("Trip Vibe", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vibes) { vibe ->
                FilterChip(
                    selected = selectedVibes.contains(vibe),
                    onClick = {
                        val newSet = selectedVibes.toMutableSet()
                        if (newSet.contains(vibe)) newSet.remove(vibe) else newSet.add(vibe)
                        selectedVibes = newSet
                    },
                    label = { Text(vibe) }
                )
            }
        }
    }
}
