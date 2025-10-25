# SecurityGuard - Android Security Monitoring Library
[![](https://jitpack.io/v/abd476/SecurityFeatures.svg)](https://jitpack.io/#abd476/SecurityFeatures)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue.svg)](https://developer.android.com/about/versions/nougat)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org/)

**SecurityGuard** is a powerful, easy-to-use Android library that provides comprehensive security monitoring features with a single, clean API. Add advanced anti-theft, device protection, and phone finder capabilities to your Android app in minutes.

## Features

SecurityGuard provides 7 essential security monitoring features:

| Feature | Description | Use Case |
|---------|-------------|----------|
| **Charger Detection** | Alerts when charger is disconnected | Anti-theft protection while charging |
| **Battery Monitor** | Monitors battery level (full/low) | Battery management notifications |
| **WiFi Monitor** | Tracks WiFi connection changes | Network connectivity alerts |
| **Clap Detection** | Find phone by clapping | Locate misplaced phone |
| **Motion Detection** | Detects device movement | Anti-theft alarm system |
| **Proximity Detection** | Detects nearby objects | Pocket detection, unauthorized access |
| **Handsfree Detection** | Monitors headphone connection | Audio accessory tracking |

## Why SecurityGuard?

- **Simple API** - Single entry point for all features  
- **Plug & Play** - Works out of the box with sensible defaults  
- **Highly Configurable** - Customize sensitivity, sounds, and behavior  
- **Production Ready** - Proper error handling and resource management  
- **Lightweight** - Minimal dependencies, small footprint  
- **No Backend Required** - All processing happens on-device  

## Installation

Add it in your root settings.gradle at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
In your app's `build.gradle.kts`:

```kotlin

		dependencies {
	        implementation 'com.github.abd476:SecurityFeatures:v1.0.0'
	}
```

### Step 2: Add required permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<!-- Required for all features -->
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- For WiFi monitoring -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

<!-- For clap detection only -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### Step 3: Request runtime permissions

For Android 6.0+, request runtime permissions:

```kotlin
// For clap detection
ActivityCompat.requestPermissions(
    this,
    arrayOf(Manifest.permission.RECORD_AUDIO),
    PERMISSION_CODE
)

// For notifications (Android 13+)
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        NOTIFICATION_PERMISSION_CODE
    )
}
```

## Quick Start

### Basic Usage (3 lines of code)

```kotlin
// Initialize
val securityGuard = SecurityGuard.getInstance(context)

// Enable a feature
securityGuard.enableFeature(SecurityFeature.MOTION_DETECTION)

// That's it! Motion detection is now active
```

### Complete Example

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var securityGuard: SecurityGuard
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 1. Initialize SecurityGuard
        securityGuard = SecurityGuard.getInstance(this)
        
        // 2. Configure (optional)
        val config = SecurityGuardConfig(
            enableNotifications = true,
            alarmDuration = 5000L,
            motionSensitivity = 600,
            clapSensitivity = 6500,
            lowBatteryThreshold = 15,
            vibrationEnabled = true
        )
        securityGuard.configure(config)
        
        // 3. Set callback to receive events (optional)
        securityGuard.setCallback(object : SecurityGuardCallback {
            override fun onSecurityEvent(feature: SecurityFeature, message: String) {
                // Handle security event
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
            
            override fun onSecurityError(feature: SecurityFeature, error: Throwable) {
                // Handle error
                Log.e("Security", "Error: ${error.message}")
            }
        })
        
        // 4. Enable features
        securityGuard.enableFeature(SecurityFeature.CHARGER_DETECTION)
        securityGuard.enableFeature(SecurityFeature.MOTION_DETECTION)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop all features when done (optional)
        securityGuard.stopAll()
    }
}
```

## Real-World Use Cases

### Anti-Theft App

```kotlin
// Enable motion and charger detection
securityGuard.enableFeatures(
    SecurityFeature.MOTION_DETECTION,
    SecurityFeature.CHARGER_DETECTION
)

// Device will trigger alarm if:
// - Someone moves your phone
// - Charger is unplugged
```

### Phone Finder App

```kotlin
// Enable clap detection
securityGuard.enableFeature(SecurityFeature.CLAP_DETECTION)

// Users can find their phone by clapping 2-3 times
// Phone will play a loud alarm and light up the screen
```

### Battery Manager App

```kotlin
// Custom battery threshold
val config = SecurityGuardConfig(lowBatteryThreshold = 20)
securityGuard.configure(config)
securityGuard.enableFeature(SecurityFeature.BATTERY_MONITOR)

// Get alerts when battery reaches 20% or is fully charged
```

### Parental Control App

```kotlin
// Monitor connections and movements
securityGuard.enableFeatures(
    SecurityFeature.WIFI_MONITOR,
    SecurityFeature.HANDSFREE_DETECTION,
    SecurityFeature.MOTION_DETECTION
)

// Track when child connects/disconnects WiFi, headphones, etc.
```

## API Reference

### SecurityGuard (Main Class)

```kotlin
// Get singleton instance
SecurityGuard.getInstance(context: Context): SecurityGuard

// Configure settings
configure(config: SecurityGuardConfig)

// Set event callback
setCallback(callback: SecurityGuardCallback?)

// Enable/disable features
enableFeature(feature: SecurityFeature): Boolean
disableFeature(feature: SecurityFeature)
enableFeatures(vararg features: SecurityFeature)
disableFeatures(vararg features: SecurityFeature)

// Check status
isFeatureEnabled(feature: SecurityFeature): Boolean
getActiveFeatures(): Set<SecurityFeature>

// Stop all features
stopAll()
```

### SecurityFeature (Enum)

```kotlin
enum class SecurityFeature {
    CHARGER_DETECTION,
    BATTERY_MONITOR,
    WIFI_MONITOR,
    CLAP_DETECTION,
    MOTION_DETECTION,
    PROXIMITY_DETECTION,
    HANDSFREE_DETECTION
}
```

### SecurityGuardConfig (Configuration)

```kotlin
data class SecurityGuardConfig(
    val enableNotifications: Boolean = true,          // Show notifications
    val customAlarmSound: Uri? = null,                // Custom alarm (null = default)
    val alarmDuration: Long = 5000L,                  // Alarm length in ms
    val motionSensitivity: Int = 600,                 // Motion threshold (100-1000)
    val clapSensitivity: Int = 6500,                  // Clap threshold (5000-10000)
    val lowBatteryThreshold: Int = 15,                // Battery % (0-100)
    val vibrationEnabled: Boolean = true              // Enable vibration
)
```

### SecurityGuardCallback (Interface)

```kotlin
interface SecurityGuardCallback {
    // Called when security event occurs
    fun onSecurityEvent(feature: SecurityFeature, message: String)
    
    // Called when error occurs (optional override)
    fun onSecurityError(feature: SecurityFeature, error: Throwable)
}
```

## Configuration Options

### Customize Motion Sensitivity

```kotlin
val config = SecurityGuardConfig(
    motionSensitivity = 400  // Lower = more sensitive (100-1000)
)
securityGuard.configure(config)
```

### Customize Clap Detection

```kotlin
val config = SecurityGuardConfig(
    clapSensitivity = 7000  // Higher = less sensitive (5000-10000)
)
securityGuard.configure(config)
```

### Custom Alarm Sound

```kotlin
val customSound = Uri.parse("android.resource://your.package/raw/custom_alarm")
val config = SecurityGuardConfig(
    customAlarmSound = customSound,
    alarmDuration = 8000L  // 8 seconds
)
securityGuard.configure(config)
```

### Disable Notifications

```kotlin
val config = SecurityGuardConfig(
    enableNotifications = false  // No notifications, only callbacks
)
securityGuard.configure(config)
```

## Control Features Dynamically

```kotlin
// Check if feature is active
if (securityGuard.isFeatureEnabled(SecurityFeature.MOTION_DETECTION)) {
    // Feature is running
}

// Get all active features
val activeFeatures = securityGuard.getActiveFeatures()
Log.d("Security", "Active features: ${activeFeatures.size}")

// Enable multiple features
securityGuard.enableFeatures(
    SecurityFeature.CHARGER_DETECTION,
    SecurityFeature.BATTERY_MONITOR,
    SecurityFeature.WIFI_MONITOR
)

// Disable multiple features
securityGuard.disableFeatures(
    SecurityFeature.MOTION_DETECTION,
    SecurityFeature.PROXIMITY_DETECTION
)

// Stop everything
securityGuard.stopAll()
```

## Privacy & Security

- **No Internet Required** - All processing happens locally  
- **No Data Collection** - Zero telemetry or analytics  
- **No External Dependencies** - Only uses AndroidX libraries  
- **Permissions Justified** - Each permission has a clear purpose  
- **Open Source** - Code is transparent and auditable  

## Requirements

- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **Language**: Kotlin 2.0.21
- **Dependencies**: AndroidX Core, AppCompat, Material Components

## Troubleshooting

### Feature not working?

1. **Check permissions are granted** (especially RECORD_AUDIO for clap detection)
2. **Verify feature is enabled**: `isFeatureEnabled(feature)`
3. **Check device compatibility** (some devices lack certain sensors)
4. **Enable logging** to see error messages in Logcat

### No alarm sound?

1. **Check device volume** is not muted
2. **Grant notification permission** (Android 13+)
3. **Verify alarm sound file** if using custom sound
4. **Try default sound** by setting `customAlarmSound = null`

### High battery usage?

1. **Disable unused features**
2. **Increase sensitivity thresholds** (less triggers = less battery)
3. **Reduce alarm duration**
4. **Stop features when app is in background**

## Sample App

A complete demo application is included in this repository. To run it:

1. Clone the repository
2. Open in Android Studio
3. Run the `app` module
4. Test all features with toggle switches

The demo app demonstrates:
- All 7 security features
- Runtime permission handling
- Custom configuration
- Event callbacks
- Modern Material Design UI

## Version History

### 1.0.0 (Current)
- Initial release
- 7 security features
- Configurable settings
- Callback support
- Production ready

## License

```
MIT License

Copyright (c) 2025 SecurityGuard

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## Support

- **Documentation**: See `QUICKSTART.md` for quick start guide
- **Build Instructions**: See `BUILD_INSTRUCTIONS.md` for build guide
- **Examples**: Check the demo app in the `app` module
- **Technical Details**: See `LIBRARY_OVERVIEW.md` for architecture

## Acknowledgments

Built with modern Android development best practices and designed for easy integration into any Android application.

---

**SecurityGuard - Simple, Powerful, Production-Ready**

Package: `com.security.guard`  
Version: 1.0.0  
License: MIT
