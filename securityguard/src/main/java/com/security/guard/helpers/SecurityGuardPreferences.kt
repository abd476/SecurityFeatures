package com.security.guard.helpers

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.security.guard.api.SecurityFeature
import com.security.guard.api.SecurityGuardConfig

/**
 * Internal helper class to manage SecurityGuard preferences.
 */
internal class SecurityGuardPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "security_guard_prefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_ALARM_SOUND = "alarm_sound"
        private const val KEY_ALARM_DURATION = "alarm_duration"
        private const val KEY_MOTION_SENSITIVITY = "motion_sensitivity"
        private const val KEY_CLAP_SENSITIVITY = "clap_sensitivity"
        private const val KEY_LOW_BATTERY_THRESHOLD = "low_battery_threshold"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_FEATURE_PREFIX = "feature_enabled_"
    }
    
    fun saveConfig(config: SecurityGuardConfig) {
        prefs.edit().apply {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, config.enableNotifications)
            putString(KEY_ALARM_SOUND, config.customAlarmSound?.toString())
            putLong(KEY_ALARM_DURATION, config.alarmDuration)
            putInt(KEY_MOTION_SENSITIVITY, config.motionSensitivity)
            putInt(KEY_CLAP_SENSITIVITY, config.clapSensitivity)
            putInt(KEY_LOW_BATTERY_THRESHOLD, config.lowBatteryThreshold)
            putBoolean(KEY_VIBRATION_ENABLED, config.vibrationEnabled)
            apply()
        }
    }
    
    fun loadConfig(): SecurityGuardConfig {
        return SecurityGuardConfig(
            enableNotifications = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
            customAlarmSound = prefs.getString(KEY_ALARM_SOUND, null)?.let { Uri.parse(it) },
            alarmDuration = prefs.getLong(KEY_ALARM_DURATION, 5000L),
            motionSensitivity = prefs.getInt(KEY_MOTION_SENSITIVITY, 600),
            clapSensitivity = prefs.getInt(KEY_CLAP_SENSITIVITY, 6500),
            lowBatteryThreshold = prefs.getInt(KEY_LOW_BATTERY_THRESHOLD, 15),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        )
    }
    
    fun setFeatureEnabled(feature: SecurityFeature, enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FEATURE_PREFIX + feature.name, enabled).apply()
    }
    
    fun isFeatureEnabled(feature: SecurityFeature): Boolean {
        return prefs.getBoolean(KEY_FEATURE_PREFIX + feature.name, false)
    }
    
    fun getAlarmSoundUri(): Uri? {
        return prefs.getString(KEY_ALARM_SOUND, null)?.let { Uri.parse(it) }
    }
    
    fun getAlarmDuration(): Long {
        return prefs.getLong(KEY_ALARM_DURATION, 5000L)
    }
    
    fun getMotionSensitivity(): Int {
        return prefs.getInt(KEY_MOTION_SENSITIVITY, 600)
    }
    
    fun getClapSensitivity(): Int {
        return prefs.getInt(KEY_CLAP_SENSITIVITY, 6500)
    }
    
    fun getLowBatteryThreshold(): Int {
        return prefs.getInt(KEY_LOW_BATTERY_THRESHOLD, 15)
    }
    
    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
    }
}

