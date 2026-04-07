package com.example.a519lablearnandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityOptionsCompat
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

// ─────────────────────────────────────────────────────────────────────────────
// Primary Activity (Sender)
// ─────────────────────────────────────────────────────────────────────────────

class Part7Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current as Activity

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Activity A",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // 1. Create the Intent and attach a String extra
                val intent = Intent(context, Part7DetailActivity::class.java).apply {
                    putExtra("EXTRA_MESSAGE", "Hello from Activity A! 👋")
                }

                // 2. Define the Custom Animation (Slide In Up for new, Stay for old)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                    context,
                    R.anim.slide_in_up,
                    R.anim.stay
                )

                // 3. Launch Activity with options
                context.startActivity(intent, options.toBundle())
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Open Detail (Slide Up)", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Secondary Activity (Receiver)
// ─────────────────────────────────────────────────────────────────────────────

class Part7DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Read the string extra passed from Activity A
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "No message received"

        setContent {
            _519LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DetailScreen(
                        message = message,
                        modifier = Modifier.padding(innerPadding),
                        onClose = {
                            finish()
                            // Override the exit animation when finish() is called
                            // Old Activity (A) stays still, New Activity (Detail) slides down
                            overridePendingTransition(R.anim.stay, R.anim.slide_out_down)
                        }
                    )
                }
            }
        }
    }

    // Also handle the system back button so it animates nicely
    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher.onBackPressed()"))
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down)
    }
}

@Composable
fun DetailScreen(
    message: String,
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0E7FF)), // Light Indigo background to differentiate
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Activity B (Detail)",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3730A3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display the received message
        Text(
            text = message,
            fontSize = 18.sp,
            color = Color(0xFF4F46E5)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onClose,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)) // Red Close button
        ) {
            Text(text = "Close (Slide Down)", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
        }
    }
}