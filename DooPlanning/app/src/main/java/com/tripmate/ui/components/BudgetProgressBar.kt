package com.tripmate.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BudgetProgressBar(budget: Double, spent: Double, breakdown: Map<String, Double> = emptyMap()) {
    val progress = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1f) else 0f
    
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnim")
    
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = "ใช้ไปแล้ว: ฿$spent")
            Text(text = "คงเหลือ: ฿${(budget - spent).coerceAtLeast(0.0)}")
        }
        
        Box(modifier = Modifier.fillMaxWidth().height(12.dp).padding(top = 4.dp).background(Color.LightGray, shape = MaterialTheme.shapes.small)) {
            Box(modifier = Modifier.fillMaxWidth(animatedProgress).fillMaxHeight().background(if(animatedProgress > 0.9f) Color.Red else MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small))
        }
        
        if (breakdown.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                breakdown.forEach { (category, amount) ->
                    Text(text = "$category: ฿$amount", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
