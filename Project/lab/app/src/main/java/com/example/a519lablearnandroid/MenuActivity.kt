package com.example.a519lablearnandroid

// checking 24/2/2026

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


class MenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, RPGCardActivity::class.java)
                    )
                }) {
                    Text(text = "RPGCardActivity")
                }

                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, PokedexActivity::class.java)
                    )
                }) {
                    Text(text = "PokedexActivity")
                }

                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, LifeCycleComposeActivity::class.java)
                    )
                }) {
                    Text(text = "LifeCycleComposeActivity")
                }

                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, SharedPreferencesActivity::class.java)
                    )
                }) {
                    Text(text = "SharedPreferencesActivity")
                }

                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, GalleryActivity::class.java)
                    )
                }) {
                    Text(text = "GalleryActivity")
                }

                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, SensorCameraActivity::class.java)
                    )
                }) {
                    Text(text = "Sensor & Camera Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part1AnimationActivity::class.java)
                    )
                }) {
                    Text(text = "Part1AnimationActivity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part2Activity::class.java)
                    )
                }) {
                    Text(text = "Part2Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part3Activity::class.java)
                    )
                }) {
                    Text(text = "Part3Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part4Activity::class.java)
                    )
                }) {
                    Text(text = "Part4Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part5Activity::class.java)
                    )
                }) {
                    Text(text = "Part5Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part6Activity::class.java)
                    )
                }) {
                    Text(text = "Part6Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part7Activity::class.java)
                    )
                }) {
                    Text(text = "Part7Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part8Activity::class.java)
                    )
                }) {
                    Text(text = "Part8Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part9Activity::class.java)
                    )
                }) {
                    Text(text = "Part9Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part10Activity::class.java)
                    )
                }) {
                    Text(text = "Part10Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part11Activity::class.java)
                    )
                }) {
                    Text(text = "Part11Activity")
                }
                Button(onClick = {
                    startActivity(
                        Intent(this@MenuActivity, Part12Activity::class.java)
                    )
                }) {
                    Text(text = "Part12Activity")
                }
            }
        }
    }
}

//hello sawaddeee
