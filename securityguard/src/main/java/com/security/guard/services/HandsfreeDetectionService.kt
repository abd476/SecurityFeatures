package com.security.guard.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.security.guard.helpers.NotificationHelper
import com.security.guard.helpers.SoundManager

/**
 * Service that detects headphone/handsfree connection and disconnection.
 */
class HandsfreeDetectionService : Service() {
    
    private lateinit var headsetReceiver: BroadcastReceiver
    
    override fun onCreate() {
        super.onCreate()
        
        headsetReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_HEADSET_PLUG -> {
                        val state = intent.getIntExtra("state", -1)
                        if (state == 0) {
                            // Headphones disconnected
                            triggerHeadsetAlarm("Headphones disconnected!")
                            NotificationHelper.sendNotification(
                                context = this@HandsfreeDetectionService,
                                title = "Headphones Disconnected",
                                message = "Headphones have been disconnected",
                                notificationId = 103
                            )
                            Toast.makeText(
                                context, 
                                "Headphones disconnected!", 
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (state == 1) {
                            // Headphones connected
                            Log.d(TAG, "Headphones connected")
                        }
                    }
                }
            }
        }
        
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(headsetReceiver, filter)
        Log.d(TAG, "HandsfreeDetectionService started")
    }
    
    private fun triggerHeadsetAlarm(message: String) {
        try {
            val soundUri = SoundManager.getSelectedSound(this)
            val mediaPlayer = MediaPlayer.create(this, soundUri)
            mediaPlayer?.start()
            
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer?.stop()
                mediaPlayer?.release()
            }, 3000)
            
            Log.d(TAG, "Headset alarm triggered: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing alarm", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(headsetReceiver)
            Log.d(TAG, "HandsfreeDetectionService stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val TAG = "HandsfreeService"
    }
}

