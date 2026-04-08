package com.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.tripmate.data.models.TripRecord
import com.tripmate.ui.components.SearchFilterBar
import com.tripmate.ui.components.TripHistoryCard

@Composable
fun RecordPlanScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("แผนที่วางไว้", "ทริปที่เสร็จแล้ว", "ฉบับร่าง")

    val mockTrips = listOf(
        TripRecord("1", "แอ่วเหนือหน้าหนาว", "Chiang Mai", "10 ธ.ค.", "15 ธ.ค.", "Upcoming", 15000.0, 2000.0, "", emptyList(), null),
        TripRecord("2", "ทะเลใต้ชิลล์ๆ", "Phuket", "1 พ.ค.", "5 พ.ค.", "Completed", 25000.0, 24500.0, "", listOf("Pic1", "Pic2"), null)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("ai_generator") }) {
                Icon(Icons.Filled.Add, contentDescription = "New Plan")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            SearchFilterBar()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val filteredTrips = when (selectedTabIndex) {
                    0 -> mockTrips.filter { it.status == "Upcoming" }
                    1 -> mockTrips.filter { it.status == "Completed" }
                    else -> emptyList() // Drafts
                }
                
                items(filteredTrips) { trip ->
                    TripHistoryCard(trip = trip)
                }
            }
        }
    }
}
