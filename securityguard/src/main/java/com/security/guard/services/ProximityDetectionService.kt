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
import com.security.guard.helpers.SoundManager

/**
 * Service that detects nearby objects using the proximity sensor.
 */
class ProximityDetectionService : Service(), SensorEventListener {
    
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var isAlarmPlaying = false
    private var lastAlarmTime = 0L
    private val alarmCooldownDuration = 3000L // 3 seconds cooldown for proximity
    
    override fun onCreate() {
        super.onCreate()
        
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "ProximityDetectionService started")
        } ?: run {
            Log.e(TAG, "Proximity sensor not available")
            stopSelf()
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val maxRange = proximitySensor?.maximumRange ?: 5.0f
            
            // Trigger alarm when object is detected nearby
            if (distance < maxRange) {
                val currentTime = System.currentTimeMillis()
                if (!isAlarmPlaying && (currentTime - lastAlarmTime >= alarmCooldownDuration)) {
                    triggerProximityAlarm("Object detected nearby!")
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
    
    private fun triggerProximityAlarm(message: String) {
        try {
            isAlarmPlaying = true
            lastAlarmTime = System.currentTimeMillis()
            
            val soundUri = SoundManager.getSelectedSound(this)
            val mediaPlayer = MediaPlayer.create(this, soundUri)
            mediaPlayer?.start()
            
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer?.stop()
                mediaPlayer?.release()
                isAlarmPlaying = false
            }, 3000)
            
            Log.d(TAG, "Proximity alarm triggered: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing alarm", e)
            isAlarmPlaying = false
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "ProximityDetectionService stopped")
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val TAG = "ProximityService"
    }
}

