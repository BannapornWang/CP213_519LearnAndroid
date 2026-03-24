package com.example.a519lablearnandroid

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

class GalleryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _519LabLearnAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // State เก็บ Uri ของรูปภาพที่เลือก
                    var imageUri by remember { mutableStateOf<Uri?>(null) }

                    // 1) Launcher สำหรับขอ Permission อ่านรูปภาพ (Android 13+)
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        // ถ้าได้รับสิทธิ์แล้ว → เปิด Gallery ทันที
                        // (เราต้องการ galleryLauncher ตรงนี้ แต่ Kotlin Compose ไม่รองรับ
                        //  forward-reference ใน remember block ตรงๆ → ใช้ flag แทน)
                        // ที่ง่ายที่สุดคือ reference จาก lambda สุดท้ายด้านล่าง
                    }

                    // 2) Launcher สำหรับเปิด Gallery รับ Uri รูปภาพกลับมา
                    val galleryLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        imageUri = uri   // เก็บ Uri ลง State → ทริก Recomposition
                    }

                    // Permission จริงที่ใช้บน Android 13+ (API 33+)
                    val readMediaPermission = Manifest.permission.READ_MEDIA_IMAGES

                    // Launcher ที่ reference galleryLauncher ได้แล้ว (สร้างหลัง galleryLauncher)
                    val permissionLauncherWithGallery = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            galleryLauncher.launch("image/*")
                        }
                    }

                    // 3) UI
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))

                        Text(
                            text = "Gallery & Permission Flow",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4) ปุ่มกดพร้อม Logic เช็ค Permission
                        Button(
                            onClick = {
                                val permissionStatus = ContextCompat.checkSelfPermission(
                                    this@GalleryActivity,
                                    readMediaPermission
                                )
                                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                    // ✅ ได้รับสิทธิ์แล้ว → เปิด Gallery
                                    galleryLauncher.launch("image/*")
                                } else {
                                    // ❌ ยังไม่ได้รับสิทธิ์ → ขอ Permission
                                    permissionLauncherWithGallery.launch(readMediaPermission)
                                }
                            }
                        ) {
                            Text(text = "เลือกรูปภาพ")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 5) แสดงผลรูปภาพจาก Uri ด้วย Coil AsyncImage
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "รูปภาพที่เลือก",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ยังไม่ได้เลือกรูปภาพ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
