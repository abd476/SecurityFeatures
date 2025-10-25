package com.security.guard.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.security.guard.helpers.NotificationHelper
import com.security.guard.helpers.SecurityGuardPreferences

/**
 * Service that monitors battery level and charging status.
 */
class BatteryMonitorService : Service() {
    
    private lateinit var batteryReceiver: BroadcastReceiver
    private lateinit var preferences: SecurityGuardPreferences
    
    override fun onCreate() {
        super.onCreate()
        
        preferences = SecurityGuardPreferences(this)
        
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = level * 100 / scale.toFloat()
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                
                // Battery full notification
                if (batteryPct >= 100 && isCharging) {
                    NotificationHelper.sendNotification(
                        context = this@BatteryMonitorService,
                        title = "Battery Full",
                        message = "Battery has been fully charged!",
                        notificationId = 101
                    )
                    Toast.makeText(
                        this@BatteryMonitorService, 
                        "Battery fully charged!", 
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // Low battery notification
                else if (batteryPct <= preferences.getLowBatteryThreshold()) {
                    NotificationHelper.sendNotification(
                        context = this@BatteryMonitorService,
                        title = "Battery Low",
                        message = "Battery is low! Current level: ${batteryPct.toInt()}%",
                        notificationId = 102
                    )
                }
            }
        }
        
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        Log.d(TAG, "BatteryMonitorService started")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(batteryReceiver)
            Log.d(TAG, "BatteryMonitorService stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val TAG = "BatteryService"
    }
}

