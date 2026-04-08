package com.tripmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PhotoGalleryGrid(images: List<String>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(200.dp)
    ) {
        items(images.size + 1) { index ->
            if (index == 0) {
                Box(modifier = Modifier.aspectRatio(1f).background(Color.LightGray), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.AddAPhoto, contentDescription = "Add Photo")
                }
            } else {
                Box(modifier = Modifier.aspectRatio(1f).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
            }
        }
    }
}
