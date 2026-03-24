package com.example.a519lablearnandroid

import android.annotation.SuppressLint
import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * SensorCameraViewModel
 * ─────────────────────
 * MVVM ViewModel ที่ถือ state ของ:
 *  1. Accelerometer  →  _accel: FloatArray [x, y, z]
 *  2. GPS Location   →  _location: String  "Lat: xx.xx, Lng: yy.yy"
 *
 * ใช้ AndroidViewModel เพื่อเข้าถึง Application Context สำหรับ
 * SensorManager และ LocationManager
 */
class SensorCameraViewModel(application: Application) : AndroidViewModel(application) {

    // ─── Accelerometer ────────────────────────────────────────────────────────
    private val _accel = MutableStateFlow(floatArrayOf(0f, 0f, 0f))
    val accel: StateFlow<FloatArray> = _accel.asStateFlow()

    // ─── Location ─────────────────────────────────────────────────────────────
    private val _location = MutableStateFlow("กำลังรอ GPS…")
    val location: StateFlow<String> = _location.asStateFlow()

    // ─── Managers ─────────────────────────────────────────────────────────────
    private val sensorManager: SensorManager =
        application.getSystemService(SensorManager::class.java)

    private val locationManager: LocationManager =
        application.getSystemService(LocationManager::class.java)

    // ─── Sensor Listener ──────────────────────────────────────────────────────
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                // คัดลอก array ก่อนส่งเพื่อให้ StateFlow detect การเปลี่ยนแปลงถูกต้อง
                _accel.value = event.values.copyOf()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
    }

    // ─── Location Listener ────────────────────────────────────────────────────
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(loc: Location) {
            _location.value =
                "Lat: %.5f\nLng: %.5f\nAlt: %.1f m".format(loc.latitude, loc.longitude, loc.altitude)
        }
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        override fun onProviderEnabled(provider: String) = Unit
        override fun onProviderDisabled(provider: String) {
            _location.value = "GPS ถูกปิดอยู่"
        }
    }

    init {
        // ── Register Accelerometer ─────────────────────────────────────────────
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        // ── Register Location ──────────────────────────────────────────────────
        // (Permission ถูก check ไว้ใน Activity ก่อนเรียก ViewModel แล้ว)
        // ถ้ายังไม่ได้ permission ก็ไม่ crash เพราะ try/catch ครอบไว้
        startLocationUpdates()
    }

    /**
     * เริ่มขอ location updates — ถูก suppress เพราะ permission ถูก check ใน Activity
     * ก่อนที่ ViewModel จะถูกสร้าง (ใช้ try/catch กันกรณีไม่มี permission)
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        try {
            val provider = when {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                    LocationManager.GPS_PROVIDER
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                    LocationManager.NETWORK_PROVIDER
                else -> null
            }
            if (provider != null) {
                locationManager.requestLocationUpdates(
                    provider,
                    2_000L,   // minTime ms
                    1f,       // minDistance meters
                    locationListener
                )
                // ส่งค่าล่าสุดทันทีถ้ามี
                locationManager.getLastKnownLocation(provider)?.let { loc ->
                    _location.value =
                        "Lat: %.5f\nLng: %.5f\nAlt: %.1f m".format(
                            loc.latitude, loc.longitude, loc.altitude
                        )
                }
            } else {
                _location.value = "ไม่พบ GPS หรือ Network provider"
            }
        } catch (_: SecurityException) {
            _location.value = "ไม่มีสิทธิ์เข้าถึง Location"
        }
    }

    // ─── Cleanup ──────────────────────────────────────────────────────────────
    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(sensorEventListener)
        locationManager.removeUpdates(locationListener)
    }
}
