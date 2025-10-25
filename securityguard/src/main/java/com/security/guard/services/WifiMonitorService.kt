package com.security.guard.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.security.guard.helpers.NotificationHelper
import com.security.guard.helpers.SoundManager

/**
 * Service that monitors WiFi connection status.
 */
class WifiMonitorService : Service() {
    
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var wasWifiConnected = false
    private var isAlarmPlaying = false
    private var lastAlarmTime = 0L
    private val alarmCooldownDuration = 10000L
    
    override fun onCreate() {
        super.onCreate()
        
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        // Get initial WiFi state
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        wasWifiConnected = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        
        // Create network callback
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                val isWifiLost = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true || 
                                wasWifiConnected
                
                if (isWifiLost && canTriggerAlarm(System.currentTimeMillis())) {
                    sendWifiNotification("WiFi Disconnected", "WiFi connection has been lost", 201)
                    triggerAlarm("WiFi connection lost!")
                    wasWifiConnected = false
                }
            }
            
            override fun onAvailable(network: Network) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                    if (!wasWifiConnected && canTriggerAlarm(System.currentTimeMillis())) {
                        sendWifiNotification("WiFi Connected", "WiFi connection has been established", 202)
                        triggerAlarm("WiFi connected!")
                    }
                    wasWifiConnected = true
                }
            }
            
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val isWifiConnected = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                
                if (isWifiConnected != wasWifiConnected && canTriggerAlarm(System.currentTimeMillis())) {
                    if (isWifiConnected) {
                        sendWifiNotification("WiFi Connected", "WiFi connection has been established", 202)
                        triggerAlarm("WiFi connected!")
                    } else {
                        sendWifiNotification("WiFi Disconnected", "WiFi connection has been lost", 201)
                        triggerAlarm("WiFi connection lost!")
                    }
                    wasWifiConnected = isWifiConnected
                }
            }
        }
        
        // Register network callback
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        Log.d(TAG, "WifiMonitorService started")
    }
    
    private fun canTriggerAlarm(currentTime: Long): Boolean {
        return !isAlarmPlaying && (currentTime - lastAlarmTime >= alarmCooldownDuration)
    }
    
    private fun sendWifiNotification(title: String, message: String, notificationId: Int) {
        try {
            NotificationHelper.sendNotification(
                context = this,
                title = title,
                message = message,
                notificationId = notificationId
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error sending notification", e)
        }
    }
    
    private fun triggerAlarm(message: String) {
        val currentTime = System.currentTimeMillis()
        isAlarmPlaying = true
        lastAlarmTime = currentTime
        
        val notification = Intent("WIFI_ALARM")
        notification.putExtra("message", message)
        sendBroadcast(notification)
        
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
            connectivityManager.unregisterNetworkCallback(networkCallback)
            Log.d(TAG, "WifiMonitorService stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering callback", e)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val TAG = "WifiService"
    }
}

