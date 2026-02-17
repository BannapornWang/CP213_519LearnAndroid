package com.example.a519lablearnandroid
import android.content.Intent // Added
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Added (Fixes your specific error)
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton // Added
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

// Check if this path is correct based on your folder structure

class RPGCardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _519LabLearnAndroidTheme { // Recommended to wrap in theme
                RPGCardview()
            }
        }
    }

    @Composable
    fun RPGCardview() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(32.dp)
        ) {
            // hp bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(color = Color.Gray)
            ) {
                Text(
                    text = "HP",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterStart)
                        .fillMaxWidth(fraction = 0.76f)
                        .background(color = Color.Red)
                        .padding(5.dp)
                )
            }

            // image
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "profile",
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
                    .clickable { // Now resolved
                        startActivity(Intent(this@RPGCardActivity, PokedexActivity::class.java))
                    }
            )

            var str by remember { mutableIntStateOf(8) }
            var agi by remember { mutableIntStateOf(24) }
            var int by remember { mutableIntStateOf(23) }

            // status
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Str Column
                StatControl("Str", str) { str += it }
                // Agi Column
                StatControl("Agi", agi) { agi += it }
                // Int Column
                StatControl("Int", int) { int += it }
            }
        }
    }

    // Helper to keep code clean and fix the syntax error in your Int column
    @Composable
    fun StatControl(label: String, value: Int, onValueChange: (Int) -> Unit) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { onValueChange(1) }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_drop_up_24),
                    contentDescription = "up",
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(text = label, fontSize = 32.sp)
            Text(text = value.toString(), fontSize = 32.sp)
            IconButton(onClick = { onValueChange(-1) }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                    contentDescription = "down",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewScreen() {
        RPGCardview()
    }
}