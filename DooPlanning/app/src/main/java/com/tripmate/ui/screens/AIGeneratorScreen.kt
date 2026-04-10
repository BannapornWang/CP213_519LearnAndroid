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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripmate.viewmodel.AIGeneratorViewModel
import com.tripmate.viewmodel.UiState

@Composable
fun AIGeneratorScreen(navController: NavController, viewModel: AIGeneratorViewModel = viewModel()) {
    var destination by remember { mutableStateOf("") }
    var travelers by remember { mutableStateOf("1") }
    var pace by remember { mutableStateOf("พอดี") }

    val uiState by viewModel.uiState.collectAsState()
    val isGenerating = uiState is UiState.Loading

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
                onClick = { 
                    if (!isGenerating && destination.isNotBlank()) {
                        viewModel.generateItinerary(destination, travelers, pace)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isGenerating && destination.isNotBlank()
            ) {
                Text(if (isGenerating) "กำลังสร้าง..." else "🪄 สร้างแผนเที่ยว", style = MaterialTheme.typography.titleMedium)
            }
        }

        // 5. Handle Network States
        when (uiState) {
            is UiState.Loading -> {
                item { CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp)) }
            }
            is UiState.Success -> {
                item {
                    Text("✅ สร้างข้อมูลสำเร็จ! (กรุณาดูผลลัพธ์ใน Log ก่อน, ยังไม่ได้หน้าถัดไป)", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top=16.dp))
                    RouteMapPreview()
                }
            }
            is UiState.Error -> {
                item {
                    Text("❌ Error: ${(uiState as UiState.Error).message}", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top=16.dp))
                }
            }
            is UiState.Idle -> { 
                // Do nothing
            }
        }
    }
}
