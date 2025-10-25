package com.security.guard.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import com.security.guard.helpers.SecurityGuardPreferences
import com.security.guard.helpers.SoundManager
import kotlin.math.sqrt

/**
 * Service that detects clapping sounds to help find the phone.
 */
class ClapDetectionService : Service() {
    
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingThread: Thread? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var preferences: SecurityGuardPreferences
    private val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    
    private var isResponseActive = false
    private var lastResponseTime = 0L
    private val RESPONSE_COOLDOWN = 10000L // 10 seconds cooldown between triggers
    
    override fun onCreate() {
        super.onCreate()
        preferences = SecurityGuardPreferences(this)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Check if permission is granted before starting
        if (checkMicrophonePermission()) {
            if (!isRecording) startClapDetection()
        } else {
            Log.e(TAG, "Microphone permission not granted")
            // Send broadcast to notify activity to request permission
            sendPermissionRequiredBroadcast()
            stopSelf() // Stop the service
        }
        return START_STICKY
    }
    
    private fun checkMicrophonePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    private fun sendPermissionRequiredBroadcast() {
        val intent = Intent("MICROPHONE_PERMISSION_REQUIRED")
        sendBroadcast(intent)
    }
    
    private fun startClapDetection() {
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            
            // Check if AudioRecord was initialized successfully
            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord initialization failed")
                stopSelf()
                return
            }
            
            isRecording = true
            audioRecord?.startRecording()
            
            recordingThread = Thread { detectClaps() }
            recordingThread?.start()
            
            Log.d(TAG, "Clap detection started successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception: Missing RECORD_AUDIO permission", e)
            sendPermissionRequiredBroadcast()
            stopSelf()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting clap detection", e)
            stopSelf()
        }
    }
    
    private fun detectClaps() {
        val buffer = ShortArray(bufferSize)
        var lastClapTime = 0L
        val clapThreshold = preferences.getClapSensitivity()
        
        while (isRecording) {
            try {
                val bytesRead = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                
                if (bytesRead > 0) {
                    var sum = 0.0
                    for (sample in buffer) {
                        sum += sample * sample
                    }
                    val rms = sqrt(sum / buffer.size)
                    
                    if (rms > clapThreshold) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastClapTime > 1000) {
                            lastClapTime = currentTime
                            handler.post { onClapDetected() }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading audio data", e)
                break
            }
        }
    }
    
    private fun onClapDetected() {
        // Check if we're already playing a response or within cooldown period
        val currentTime = System.currentTimeMillis()
        if (isResponseActive || (currentTime - lastResponseTime) < RESPONSE_COOLDOWN) {
            Log.d(TAG, "Response blocked - cooldown active or response playing")
            return
        }
        
        triggerResponse("Clap detected! Phone found!")
    }
    
    private fun triggerResponse(message: String) {
        try {
            // Mark response as active
            isResponseActive = true
            lastResponseTime = System.currentTimeMillis()
            
            val soundUri = SoundManager.getSelectedSound(this)
            val mediaPlayer = MediaPlayer.create(this, soundUri)
            mediaPlayer?.start()
            
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "SecurityGuard:ClapResponse"
            )
            wakeLock.acquire(5000)
            
            handler.postDelayed({
                mediaPlayer?.stop()
                mediaPlayer?.release()
                if (wakeLock.isHeld) wakeLock.release()
                // Mark response as inactive after completion
                isResponseActive = false
            }, 5000)
            
            val notification = Intent("CLAP_RESPONSE")
            notification.putExtra("message", message)
            sendBroadcast(notification)
            
            Log.d(TAG, "Clap response triggered")
        } catch (e: Exception) {
            Log.e(TAG, "Error triggering response", e)
            isResponseActive = false // Reset on error
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
            Log.d(TAG, "ClapDetectionService stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio recording", e)
        }
        audioRecord = null
        recordingThread = null
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val TAG = "ClapService"
    }
}

