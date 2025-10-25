package com.security.guard.api

/**
 * Callback interface for security events.
 * Implement this interface to receive notifications when security events are triggered.
 */
interface SecurityGuardCallback {
    /**
     * Called when a security event is detected.
     * 
     * @param feature The security feature that triggered the event
     * @param message Description of the event
     */
    fun onSecurityEvent(feature: SecurityFeature, message: String)
    
    /**
     * Called when a security feature encounters an error.
     * 
     * @param feature The security feature that encountered the error
     * @param error The error that occurred
     */
    fun onSecurityError(feature: SecurityFeature, error: Throwable) {}
}

