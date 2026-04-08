package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BudgetComparisonBar(budget: Double, spent: Double) {
    val progress = if (budget > 0) (spent / budget).toFloat() else 0f
    val progressColor = if (progress > 1f) Color.Red else MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "ใช้ไป: $spent THB", style = MaterialTheme.typography.bodySmall, color = progressColor)
            Text(text = "งบ: $budget THB", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceAtMost(1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = progressColor,
            trackColor = Color.LightGray
        )
    }
}
