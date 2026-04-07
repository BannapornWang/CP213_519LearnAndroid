package com.example.a519lablearnandroid

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

// ─────────────────────────────────────────────────────────────────────────────
// Activity
// ─────────────────────────────────────────────────────────────────────────────

class Part8Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdaptiveProfileScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen with BoxWithConstraints
// ─────────────────────────────────────────────────────────────────────────────

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AdaptiveProfileScreen(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
    ) {
        // Here we read the available width of the current layout boundary.
        // Usually, phones in portrait are around ~360-400dp width.
        // Tablets or phones in landscape will have > 600dp width.
        if (maxWidth < 600.dp) {
            // Mobile (Portrait) Layout -> Vertical Stack
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfilePicture(modifier = Modifier.size(160.dp))
                Spacer(modifier = Modifier.height(32.dp))
                ProfileInfo(modifier = Modifier.fillMaxWidth())
            }
        } else {
            // Tablet / Landscape Layout -> Horizontal Layout (Master-Detail style)
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Picture stays on the left
                ProfilePicture(modifier = Modifier.size(200.dp))
                Spacer(modifier = Modifier.width(48.dp))
                // Info fills the remaining space on the right via weight
                ProfileInfo(modifier = Modifier.weight(1f))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// UI Components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ProfilePicture(modifier: Modifier = Modifier) {
    // A placeholder gray box for the profile picture
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile Picture",
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
fun ProfileInfo(modifier: Modifier = Modifier) {
    // Mock user information section
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text(
            text = "John Doe",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Text(
            text = "Senior Android Developer",
            fontSize = 18.sp,
            color = Color(0xFF757575),
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Dummy info lines
        InfoRow(label = "Email", value = "john.doe@example.com")
        InfoRow(label = "Location", value = "Bangkok, Thailand")
        InfoRow(label = "Phone", value = "+66 81 234 5678")
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF9E9E9E), fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 16.sp, color = Color(0xFF424242), modifier = Modifier.padding(top = 4.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews (Testing both Mobile & Tablet views)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Mobile - Portrait")
@Composable
fun MobilePreview() {
    _519LabLearnAndroidTheme {
        AdaptiveProfileScreen()
    }
}

@Preview(
    showBackground = true,
    name = "Tablet / Landscape",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:width=800dp,height=480dp,dpi=240,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun TabletPreview() {
    _519LabLearnAndroidTheme {
        AdaptiveProfileScreen()
    }
}