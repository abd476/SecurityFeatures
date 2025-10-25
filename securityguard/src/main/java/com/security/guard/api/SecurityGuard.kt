package com.security.guard.api

import android.content.Context
import android.content.Intent
import com.security.guard.helpers.SecurityGuardPreferences
import com.security.guard.services.*

/**
 * Main API class for SecurityGuard library.
 * 
 * This is the single entry point for using all security features.
 * 
 * Example usage:
 * ```
 * // Initialize
 * val securityGuard = SecurityGuard.getInstance(context)
 * 
 * // Configure (optional)
 * val config = SecurityGuardConfig(
 *     enableNotifications = true,
 *     alarmDuration = 5000L
 * )
 * securityGuard.configure(config)
 * 
 * // Set callback (optional)
 * securityGuard.setCallback(object : SecurityGuardCallback {
 *     override fun onSecurityEvent(feature: SecurityFeature, message: String) {
 *         // Handle event
 *     }
 * })
 * 
 * // Enable features
 * securityGuard.enableFeature(SecurityFeature.CHARGER_DETECTION)
 * securityGuard.enableFeature(SecurityFeature.MOTION_DETECTION)
 * 
 * // Disable features
 * securityGuard.disableFeature(SecurityFeature.CHARGER_DETECTION)
 * 
 * // Stop all
 * securityGuard.stopAll()
 * ```
 */
class SecurityGuard private constructor(private val context: Context) {
    
    private val appContext = context.applicationContext
    private val preferences = SecurityGuardPreferences(appContext)
    private var callback: SecurityGuardCallback? = null
    private var config: SecurityGuardConfig = SecurityGuardConfig()
    
    private val activeFeatures = mutableSetOf<SecurityFeature>()
    
    companion object {
        @Volatile
        private var instance: SecurityGuard? = null
        
        /**
         * Get singleton instance of SecurityGuard.
         * 
         * @param context Application context
         * @return SecurityGuard instance
         */
        fun getInstance(context: Context): SecurityGuard {
            return instance ?: synchronized(this) {
                instance ?: SecurityGuard(context).also { instance = it }
            }
        }
    }
    
    /**
     * Configure SecurityGuard with custom settings.
     * 
     * @param config Configuration object
     */
    fun configure(config: SecurityGuardConfig) {
        this.config = config
        preferences.saveConfig(config)
    }
    
    /**
     * Get current configuration.
     * 
     * @return Current SecurityGuardConfig
     */
    fun getConfig(): SecurityGuardConfig {
        return config
    }
    
    /**
     * Set callback for security events.
     * 
     * @param callback Callback interface implementation
     */
    fun setCallback(callback: SecurityGuardCallback?) {
        this.callback = callback
    }
    
    /**
     * Enable a security feature.
     * 
     * @param feature The feature to enable
     * @return true if successfully enabled, false otherwise
     */
    fun enableFeature(feature: SecurityFeature): Boolean {
        try {
            val serviceIntent = getServiceIntent(feature) ?: return false
            appContext.startService(serviceIntent)
            activeFeatures.add(feature)
            preferences.setFeatureEnabled(feature, true)
            return true
        } catch (e: Exception) {
            callback?.onSecurityError(feature, e)
            return false
        }
    }
    
    /**
     * Disable a security feature.
     * 
     * @param feature The feature to disable
     */
    fun disableFeature(feature: SecurityFeature) {
        try {
            val serviceIntent = getServiceIntent(feature) ?: return
            appContext.stopService(serviceIntent)
            activeFeatures.remove(feature)
            preferences.setFeatureEnabled(feature, false)
        } catch (e: Exception) {
            callback?.onSecurityError(feature, e)
        }
    }
    
    /**
     * Check if a feature is currently enabled.
     * 
     * @param feature The feature to check
     * @return true if enabled, false otherwise
     */
    fun isFeatureEnabled(feature: SecurityFeature): Boolean {
        return activeFeatures.contains(feature)
    }
    
    /**
     * Enable multiple features at once.
     * 
     * @param features Set of features to enable
     */
    fun enableFeatures(vararg features: SecurityFeature) {
        features.forEach { enableFeature(it) }
    }
    
    /**
     * Disable multiple features at once.
     * 
     * @param features Set of features to disable
     */
    fun disableFeatures(vararg features: SecurityFeature) {
        features.forEach { disableFeature(it) }
    }
    
    /**
     * Stop all active security features.
     */
    fun stopAll() {
        activeFeatures.toList().forEach { disableFeature(it) }
    }
    
    /**
     * Get set of currently active features.
     * 
     * @return Set of active SecurityFeature
     */
    fun getActiveFeatures(): Set<SecurityFeature> {
        return activeFeatures.toSet()
    }
    
    /**
     * Internal method to get service intent for a feature.
     */
    private fun getServiceIntent(feature: SecurityFeature): Intent? {
        val serviceClass = when (feature) {
            SecurityFeature.CHARGER_DETECTION -> ChargerDetectionService::class.java
            SecurityFeature.BATTERY_MONITOR -> BatteryMonitorService::class.java
            SecurityFeature.WIFI_MONITOR -> WifiMonitorService::class.java
            SecurityFeature.CLAP_DETECTION -> ClapDetectionService::class.java
            SecurityFeature.MOTION_DETECTION -> MotionDetectionService::class.java
            SecurityFeature.PROXIMITY_DETECTION -> ProximityDetectionService::class.java
            SecurityFeature.HANDSFREE_DETECTION -> HandsfreeDetectionService::class.java
        }
        return Intent(appContext, serviceClass)
    }
    
    /**
     * Internal method to trigger callback from services.
     */
    internal fun triggerCallback(feature: SecurityFeature, message: String) {
        callback?.onSecurityEvent(feature, message)
    }
}

