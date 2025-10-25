package com.security.guard.services

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.security.guard.helpers.SecurityGuardPreferences
import com.security.guard.helpers.SoundManager
import kotlin.math.abs

/**
 * Service that detects device motion using the accelerometer.
 */
class MotionDetectionService : Service(), SensorEventListener {
    
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdate = 0L
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var isAlarmPlaying = false
    private var lastAlarmTime = 0L
    private val alarmCooldownDuration = 10000L
    private lateinit var preferences: SecurityGuardPreferences
    
    override fun onCreate() {
        super.onCreate()
        
        preferences = SecurityGuardPreferences(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "MotionDetectionService started")
        } ?: run {
            Log.e(TAG, "Accelerometer not available")
            stopSelf()
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            
            if (currentTime - lastUpdate > 100) {
                val timeDiff = currentTime - lastUpdate
                lastUpdate = currentTime
                
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                
                val speed = abs(x + y + z - lastX - lastY - lastZ) / timeDiff * 10000
                
                val motionThreshold = preferences.getMotionSensitivity()
                if (speed > motionThreshold) {
                    if (canTriggerAlarm(currentTime)) {
                        triggerMotionAlarm("Motion detected!")
                    }
                }
                
                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }
    
    private fun canTriggerAlarm(currentTime: Long): Boolean {
        return !isAlarmPlaying && (currentTime - lastAlarmTime >= alarmCooldownDuration)
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
    
    private fun triggerMotionAlarm(message: String) {
        try {
            val currentTime = System.currentTimeMillis()
            isAlarmPlaying = true
            lastAlarmTime = currentTime
            
            val soundUri = SoundManager.getSelectedSound(this)
            val mediaPlayer = MediaPlayer.create(this, soundUri)
            mediaPlayer?.start()
            
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer?.stop()
                mediaPlayer?.release()
                isAlarmPlaying = false
            }, 5000)
            
            Log.d(TAG, "Motion alarm triggered: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing alarm", e)
            isAlarmPlaying = false
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "MotionDetectionService stopped")
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val TAG = "MotionService"
    }
}

