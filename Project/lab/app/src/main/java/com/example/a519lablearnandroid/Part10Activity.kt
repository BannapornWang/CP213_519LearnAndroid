package com.example.a519lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

// ─────────────────────────────────────────────────────────────────────────────
// Activity
// ─────────────────────────────────────────────────────────────────────────────

class Part10Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Part10Screen(onBackClick = { finish() })
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part10Screen(onBackClick: () -> Unit = {}) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mission 10: App Widget", fontWeight = FontWeight.Bold) },
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
            item { AppWidgetConceptCard() }
            item { GlanceConceptCard() }
            item { GlanceArchitectureCard() }
            item { WidgetPreviewSection() }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Concept Cards
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AppWidgetConceptCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "App Widget คืออะไร?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
📌 App Widget (หรือที่รู้จักกันว่า Home Screen Widget) คือ UI ขนาดเล็กที่ฝังอยู่บน Home Screen ของ Android โดยตรง
ผู้ใช้ไม่ต้องเปิดแอปก็สามารถดูข้อมูลสำคัญ หรือทำ action ง่ายๆ ได้ทันที

📱 ตัวอย่าง App Widget ที่คุ้นเคย:
• Clock Widget — แสดงเวลาและวันที่
• Weather Widget — แสดงสภาพอากาศปัจจุบัน
• Music Player Widget — ควบคุมเพลงได้โดยตรง
• Calendar Widget — แสดงกำหนดการวันนี้

⚠️ ข้อแตกต่างจาก Notification:
Widget แสดงข้อมูล "เรียลไทม์" บน Home Screen
Notification คือการแจ้งเตือนที่ผู้ใช้ต้องดำเนินการ
                """.trimIndent(),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun GlanceConceptCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "✨ Jetpack Glance คืออะไร?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
Jetpack Glance คือ library ที่ทำให้เราสร้าง App Widget ด้วย Jetpack Compose API ได้ แทนที่จะต้องเขียน XML RemoteViews แบบเดิม

🔄 เปรียบเทียบ:
""",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
            ComparisonTable()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
📦 Dependency ที่ต้องเพิ่ม:
implementation("androidx.glance:glance-appwidget:1.1.0")
implementation("androidx.glance:glance-material3:1.1.0")
                """.trimIndent(),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun ComparisonTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("แบบเก่า (RemoteViews)", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text("Jetpack Glance", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(6.dp))
        ComparisonRow("เขียนด้วย XML layout", "เขียนด้วย Compose (@Composable)")
        ComparisonRow("API ซับซ้อน", "API คล้าย Compose ที่คุ้นเคย")
        ComparisonRow("ยากต่อการ Preview", "รองรับ Preview บางส่วน")
        ComparisonRow("จำกัด View types", "ยืดหยุ่นมากขึ้น")
    }
}

@Composable
fun ComparisonRow(old: String, new: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text("❌ $old", fontSize = 11.sp, modifier = Modifier.weight(1f), lineHeight = 16.sp, color = Color(0xFF9E9E9E))
        Text("✅ $new", fontSize = 11.sp, modifier = Modifier.weight(1f), lineHeight = 16.sp, color = Color(0xFF388E3C))
    }
}

@Composable
fun GlanceArchitectureCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🏗️ โครงสร้างของ Glance Widget",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            val steps = listOf(
                "GlanceAppWidget" to "Class หลัก — override fun provideGlance() เพื่อ render UI",
                "GlanceAppWidgetReceiver" to "BroadcastReceiver ที่รับ Intent จาก Android OS เพื่ออัปเดต Widget",
                "provideContent { }" to "ฟังก์ชันหลักภายใน GlanceAppWidget สำหรับ render Composable",
                "LocalContext.current" to "ใน Glance ใช้ LocalContext จาก androidx.glance แทน Compose",
                "appWidgetManager.updateAppWidget" to "อัปเดต Widget เมื่อข้อมูลเปลี่ยน",
                "AppWidgetProviderInfo (XML)" to "ไฟล์ XML ที่กำหนดขนาด, update interval ของ Widget"
            )

            steps.forEachIndexed { i, (title, desc) ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${i + 1}",
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Widget Preview Simulation (จำลองหน้าตา Widget บน Home Screen)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WidgetPreviewSection() {
    Column {
        Text(
            text = "📲 ตัวอย่าง Widget ที่อาจสร้างได้",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Wallpaper simulation background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF1A237E),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🏠  Home Screen Simulation",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )

                // Widget 1: Clock Widget
                ClockWidgetPreview()

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Widget 2: Weather
                    WeatherWidgetPreview(modifier = Modifier.weight(1f))
                    // Widget 3: Quick Actions
                    QuickActionWidgetPreview(modifier = Modifier.weight(1f))
                }

                // Widget 4: Email count
                EmailWidgetPreview()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "💡 Widget เหล่านี้สร้างได้ด้วย Jetpack Glance โดยเขียน Composable function ใน GlanceAppWidget.provideContent { }",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            lineHeight = 18.sp
        )
    }
}

@Composable
fun ClockWidgetPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("10:45", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Light)
                Text("จันทร์, 7 เมษายน 2026", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun WeatherWidgetPreview(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF0288D1).copy(alpha = 0.85f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌤️", fontSize = 28.sp)
            Text("28°C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Bangkok", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        }
    }
}

@Composable
fun QuickActionWidgetPreview(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Quick Actions", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WidgetIconButton(Icons.Default.PlayArrow, "Play")
                WidgetIconButton(Icons.Default.Refresh, "Sync")
                WidgetIconButton(Icons.Default.Home, "Home")
            }
        }
    }
}

@Composable
fun WidgetIconButton(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp)
    }
}

@Composable
fun EmailWidgetPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50).copy(alpha = 0.85f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("อีเมล", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("3 ข้อความใหม่", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("3", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun Part10Preview() {
    _519LabLearnAndroidTheme {
        Part10Screen()
    }
}
