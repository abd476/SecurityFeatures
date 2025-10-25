package com.security.guard.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.security.guard.R

/**
 * Helper class for managing notifications.
 */
object NotificationHelper {
    
    private const val CHANNEL_ID = "security_guard_channel"
    private const val CHANNEL_NAME = "Security Alerts"
    private const val CHANNEL_DESCRIPTION = "Notifications for security events"
    
    /**
     * Create notification channel (required for Android O+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Send a notification.
     * 
     * @param context Application context
     * @param title Notification title
     * @param message Notification message
     * @param notificationId Unique notification ID
     */
    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        try {
            createNotificationChannel(context)
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 200, 500))
                .build()
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notification)
        } catch (e: Exception) {
            // Silently fail if notifications can't be shown
            e.printStackTrace()
        }
    }
    
    /**
     * Cancel a specific notification.
     * 
     * @param context Application context
     * @param notificationId Notification ID to cancel
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
    
    /**
     * Cancel all notifications.
     * 
     * @param context Application context
     */
    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}

