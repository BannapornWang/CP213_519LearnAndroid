package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BudgetSliderGroup() {
    var totalBudget by remember { mutableFloatStateOf(10000f) }
    var transport by remember { mutableFloatStateOf(20f) }
    var accommodation by remember { mutableFloatStateOf(40f) }
    var food by remember { mutableFloatStateOf(20f) }
    var activities by remember { mutableFloatStateOf(10f) }
    var misc by remember { mutableFloatStateOf(10f) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("Total Budget: ${totalBudget.toInt()} THB", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = totalBudget,
            onValueChange = { totalBudget = it },
            valueRange = 1000f..50000f,
            steps = 49
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("Budget Breakdown", style = MaterialTheme.typography.titleMedium)
        
        BudgetBreakdownRow("เดินทาง", transport) { transport = it }
        BudgetBreakdownRow("ที่พัก", accommodation) { accommodation = it }
        BudgetBreakdownRow("อาหาร", food) { food = it }
        BudgetBreakdownRow("กิจกรรม", activities) { activities = it }
        BudgetBreakdownRow("สำรอง", misc) { misc = it }
    }
}

@Composable
fun BudgetBreakdownRow(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(text = "$label: ${value.toInt()}%", modifier = Modifier.weight(1f))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            modifier = Modifier.weight(2f)
        )
    }
}
