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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

class Part9Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Part9Screen()
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Main Screen
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part9Screen() {

    /*
     * 📌 CONCEPT: Collapsing Toolbar (TopAppBarScrollBehavior)
     *
     * Collapsing Toolbar คือ Toolbar/AppBar ที่สามารถ
     * "ยุบ" (collapse) หรือ "ขยาย" (expand) ได้
     * ตามการ scroll ของ content ด้านล่าง
     *
     * ใน Jetpack Compose (Material 3) ทำได้ผ่าน:
     *   - LargeTopAppBar / MediumTopAppBar
     *   - TopAppBarScrollBehavior  ← ตัวควบคุมพฤติกรรม
     *   - nestedScrollConnection   ← เชื่อม scroll ของ list
     *                                 เข้ากับ AppBar
     *
     * มี 3 แบบหลัก:
     *   1. exitUntilCollapsedScrollBehavior → ยุบจนสุด แล้วค้างไว้
     *   2. enterAlwaysScrollBehavior        → ยุบเมื่อ scroll ลง
     *                                         ขยายทันทีเมื่อ scroll ขึ้น
     *   3. pinnedScrollBehavior             → ไม่ยุบ (ค้างอยู่เสมอ)
     */

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        // ✅ nestedScrollConnection เชื่อม scroll event
        //    ของ LazyColumn → TopAppBar
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CollapsingHeader(scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Concept Cards ──────────────────────────────
            item { ConceptCard() }
            item { BehaviorTypesCard() }
            item { HowItWorksCard() }

            // ── Demo content (เพื่อให้มี scroll ทดสอบ) ────
            items(demoItems) { item ->
                DemoItemCard(item)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Collapsing Header (LargeTopAppBar)
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingHeader(scrollBehavior: TopAppBarScrollBehavior) {

    /*
     * LargeTopAppBar มี 2 สถานะ:
     *   • Expanded  → title อยู่ด้านล่าง (ใหญ่)
     *   • Collapsed → title ย้ายขึ้นบน (เล็ก) เหมือน TopAppBar ปกติ
     *
     * scrollBehavior.state.collapsedFraction
     *   → ค่า 0f = ขยายสุด, 1f = ยุบสุด
     *   → ใช้ค่านี้ interpolate สี / ขนาด ได้
     */

    val collapsedFraction = scrollBehavior.state.collapsedFraction

    // Interpolate สีพื้นหลังตาม scroll
    val containerColor = lerp(
        start = MaterialTheme.colorScheme.primaryContainer,
        stop  = MaterialTheme.colorScheme.surface,
        fraction = collapsedFraction
    )

    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = "Mission 9: Collapsing",
                    fontWeight = FontWeight.Bold
                )
                // subtitle จะค่อยๆ หายไปตอน collapse
                if (collapsedFraction < 0.5f) {
                    Text(
                        text = "Scroll ลงเพื่อดู Collapsing Effect ✨",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                            .copy(alpha = 1f - collapsedFraction * 2)
                    )
                }
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorite")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor         = containerColor,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehavior
    )
}

// ─────────────────────────────────────────────
//  Concept Cards
// ─────────────────────────────────────────────
@Composable
fun ConceptCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📚 Collapsing Toolbar คืออะไร?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                    Collapsing Toolbar คือ UI Pattern ที่ทำให้
                    Header / AppBar มีพฤติกรรม "ยืดหยุ่น" ตามการ scroll
                    
                    ✅ ขยาย (Expand) → แสดงข้อมูลเพิ่มเติม เช่น รูปภาพ, คำอธิบาย
                    ✅ ยุบ (Collapse) → เหลือแค่ชื่อ + ปุ่ม action พื้นฐาน
                    
                    ประโยชน์:
                    • ประหยัดพื้นที่หน้าจอขณะ scroll อ่าน content
                    • UX ดีขึ้น เพราะ content มีพื้นที่แสดงมากขึ้น
                    • นิยมใช้ใน Profile Page, Detail Page, Article Page
                """.trimIndent(),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun BehaviorTypesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚙️ ScrollBehavior 3 แบบ",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            BehaviorItem(
                emoji  = "1️⃣",
                name   = "exitUntilCollapsed",
                desc   = "ยุบเมื่อ scroll ลง แต่จะไม่ขยายกลับ\nจนกว่าจะ scroll ขึ้นถึงบนสุด\n→ ใช้ในหน้านี้ ✅"
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            BehaviorItem(
                emoji  = "2️⃣",
                name   = "enterAlways",
                desc   = "ยุบเมื่อ scroll ลง และขยายทันที\nเมื่อ scroll ขึ้นแม้จะไม่ถึงบนสุด"
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            BehaviorItem(
                emoji  = "3️⃣",
                name   = "pinned",
                desc   = "ไม่ยุบ ค้างอยู่ตลอด\n(ใช้เมื่อต้องการ TopAppBar แบบคงที่)"
            )
        }
    }
}

@Composable
fun BehaviorItem(emoji: String, name: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = desc, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
fun HowItWorksCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🔧 วิธีการทำงานเบื้องหลัง",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                    ขั้นตอนการ Implement:
                    
                    1. สร้าง ScrollBehavior
                       val scrollBehavior = TopAppBarDefaults
                           .exitUntilCollapsedScrollBehavior()
                    
                    2. เชื่อม Scaffold กับ nestedScroll
                       Modifier.nestedScroll(
                           scrollBehavior.nestedScrollConnection
                       )
                    
                    3. ส่ง scrollBehavior ให้ LargeTopAppBar
                       LargeTopAppBar(
                           scrollBehavior = scrollBehavior
                       )
                    
                    4. (Optional) ใช้ collapsedFraction
                       เพื่อ interpolate animation เอง
                       val fraction = scrollBehavior
                           .state.collapsedFraction
                       // 0f = expanded, 1f = collapsed
                """.trimIndent(),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 20.sp
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Demo Items
// ─────────────────────────────────────────────
data class DemoItem(val title: String, val body: String)

val demoItems = List(10) { i ->
    DemoItem(
        title = "Demo Item #${i + 1}",
        body  = "Scroll เพื่อทดสอบการ Collapse/Expand ของ Header ด้านบน " +
                "สังเกตว่า LargeTopAppBar จะค่อยๆ ยุบตัวลงเมื่อ scroll ลง"
    )
}

@Composable
fun DemoItemCard(item: DemoItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.body, fontSize = 13.sp)
        }
    }
}

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun Part9Preview() {
    _519LabLearnAndroidTheme {
        Part9Screen()
    }
}