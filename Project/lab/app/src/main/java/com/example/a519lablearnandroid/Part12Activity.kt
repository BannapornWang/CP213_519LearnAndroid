package com.example.a519lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme
import kotlinx.coroutines.launch

class Part12Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Part12Screen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part12Screen(onBackClick: () -> Unit = {}) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mission 12: Sheet & Dialog", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(text = "Interactive Demonstrations", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ทดลองกดปุ่มด้านล่างเพื่อดูการทำงานของ UI", fontSize = 14.sp)
                }
            }

            // ─── Control Buttons ───
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showBottomSheet = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Show Bottom Sheet")
                    }
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Show Alert Dialog")
                    }
                }
            }

            // ─── Concept Cards ───
            item { BottomSheetConceptCard() }
            item { DialogConceptCard() }
        }

        // ────── MODAL BOTTOM SHEET ──────
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                // Sheet Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp, start = 24.dp, end = 24.dp, top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Modal Bottom Sheet ✨", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sheet นี้สามารถเลื่อนขึ้นลงได้ " +
                                "และจะปิดตัวลงเมื่อลากลงสุดหรือกดที่พื้นที่ด้านนอก",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }) {
                        Text("ปิด Sheet")
                    }
                }
            }
        }

        // ────── ALERT DIALOG ──────
        if (showDialog) {
            AlertDialog(
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                title = { Text(text = "Middle Dialog (Alert)") },
                text = {
                    Text(
                        text = "นี่คือ Alert Dialog ที่ปรากฏขึ้นในตำแหน่งกึ่งกลางหน้าจอ " +
                                "ใช้สำหรับการแจ้งเตือนที่สำคัญหรือต้องการการยืนยันจากผู้ใช้"
                    )
                },
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("ตกลง")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("ยกเลิก")
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Concept Cards
// ─────────────────────────────────────────────
@Composable
fun BottomSheetConceptCard() {
    ConceptCard(
        title = "📄 Modal Bottom Sheet",
        description = "คือ UI ที่เลื่อนขึ้นมาจากขอบด้านล่างของหน้าจอ\n\n" +
                "✅ ใช้เมื่อ: ต้องการแสดงตัวเลือกเพิ่มเติม, ตัวกรอง (Filter), หรือแสดงข้อมูลย่อยโดยไม่อยากเปลี่ยนหน้าใหม่\n" +
                "✅ ข้อดี: เข้าถึงได้ง่ายด้วยนิ้วแม่มือ (Thumb-friendly design)",
        icon = Icons.Default.Info,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
fun DialogConceptCard() {
    ConceptCard(
        title = "🖼️ Middle Dialog (Alert)",
        description = "คือ Modal ที่ปรากฏขึ้นมาทับหน้าหลักตรงกึ่งกลางหน้าจอ\n\n" +
                "✅ ใช้เมื่อ: ต้องการให้ความสำคัญสูงสุด, การยืนยันการลบข้อมูล, หรือแจ้งเตือนความผิดพลาดร้ายแรง\n" +
                "✅ ข้อสังเกต: รบกวนการทำงานของผู้ใช้มากกว่า Bottom Sheet จึงควรใช้เมื่อจำเป็นจริงๆ",
        icon = Icons.Default.Notifications,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    )
}

@Composable
private fun ConceptCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part12Preview() {
    _519LabLearnAndroidTheme {
        Part12Screen()
    }
}