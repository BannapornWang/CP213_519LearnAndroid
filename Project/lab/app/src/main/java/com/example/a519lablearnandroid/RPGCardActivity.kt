package com.example.a519lablearnandroid


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class RPGCardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Lifecycle", "onCreate")

        setContent {
            RPGCardView()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("Lifecycle", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i("Lifecycle", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i("Lifecycle", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i("Lifecycle", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Lifecycle", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("Lifecycle", "onRestart")
    }
}

@Composable
fun RPGCardView() {

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    var str by remember { mutableIntStateOf(8) }
    var agi by remember { mutableIntStateOf(24) }
    var intStat by remember { mutableIntStateOf(23) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(32.dp)
    ) {

        // 🔴 HP BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color.White)
        ) {
            Text(
                text = "HP",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.76f)
                    .background(Color.Red)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "profile",
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    if (!isPreview) {
                        context.startActivity(
                            Intent(context, PokedexActivity::class.java)
                        )
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatControl("Str", str) { str += it }
            StatControl("Agi", agi) { agi += it }
            StatControl("Int", intStat) { intStat += it }
        }
    }
}

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

        Text(text = label, fontSize = 24.sp)
        Text(text = value.toString(), fontSize = 24.sp)

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
    RPGCardView()
}