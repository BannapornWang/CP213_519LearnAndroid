package com.tripmate.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VibeChip(vibe: String, selected: Boolean, onSelect: (Boolean) -> Unit) {
    FilterChip(
        selected = selected,
        onClick = { onSelect(!selected) },
        label = { Text(vibe) },
        leadingIcon = { Icon(Icons.Filled.LocalFireDepartment, contentDescription = null) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}
