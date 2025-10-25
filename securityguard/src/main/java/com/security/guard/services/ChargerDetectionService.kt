package com.security.guard.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.BatteryManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.security.guard.helpers.NotificationHelper
import com.security.guard.helpers.SoundManager

/**
 * Service that monitors charger connection/disconnection events.
 */
class ChargerDetectionService : Service() {
    
    private lateinit var batteryReceiver: BroadcastReceiver
    private var wasCharging = false
    private var isAlarmPlaying = false
    private var lastAlarmTime = 0L
    private val alarmCooldownDuration = 10000L
    
    override fun onCreate() {
        super.onCreate()
        
        // Get initial charging state
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        wasCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || 
                     status == BatteryManager.BATTERY_STATUS_FULL
        
        // Create battery receiver
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || 
                                status == BatteryManager.BATTERY_STATUS_FULL
                
                // Detect disconnection
                if (wasCharging && !isCharging) {
                    if (canTriggerAlarm(System.currentTimeMillis())) {
                        NotificationHelper.sendNotification(
                            context = this@ChargerDetectionService,
                            title = "Charger Disconnected",
                            message = "Charger has been disconnected!",
                            notificationId = 101
                        )
                        triggerAlarm("Charger disconnected!")
                    }
                }
                
                wasCharging = isCharging
            }
        }
        
        // Register receiver
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        Log.d(TAG, "ChargerDetectionService started")
    }
    
    private fun canTriggerAlarm(currentTime: Long): Boolean {
        return !isAlarmPlaying && (currentTime - lastAlarmTime >= alarmCooldownDuration)
    }
    
    private fun triggerAlarm(message: String) {
        val currentTime = System.currentTimeMillis()
        isAlarmPlaying = true
        lastAlarmTime = currentTime
        
        NotificationHelper.sendNotification(
            context = this,
            title = "Charger Disconnected",
            message = message,
            notificationId = 104
        )
        
        try {
            val soundUri = SoundManager.getSelectedSound(this)
            val mediaPlayer = MediaPlayer.create(this, soundUri)
            mediaPlayer?.start()
            
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer?.stop()
                mediaPlayer?.release()
                isAlarmPlaying = false
            }, 5000)
        } catch (e: Exception) {
            Log.e(TAG, "Error playing alarm", e)
            isAlarmPlaying = false
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(batteryReceiver)
            Log.d(TAG, "ChargerDetectionService stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val TAG = "ChargerService"
    }
}

