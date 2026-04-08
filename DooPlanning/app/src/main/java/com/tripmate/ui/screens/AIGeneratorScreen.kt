package com.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tripmate.ui.components.*

@Composable
fun AIGeneratorScreen(navController: NavController) {
    var destination by remember { mutableStateOf("") }
    var travelers by remember { mutableStateOf("1") }
    var pace by remember { mutableStateOf("พอดี") }
    var isGenerating by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "สร้างทริปอัจฉริยะ",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 1. Trip Input Form
        item {
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("ปลายทาง (เช่น เชียงใหม่)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            DateRangePicker()
        }
        item {
            OutlinedTextField(
                value = travelers,
                onValueChange = { travelers = it },
                label = { Text("จำนวนผู้เดินทาง") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            VibeSelectorChips()
        }

        // 2. Budget Section
        item {
            BudgetSliderGroup()
        }

        // 3. Preferences Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("จังหวะการเดินทาง", style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val paces = listOf("ชิลล์ๆ", "พอดี", "เที่ยวเยอะ")
                    paces.forEach { p ->
                        FilterChip(
                            selected = pace == p,
                            onClick = { pace = p },
                            label = { Text(p) }
                        )
                    }
                }
            }
        }

        // 4. Generate Button
        item {
            Button(
                onClick = { isGenerating = !isGenerating },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isGenerating) "กำลังสร้าง..." else "🪄 สร้างแผนเที่ยว", style = MaterialTheme.typography.titleMedium)
            }
        }

        // 5. Build Generated Preview Mockup
        if (isGenerating) {
            item {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
        } else if (destination.isNotBlank()) {
            item {
                Text("Preview Itinerary (Mockup)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top=16.dp))
                RouteMapPreview()
            }
        }
    }
}
