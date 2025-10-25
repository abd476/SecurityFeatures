# SecurityGuard Android Library

A comprehensive Android library providing easy-to-use security monitoring features for your Android applications.

## Features

SecurityGuard provides the following security monitoring capabilities:

### 🔌 Charger Detection
- Detects when the device charger is connected or disconnected
- Triggers alarm and notification on charger disconnection
- Useful for preventing theft when charging

### 🔋 Battery Monitor
- Monitors battery level continuously
- Alerts when battery is fully charged (100%)
- Alerts when battery is low (configurable threshold, default 15%)

### 📶 WiFi Monitor
- Monitors WiFi connection status
- Alerts when WiFi is connected or disconnected
- Useful for tracking network connectivity changes

### 👏 Clap Detection
- Uses microphone to detect clapping sounds
- Helps locate your phone by clapping
- Configurable sensitivity
- Requires RECORD_AUDIO permission

### 📱 Motion Detection
- Uses accelerometer to detect device movement
- Triggers alarm when device is moved
- Configurable sensitivity
- Perfect for anti-theft scenarios

### 🤚 Proximity Detection
- Uses proximity sensor to detect nearby objects
- Triggers alarm when objects are detected close to the device

### 🎧 Handsfree Detection
- Detects headphone/handsfree connection and disconnection
- Triggers alarm when headphones are disconnected

## Installation

### Add the library to your project

1. Add the library module to your `settings.gradle.kts`:
```kotlin
include(":securityguard")
```

2. Add dependency in your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":securityguard"))
}
```

### Required Permissions

Add these permissions to your app's `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

## Usage

### Basic Usage

```kotlin
// Initialize SecurityGuard
val securityGuard = SecurityGuard.getInstance(context)

// Enable a feature
securityGuard.enableFeature(SecurityFeature.CHARGER_DETECTION)

// Disable a feature
securityGuard.disableFeature(SecurityFeature.CHARGER_DETECTION)

// Stop all features
securityGuard.stopAll()
```

### Advanced Configuration

```kotlin
// Configure SecurityGuard with custom settings
val config = SecurityGuardConfig(
    enableNotifications = true,      // Show notifications for events
    customAlarmSound = null,          // Custom alarm sound URI (null for default)
    alarmDuration = 5000L,            // Alarm duration in milliseconds
    motionSensitivity = 600,          // Motion detection sensitivity (100-1000)
    clapSensitivity = 6500,           // Clap detection sensitivity (5000-10000)
    lowBatteryThreshold = 15,         // Low battery percentage threshold
    vibrationEnabled = true           // Enable vibration with alarms
)

securityGuard.configure(config)
```

### Using Callbacks

```kotlin
// Set callback to receive security events
securityGuard.setCallback(object : SecurityGuardCallback {
    override fun onSecurityEvent(feature: SecurityFeature, message: String) {
        // Handle security event
        Log.d("SecurityGuard", "Event from ${feature.name}: $message")
    }
    
    override fun onSecurityError(feature: SecurityFeature, error: Throwable) {
        // Handle error
        Log.e("SecurityGuard", "Error in ${feature.name}", error)
    }
})
```

### Enable Multiple Features

```kotlin
// Enable multiple features at once
securityGuard.enableFeatures(
    SecurityFeature.CHARGER_DETECTION,
    SecurityFeature.MOTION_DETECTION,
    SecurityFeature.WIFI_MONITOR
)

// Disable multiple features
securityGuard.disableFeatures(
    SecurityFeature.CHARGER_DETECTION,
    SecurityFeature.MOTION_DETECTION
)

// Check if a feature is enabled
if (securityGuard.isFeatureEnabled(SecurityFeature.MOTION_DETECTION)) {
    // Feature is active
}

// Get all active features
val activeFeatures = securityGuard.getActiveFeatures()
```

## Available Security Features

```kotlin
enum class SecurityFeature {
    CHARGER_DETECTION,      // Detects charger connection/disconnection
    BATTERY_MONITOR,        // Monitors battery level
    WIFI_MONITOR,           // Monitors WiFi connection
    CLAP_DETECTION,         // Detects clapping sounds
    MOTION_DETECTION,       // Detects device motion
    PROXIMITY_DETECTION,    // Detects nearby objects
    HANDSFREE_DETECTION     // Detects headphone connection
}
```

## Runtime Permissions

Some features require runtime permissions:

### Microphone Permission (for Clap Detection)

```kotlin
// Request microphone permission
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
    != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.RECORD_AUDIO),
        MICROPHONE_PERMISSION_CODE
    )
}
```

### Notification Permission (Android 13+)

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_CODE
        )
    }
}
```

## Demo App

A demo application is included in the `app` module that demonstrates all features of the SecurityGuard library. The demo app provides:

- Toggle switches for each security feature
- Real-time status updates
- Permission request handling
- Example implementation of all features

To run the demo:
1. Open the project in Android Studio
2. Run the `app` module
3. Toggle features on/off to test them

## Building AAR

To build the library as an AAR file:

```bash
./gradlew :securityguard:assembleRelease
```

The AAR file will be generated at:
```
securityguard/build/outputs/aar/securityguard-release.aar
```

## Architecture

The library follows a clean architecture pattern:

```
securityguard/
├── api/                    # Public API classes
│   ├── SecurityGuard.kt    # Main API entry point
│   ├── SecurityFeature.kt  # Feature enum
│   ├── SecurityGuardConfig.kt    # Configuration data class
│   └── SecurityGuardCallback.kt  # Callback interface
├── services/               # Internal service implementations
│   ├── ChargerDetectionService.kt
│   ├── BatteryMonitorService.kt
│   ├── WifiMonitorService.kt
│   ├── ClapDetectionService.kt
│   ├── MotionDetectionService.kt
│   ├── ProximityDetectionService.kt
│   └── HandsfreeDetectionService.kt
└── helpers/                # Internal helper classes
    ├── NotificationHelper.kt
    ├── SoundManager.kt
    └── SecurityGuardPreferences.kt
```

## Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Language**: Kotlin
- **AndroidX**: Required

## ProGuard

If you're using ProGuard, the library includes consumer ProGuard rules that will be automatically applied.

## License

This library is provided as-is for use in your Android projects.

## Support

For issues, questions, or contributions, please refer to the project repository.

## Changelog

### Version 1.0.0
- Initial release
- Support for 7 security features
- Configurable sensitivity and thresholds
- Notification support
- Custom alarm sounds
- Comprehensive callback system

