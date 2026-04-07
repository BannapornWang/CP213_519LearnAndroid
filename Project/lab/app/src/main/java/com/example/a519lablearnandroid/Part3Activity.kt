package com.example.a519lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

class Part3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DonutChartScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DonutChartScreen(modifier: Modifier = Modifier) {
    val slices = listOf(30f, 40f, 30f)
    val colors = listOf(
        Color(0xFF6C63FF),   // Purple
        Color(0xFF43E97B),   // Green
        Color(0xFFFA709A),   // Pink
    )
    val labels = listOf("Purple 30%", "Green 40%", "Pink 30%")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Donut Chart",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        DonutChart(
            values = slices,
            colors = colors,
            modifier = Modifier.size(260.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Legend
        labels.forEachIndexed { i, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(colors[i], shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = label, color = Color.White.copy(alpha = 0.85f), fontSize = 15.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DonutChart Composable
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A Donut Chart drawn purely with [Canvas] — no external charting library.
 *
 * @param values   List of proportional percentages (they should sum to 100).
 * @param colors   List of colors matching each slice in [values].
 * @param strokeWidth  Width of the ring (donut thickness) in dp.
 * @param animationDurationMs  Duration of the sweep-angle entry animation.
 */
@Composable
fun DonutChart(
    values: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 60f,
    animationDurationMs: Int = 1200
) {
    require(values.size == colors.size) { "values and colors must have the same size" }

    // Animate from 0 → 360 on first composition
    val animatedSweep = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedSweep.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = animationDurationMs)
        )
    }

    val total = values.sum().takeIf { it > 0f } ?: 1f
    val sweepAngles = values.map { (it / total) * 360f }

    // Read state value here (in composable scope) so Canvas redraws on each animation frame
    val progress = animatedSweep.value / 360f

    Canvas(modifier = modifier) {
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f
        )
        val arcSize = Size(diameter, diameter)
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Butt)

        var startAngle = -90f   // Start at the top (12 o'clock)
        sweepAngles.forEachIndexed { index, fullSweep ->

            // How far through the total animation does this slice finish?
            val sliceStart = sweepAngles.take(index).sum() / 360f
            val sliceEnd = sliceStart + fullSweep / 360f

            val drawnSweep = when {
                progress <= sliceStart -> 0f
                progress >= sliceEnd   -> fullSweep
                else -> (progress - sliceStart) / (fullSweep / 360f) * fullSweep
            }

            if (drawnSweep > 0f) {
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = drawnSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
            }
            startAngle += fullSweep
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
fun DonutChartPreview() {
    _519LabLearnAndroidTheme {
        DonutChartScreen()
    }
}