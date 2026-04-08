package com.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tripmate.data.models.Destination
import com.tripmate.data.models.NearbyPlace
import com.tripmate.ui.components.DestinationCard
import com.tripmate.ui.components.NearbyGemItem
import com.tripmate.ui.components.VibeSearchBar
import com.tripmate.ui.components.WeatherWidget

@Composable
fun HomeScreen(navController: NavController) {
    val trendingDestinations = listOf(
        Destination("1", "เชียงใหม่คาเฟ่ฮอปปิ้ง", "", 4.8, 1500.0, "Cafes", "ผ่อนคลาย", "Chiang Mai"),
        Destination("2", "ทะเลภูเก็ตหน้าร้อน", "", 4.9, 3500.0, "Beach", "ผจญภัย", "Phuket")
    )
    val nearbyPlaces = listOf(
        NearbyPlace("1", "Local Coffee House", "2 km", "1 hr", "Cafe"),
        NearbyPlace("2", "Hidden Art Gallery", "5 km", "2 hrs", "Arts")
    )
    val recommendations = listOf(
        Destination("3", "เขาใหญ่ธรรมชาติ", "", 4.7, 2000.0, "Nature", "ครอบครัว", "Khao Yai")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            HeaderSection()
        }
        item {
            VibeSearchBar()
        }
        item {
            SectionTitle("Trending Destinations")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(trendingDestinations) { dest ->
                    DestinationCard(destination = dest)
                }
            }
        }
        item {
            SectionTitle("Nearby Gems")
            Column {
                nearbyPlaces.forEach { place ->
                    NearbyGemItem(place = place)
                }
            }
        }
        item {
            SectionTitle("คุณอาจชอบ...")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(recommendations) { dest ->
                    DestinationCard(destination = dest)
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "สวัสดี, นักเดินทาง!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "กรุงเทพมหานคร", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
        WeatherWidget()
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
