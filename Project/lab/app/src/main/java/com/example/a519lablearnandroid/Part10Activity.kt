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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part10Screen(onBackClick: () -> Unit = {}) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MediumTopAppBar(
                title = { Text("Mission 10: App Widget", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
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
            item { WidgetIntroductionCard() }
            item { WidgetCoreComponentsCard() }
            item { WidgetLifecycleCard() }
            item { GlanceIntroductionCard() }
            item { WidgetConfigurationCard() }
        }
    }
}

@Composable
fun WidgetIntroductionCard() {
    ConceptCard(
        title = "📱 App Widget คืออะไร?",
        description = "App Widgets คือ 'mini-view' ของแอปพลิเคชันที่สามารถฝัง (embedded) อยู่ในหน้า Home Screen เพื่อให้ผู้ใช้อัปเดตข้อมูลหรือโต้ตอบสั้นๆ ได้ทันทีโดยไม่ต้องเปิดแอปเต็มรูปแบบ",
        icon = Icons.Default.Info,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
fun WidgetCoreComponentsCard() {
    ConceptCard(
        title = "🏗️ ส่วนประกอบสำคัญ",
        description = "• AppWidgetProvider: คลาสที่จัดการ Lifecycle ของ Widget (BroadcastReceiver)\n" +
                "• RemoteViews: คลาสที่ใช้อธิบาย UI ที่จะไปแสดงผลใน process อื่น (Home Screen)\n" +
                "• XML Metadata: ไฟล์ XML ที่กำหนดคุณสมบัติ เช่น ขนาดเริ่มต้น, ชื่อเรียก, และเวลาอัปเดต",
        icon = Icons.Default.Build,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    )
}

@Composable
fun WidgetLifecycleCard() {
    ConceptCard(
        title = "🔄 Lifecycle ของ Widget",
        description = "• onUpdate(): เรียกเมื่อถึงเวลาอัปเดตที่กำหนดใน XML หรือ manual update\n" +
                "• onEnabled(): เรียกเมื่อ Widget ตัวแรกถูกเพิ่มลากวางที่หน้าจอ\n" +
                "• onDisabled(): เรียกเมื่อ Widget ตัวสุดท้ายถูกลบออก\n" +
                "• onDeleted(): เรียกทุกครั้งที่ Widget ถูกลบออกทีละตัว",
        icon = Icons.Default.Refresh,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    )
}

@Composable
fun GlanceIntroductionCard() {
    ConceptCard(
        title = "✨ Jetpack Glance",
        description = "เฟรมเวิร์กสมัยใหม่จาก Google ที่ช่วยให้เขียน App Widgets โดยใช้ syntax คล้ายกับ Jetpack Compose แทนการใช้ XML/RemoteViews แบบดั้งเดิม ทำให้เขียนง่ายและจัดการสถานะได้ดีขึ้น",
        icon = Icons.Default.Settings,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
fun WidgetConfigurationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "🛠️ การตั้งค่าใน XML (appwidget-provider)", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "minWidth / minHeight: ขนาดเริ่มต้น\n" +
                        "updatePeriodMillis: ความถี่ในการอัปเดต (ขั้นต่ำ 30 นาทีเพื่อประหยัดแบตเตอรี่)\n" +
                        "initialLayout: ไฟล์ Layout เบื้องต้น",
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ConceptCard(
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part10Preview() {
    _519LabLearnAndroidTheme {
        Part10Screen()
    }
}