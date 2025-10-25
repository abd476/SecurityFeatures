package com.example.secuirtyfeatures

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.security.guard.api.SecurityFeature
import com.security.guard.api.SecurityGuard
import com.security.guard.api.SecurityGuardCallback
import com.security.guard.api.SecurityGuardConfig

/**
 * Demo Activity showcasing SecurityGuard library features.
 */
class MainActivity : AppCompatActivity(), SecurityGuardCallback {
    
    private lateinit var securityGuard: SecurityGuard
    
    // UI elements
    private lateinit var switchChargerDetection: SwitchMaterial
    private lateinit var switchBatteryMonitor: SwitchMaterial
    private lateinit var switchWifiMonitor: SwitchMaterial
    private lateinit var switchClapDetection: SwitchMaterial
    private lateinit var switchMotionDetection: SwitchMaterial
    private lateinit var switchProximityDetection: SwitchMaterial
    private lateinit var switchHandsfreeDetection: SwitchMaterial
    private lateinit var tvStatus: TextView
    
    // Permission launcher for microphone (needed for clap detection)
    private val microphonePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            switchClapDetection.isChecked = true
            enableFeature(SecurityFeature.CLAP_DETECTION)
        } else {
            switchClapDetection.isChecked = false
            showToast("Microphone permission is required for clap detection")
        }
    }
    
    // Permission launcher for notifications
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showToast("Notification permission denied. Some features may not work properly.")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        switchChargerDetection = findViewById(R.id.switchChargerDetection)
        switchBatteryMonitor = findViewById(R.id.switchBatteryMonitor)
        switchWifiMonitor = findViewById(R.id.switchWifiMonitor)
        switchClapDetection = findViewById(R.id.switchClapDetection)
        switchMotionDetection = findViewById(R.id.switchMotionDetection)
        switchProximityDetection = findViewById(R.id.switchProximityDetection)
        switchHandsfreeDetection = findViewById(R.id.switchHandsfreeDetection)
        tvStatus = findViewById(R.id.tvStatus)
        
        // Initialize SecurityGuard
        securityGuard = SecurityGuard.getInstance(this)
        
        // Configure SecurityGuard
        val config = SecurityGuardConfig(
            enableNotifications = true,
            alarmDuration = 5000L,
            motionSensitivity = 600,
            clapSensitivity = 6500,
            lowBatteryThreshold = 15,
            vibrationEnabled = true
        )
        securityGuard.configure(config)
        
        // Set callback
        securityGuard.setCallback(this)
        
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        // Setup switch listeners
        setupSwitchListeners()
        
        // Update initial status
        updateStatus()
    }
    
    private fun setupSwitchListeners() {
        // Charger Detection
        switchChargerDetection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableFeature(SecurityFeature.CHARGER_DETECTION)
            } else {
                disableFeature(SecurityFeature.CHARGER_DETECTION)
            }
        }
        
        // Battery Monitor
        switchBatteryMonitor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableFeature(SecurityFeature.BATTERY_MONITOR)
            } else {
                disableFeature(SecurityFeature.BATTERY_MONITOR)
            }
        }
        
        // WiFi Monitor
        switchWifiMonitor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableFeature(SecurityFeature.WIFI_MONITOR)
            } else {
                disableFeature(SecurityFeature.WIFI_MONITOR)
            }
        }
        
        // Clap Detection (requires microphone permission)
        switchClapDetection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (checkMicrophonePermission()) {
                    enableFeature(SecurityFeature.CLAP_DETECTION)
                } else {
                    switchClapDetection.isChecked = false
                    requestMicrophonePermission()
                }
            } else {
                disableFeature(SecurityFeature.CLAP_DETECTION)
            }
        }
        
        // Motion Detection
        switchMotionDetection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableFeature(SecurityFeature.MOTION_DETECTION)
            } else {
                disableFeature(SecurityFeature.MOTION_DETECTION)
            }
        }
        
        // Proximity Detection
        switchProximityDetection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableFeature(SecurityFeature.PROXIMITY_DETECTION)
            } else {
                disableFeature(SecurityFeature.PROXIMITY_DETECTION)
            }
        }
        
        // Handsfree Detection
        switchHandsfreeDetection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableFeature(SecurityFeature.HANDSFREE_DETECTION)
            } else {
                disableFeature(SecurityFeature.HANDSFREE_DETECTION)
            }
        }
    }
    
    private fun enableFeature(feature: SecurityFeature) {
        val success = securityGuard.enableFeature(feature)
        if (success) {
            showToast("${feature.name.replace("_", " ")} enabled")
            updateStatus()
        } else {
            showToast("Failed to enable ${feature.name.replace("_", " ")}")
        }
    }
    
    private fun disableFeature(feature: SecurityFeature) {
        securityGuard.disableFeature(feature)
        showToast("${feature.name.replace("_", " ")} disabled")
        updateStatus()
    }
    
    private fun updateStatus() {
        val activeFeatures = securityGuard.getActiveFeatures()
        if (activeFeatures.isEmpty()) {
            tvStatus.text = "Status: No features active"
        } else {
            tvStatus.text = "Status: ${activeFeatures.size} feature(s) active"
        }
    }
    
    private fun checkMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestMicrophonePermission() {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                AlertDialog.Builder(this)
                    .setTitle("Microphone Permission Required")
                    .setMessage("Clap detection requires microphone permission to listen for clapping sounds.")
                    .setPositiveButton("OK") { _, _ ->
                        microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    // SecurityGuardCallback implementation
    override fun onSecurityEvent(feature: SecurityFeature, message: String) {
        runOnUiThread {
            showToast("Event: $message")
        }
    }
    
    override fun onSecurityError(feature: SecurityFeature, error: Throwable) {
        runOnUiThread {
            showToast("Error in ${feature.name}: ${error.message}")
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Optionally stop all features when app is destroyed
        // securityGuard.stopAll()
    }
}
