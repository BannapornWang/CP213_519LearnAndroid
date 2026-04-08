package com.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tripmate.data.models.BookingInfo
import com.tripmate.data.models.Expense
import com.tripmate.data.models.ExpenseCategory
import com.tripmate.ui.components.*

@Composable
fun TripDetailScreen(navController: NavController, tripId: String?) {
    var showExpenseDialog by remember { mutableStateOf(false) }

    val mockBooking = BookingInfo("เที่ยวบินไป", "Thai Airways", "ABCDEF", "BKK - CNX", "10 เม.ย. 08:00")
    val mockExpense = Expense("1", "10 เม.ย.", "ค่ากาแฟ", 150.0, ExpenseCategory.FOOD, null)

    if (showExpenseDialog) {
        ExpenseAddDialog(onDismiss = { showExpenseDialog = false })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showExpenseDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Header Section
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray)) {
                    Text("Cover Image Swipeable Gallery", modifier = Modifier.align(androidx.compose.ui.Alignment.Center), color = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("แอ่วเหนือหน้าหนาว (Trip $tripId)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("เชียงใหม่ | 10 - 15 เม.ย.", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                }
            }

            // 2. Booking Information
            item {
                Text("ข้อมูลการจอง", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                BookingInfoCard(booking = mockBooking)
                BookingInfoCard(booking = mockBooking.copy(type = "ที่พัก", provider = "Chiang Mai Hotel", confirmationCode = "H12345"))
            }

            // 3. Daily Itinerary Section
            item {
                Text("แผนการเดินทางรายวัน", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TimelineView(activities = listOf(
                    TimelineActivity(time = "08:00", title = "เดินทางถึงสนามบิน", subtitle = "CNX Airport", isCompleted = true),
                    TimelineActivity(time = "09:30", title = "เช่ารถ", subtitle = "Avis", isCompleted = true),
                    TimelineActivity(time = "10:30", title = "คาเฟ่ฮอปปิ้ง", subtitle = "นิมมาน", isCompleted = false)
                ))
            }

            // 4. Budget Tracker Section
            item {
                Text("บันทึกค่าใช้จ่าย", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                BudgetProgressBar(spent = 2000.0, budget = 15000.0)
                CategoryPieChart()
                ExpenseItem(expense = mockExpense)
                ExpenseItem(expense = mockExpense.copy(description = "ค่ารถแดง", amount = 50.0, category = ExpenseCategory.TRANSPORT))
            }

            // 5. Photos & Memories Section
            item {
                Text("ภาพถ่าย/ความทรงจำ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                PhotoGalleryGrid(images = emptyList())
            }

            // 6. Quick Actions
            item {
                Text("จัดการทริป", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    OutlinedButton(onClick = {}) { Text("แก้ไขแผน") }
                    OutlinedButton(onClick = {}) { Text("Export PDF") }
                }
            }
        }
    }
}
