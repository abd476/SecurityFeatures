package com.security.guard.api

/**
 * Enum representing all available security features in the SecurityGuard library.
 */
enum class SecurityFeature {
    /**
     * Detects when charger is connected or disconnected
     */
    CHARGER_DETECTION,
    
    /**
     * Monitors battery level (full charge, low battery)
     */
    BATTERY_MONITOR,
    
    /**
     * Monitors WiFi connection status
     */
    WIFI_MONITOR,
    
    /**
     * Detects clapping sounds to find phone
     */
    CLAP_DETECTION,
    
    /**
     * Detects device motion/movement
     */
    MOTION_DETECTION,
    
    /**
     * Detects nearby objects using proximity sensor
     */
    PROXIMITY_DETECTION,
    
    /**
     * Detects headphone/handsfree connection/disconnection
     */
    HANDSFREE_DETECTION
}

