package com.tripmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VibeSearchBar() {
    var text by remember { mutableStateOf("") }
    val vibes = listOf("ผ่อนคลาย", "ผจญภัย", "โรแมนติก", "ครอบครัว", "ถ่ายรูปสวย")

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ค้นหาตามอารมณ์... เช่น คาเฟ่เงียบๆ...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vibes) { vibe ->
                AssistChip(
                    onClick = { text = vibe },
                    label = { Text(vibe) }
                )
            }
        }
    }
}
