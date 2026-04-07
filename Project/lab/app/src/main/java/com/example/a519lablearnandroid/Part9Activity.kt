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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

// ─────────────────────────────────────────────────────────────────────────────
// Activity
// ─────────────────────────────────────────────────────────────────────────────

class Part9Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Part9Screen(onBackClick = { finish() })
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part9Screen(onBackClick: () -> Unit = {}) {
    // scrollBehavior จะเป็นตัวเชื่อมระหว่าง LazyColumn กับ TopAppBar
    // เมื่อ scroll ลง → TopAppBar จะ "ยุบ" (collapse)
    // เมื่อ scroll ขึ้น → TopAppBar จะ "ขยาย" (expand) กลับมา
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        // nestedScrollConnection ต้องถูกส่งจาก Scaffold ขึ้นไปหา TopAppBar
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Mission 9: Collapsing Toolbar",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorite")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                // ส่ง scrollBehavior เข้า LargeTopAppBar เพื่อให้รับรู้ scroll state
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
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
            // Concept Card
            item { CollapsingConceptCard() }

            // Scroll Behavior Types Card
            item { ScrollBehaviorTypesCard() }

            // Key APIs Card
            item { KeyApisCard() }

            // Demo content to allow scrolling
            items(12) { index ->
                DemoArticleItem(index = index + 1)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Concept Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CollapsingConceptCard() {
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
                    text = "Collapsing Toolbar คืออะไร?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
📌 Collapsing Toolbar (หรือ Collapsing AppBar) คือ TopAppBar ที่สามารถ "ยุบตัว" ได้เมื่อผู้ใช้ scroll หน้าจอลง และ "ขยาย" กลับเมื่อ scroll ขึ้น

🎯 ทำไมต้องใช้?
• เพิ่มพื้นที่แสดงเนื้อหาเมื่อต้องการอ่าน
• ให้ความรู้สึก dynamic และ modern มากขึ้น
• ใช้พื้นที่หน้าจอได้อย่างมีประสิทธิภาพ

⚙️ หลักการทำงาน:
ใช้ Nested Scroll ซึ่งเป็นระบบที่ Child (LazyColumn) ส่ง scroll event ขึ้นไปหา Parent (TopAppBar) เพื่อให้ TopAppBar ปรับขนาดตาม
                """.trimIndent(),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Scroll Behavior Types Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ScrollBehaviorTypesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🔄 ประเภทของ ScrollBehavior",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            ScrollBehaviorItem(
                name = "exitUntilCollapsedScrollBehavior",
                desc = "TopAppBar จะยุบและไม่แสดงอีกเลยจนกว่าจะ scroll กลับขึ้นมาจนสุด (ใช้กับ LargeTopAppBar / MediumTopAppBar)"
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScrollBehaviorItem(
                name = "enterAlwaysScrollBehavior",
                desc = "TopAppBar จะกลับมาทันทีเมื่อเริ่ม scroll ขึ้น แม้ยังไม่ถึงด้านบน"
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScrollBehaviorItem(
                name = "pinnedScrollBehavior",
                desc = "TopAppBar คงตำแหน่งไว้ แต่เปลี่ยนสีตาม scroll state (ไม่ยุบ)"
            )
        }
    }
}

@Composable
fun ScrollBehaviorItem(name: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = desc, fontSize = 13.sp, lineHeight = 20.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Key APIs Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun KeyApisCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🔑 Key APIs ที่ต้องรู้",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            val apis = listOf(
                "LargeTopAppBar" to "TopAppBar ขนาดใหญ่ที่ collapse ได้ (title อยู่ด้านล่าง)",
                "MediumTopAppBar" to "ขนาดกลาง รองรับ collapse เช่นกัน",
                "rememberTopAppBarState()" to "เก็บ state ของ TopAppBar (offset, contentOffset)",
                ".nestedScroll(...)" to "Modifier ที่ส่ง scroll event จาก LazyColumn ไปยัง TopAppBar",
                "scrollBehavior.nestedScrollConnection" to "สะพานเชื่อม scroll event กับ behavior"
            )
            apis.forEach { (api, desc) ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "• ",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Column {
                        Text(text = api, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Demo Article Item (for scrollable content)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DemoArticleItem(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "บทความที่ $index",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = "ลอง scroll ขึ้น-ลง เพื่อดู Collapsing Toolbar ทำงาน",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun Part9Preview() {
    _519LabLearnAndroidTheme {
        Part9Screen()
    }
}
