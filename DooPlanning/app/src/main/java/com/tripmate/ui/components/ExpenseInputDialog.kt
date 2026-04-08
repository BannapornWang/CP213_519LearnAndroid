package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseInputDialog(onSave: (amount: Double, category: String, desc: String) -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    var amount by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Add Expense", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount ฿") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            
            Text("Category")
            Row {
                FilterChip(selected = selectedCategory == "Food", onClick = { selectedCategory = "Food" }, label = { Text("Food") }, leadingIcon = { Icon(Icons.Filled.Fastfood, null) })
            }
            
            TextButton(onClick = { /* File picker action */ }) {
                Icon(Icons.Filled.AddAPhoto, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Attach Photo")
            }
            
            Button(
                onClick = { onSave(amount.toDoubleOrNull() ?: 0.0, selectedCategory, desc); onDismiss() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Expense")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
