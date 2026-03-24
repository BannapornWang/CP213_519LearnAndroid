package com.example.a519lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a519lablearnandroid.Util.SharedPreferencesUtil
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme


class SharedPreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPreferencesUtil.init(this)

        // การบันทึกค่า (เช่น เมื่อกดปุ่ม Save)
//        SharedPreferencesUtil.saveString("user_name", "Ananchanon") // ชื่อตัวเอง
//        SharedPreferencesUtil.saveBoolean("is_dark_mode", true)

// การดึงค่ามาใช้งาน (เช่น เมื่อเปิดแอพขึ้นมาใหม่)

        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {

            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        _519LabLearnAndroidTheme {
            Greeting("Android")
        }
    }
}