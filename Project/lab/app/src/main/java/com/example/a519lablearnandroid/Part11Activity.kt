package com.example.a519lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme
import kotlinx.coroutines.delay

class Part11Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Part11Screen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part11Screen(onBackClick: () -> Unit = {}) {
    var isLoading by remember { mutableStateOf(true) }

    // จำลองการโหลดข้อมูลเมื่อเปิดหน้าจอ
    LaunchedEffect(Unit) {
        delay(2000) // รอ 2 วินาที
        isLoading = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mission 11: Skeleton Loading", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isLoading = true }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
            item { SkeletonConceptCard() }

            if (isLoading) {
                items(5) { SkeletonItem() }
            } else {
                items(actualContent) { content ->
                    ActualContentItem(content)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Concept Card
// ─────────────────────────────────────────────
@Composable
fun SkeletonConceptCard() {
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
                Text(text = "Skeleton Loading คืออะไร?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "การทำ Skeleton Loading (หรือ Shimmer Effect) คือการแสดง 'โครงร่าง' ของหน้าตาทั้งหมด " +
                        "ในขณะที่ข้อมูลจริงยังมาไม่ถึง เพื่อให้ผู้ใช้เห็นว่า 'กำลังจะมีอะไรเกิดขึ้น' " +
                        "และลดความรู้สึกหงุดหงิดจากการเห็นหน้าจอเปล่าๆ หรือ ProgressBar แบบหมุนวนทั่วไป",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Skeleton Item (Shimmer Effect)
// ─────────────────────────────────────────────
@Composable
fun SkeletonItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Profile Image Skeleton
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.LightGray, shape = CircleShape)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                // Title Skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Subtitle Skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(14.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Shimmer Effect Modifier
// ─────────────────────────────────────────────
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    background(brush = brush)
}

// ─────────────────────────────────────────────
//  Actual Content
// ─────────────────────────────────────────────
data class ContentData(val title: String, val author: String)
val actualContent = listOf(
    ContentData("เรียนรู้ Jetpack Compose", "By Aj. Peter"),
    ContentData("Android Navigation ขั้นสูง", "By Dev Team"),
    ContentData("การจัดการ State ใน ViewModel", "By Google Developers"),
    ContentData("Modern UI Design Patterns", "By UI Guru"),
    ContentData("Kotlin Coroutines Workflow", "By Kotlin Master")
)

@Composable
fun ActualContentItem(data: ContentData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = data.author.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = data.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = data.author, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part11Preview() {
    _519LabLearnAndroidTheme {
        Part11Screen()
    }
}