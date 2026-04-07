package com.example.a519lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

class Part1AnimationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LikeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LikeScreen(modifier: Modifier = Modifier) {
    // --- State ---
    var isLiked by remember { mutableStateOf(false) }

    // 1. Scale animation: ขยายขึ้นเล็กน้อยเมื่อกด แล้วกลับมาด้วย spring
    val scale by animateFloatAsState(
        targetValue = if (isLiked) 1.12f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "likeScale"
    )

    // 2. Color animation: เทา → ชมพู
    val buttonColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFE91E8C) else Color(0xFF9E9E9E),
        animationSpec = tween(durationMillis = 300),
        label = "likeColor"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3. Scale applied via graphicsLayer
        Box(modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)) {
            Button(
                onClick = { isLiked = !isLiked },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 3. AnimatedVisibility: Heart icon โผล่เมื่อ isLiked = true
                    AnimatedVisibility(
                        visible = isLiked,
                        enter = scaleIn() + fadeIn()
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Heart",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                    Text(
                        text = if (isLiked) "Liked!" else "Like",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LikeScreenPreview() {
    _519LabLearnAndroidTheme {
        LikeScreen()
    }
}