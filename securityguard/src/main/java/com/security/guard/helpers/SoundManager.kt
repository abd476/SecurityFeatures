package com.security.guard.helpers

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri

/**
 * Helper class for managing alarm sounds.
 */
object SoundManager {
    
    /**
     * Get the selected alarm sound URI.
     * Returns custom sound if set, otherwise returns default alarm sound.
     * 
     * @param context Application context
     * @return Uri of the sound to play
     */
    fun getSelectedSound(context: Context): Uri {
        val prefs = SecurityGuardPreferences(context)
        return prefs.getAlarmSoundUri() ?: getDefaultAlarmSound()
    }
    
    /**
     * Get default alarm sound.
     * 
     * @return Uri of default alarm sound
     */
    fun getDefaultAlarmSound(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    }
    
    /**
     * Set custom alarm sound.
     * 
     * @param context Application context
     * @param soundUri Uri of the custom sound
     */
    fun setCustomSound(context: Context, soundUri: Uri?) {
        val prefs = SecurityGuardPreferences(context)
        val config = prefs.loadConfig().copy(customAlarmSound = soundUri)
        prefs.saveConfig(config)
    }
}

