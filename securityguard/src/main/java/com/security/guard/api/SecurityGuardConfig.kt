package com.security.guard.api

import android.net.Uri

/**
 * Configuration class for SecurityGuard library.
 * 
 * @param enableNotifications Whether to show notifications for security events
 * @param customAlarmSound Custom alarm sound URI (null to use default)
 * @param alarmDuration Duration of alarm sound in milliseconds (default 5000ms)
 * @param motionSensitivity Sensitivity for motion detection (100-1000, default 600)
 * @param clapSensitivity Sensitivity for clap detection (5000-10000, default 6500)
 * @param lowBatteryThreshold Battery percentage threshold for low battery alert (default 15)
 * @param vibrationEnabled Whether to enable vibration with alarms
 */
data class SecurityGuardConfig(
    val enableNotifications: Boolean = true,
    val customAlarmSound: Uri? = null,
    val alarmDuration: Long = 5000L,
    val motionSensitivity: Int = 600,
    val clapSensitivity: Int = 6500,
    val lowBatteryThreshold: Int = 15,
    val vibrationEnabled: Boolean = true
)

