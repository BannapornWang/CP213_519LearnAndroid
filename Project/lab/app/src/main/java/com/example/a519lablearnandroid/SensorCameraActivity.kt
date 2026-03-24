package com.example.a519lablearnandroid

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
class SensorCameraActivity : ComponentActivity() {

    // ViewModel สำหรับ StateFlow
    private val viewModel: SensorCameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request Location Permissions แบบง่ายๆ ตอนเริ่ม Activity
        // (สามารถประยุกต์ใช้ rememberLauncherForActivityResult ใน Compose แทนได้เพื่อรับ result กลับมาสวยๆ)
        requestLocationPermissions()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SensorCameraScreen(viewModel = viewModel)
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        val fineLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLoc != PackageManager.PERMISSION_GRANTED && coarseLoc != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1001
            )
        } else {
            // มีสิทธิ์แล้ว
            viewModel.startLocationUpdates()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.startLocationUpdates()
        }
    }
}

@Composable
fun SensorCameraScreen(viewModel: SensorCameraViewModel) {
    val context = LocalContext.current

    // Observe State จาก ViewModel
    val accel by viewModel.accel.collectAsState()
    val locationText by viewModel.location.collectAsState()

    // State เก็บรูป Bitmap
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher สำหรับถ่ายรูป
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        imageBitmap = bitmap
    }

    // Launcher สำหรับขอ Permission กล้อง
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // ได้รับสิทธิ์แล้ว ให้เปิดกล้อง
            takePictureLauncher.launch()
        } else {
            Toast.makeText(context, "CAMERA permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MVVM: Sensor & Camera",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // ─── Accelerometer ──────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Accelerometer (X, Y, Z)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                // แสดงผลอัปเดตแบบ Real-time
                Text("X: %.2f".format(accel[0]))
                Text("Y: %.2f".format(accel[1]))
                Text("Z: %.2f".format(accel[2]))
            }
        }

        // ─── Location ──────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("GPS Location", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = locationText)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─── Camera ─────────────────────────────────────────────
        Button(
            onClick = {
                val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // อนุญาตแล้ว -> เปิดกล้อง
                    takePictureLauncher.launch()
                } else {
                    // ยังไม่อนุญาต -> ขอสิทธิ์
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Take Picture")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─── แสดงรูปที่ถ่ายมา ─────────────────────────────────────
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No image captured",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
