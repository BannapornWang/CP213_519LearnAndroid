package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tripmate.data.models.Expense

@Composable
fun ExpenseItem(expense: Expense) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = expense.description, style = MaterialTheme.typography.bodyLarge)
            Text(text = "${expense.date} • ${expense.category.name}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(text = "-฿${expense.amount}", style = MaterialTheme.typography.titleMedium, color = Color.Red)
    }
}
