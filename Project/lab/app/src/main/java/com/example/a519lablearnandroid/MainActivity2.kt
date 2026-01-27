package com.example.a519lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.DefaultTab.AlbumsTab.value
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android2",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") } //mutable...("")
    Column {
            Text(
            text = "Hello $name! say ="+inputText,
            modifier = modifier
        )
            Text(
                text = "Good bye $name!",
                modifier = modifier
            )
//            TextField(
//                value = inputText,
//                onValueChange = {
//                    inputText = it
//                }
//            )
        TextField(
            value = inputText,
            onValueChange = {
                inputText = it
            }
        )
        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    _519LabLearnAndroidTheme {
        Greeting2("Android")
    }
}