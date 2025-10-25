# SecurityGuard - Quick Start Guide

Get started with SecurityGuard in 5 minutes!

## Step 1: Add Library to Your Project

In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":securityguard"))
}
```

## Step 2: Add Permissions

In your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Step 3: Initialize in Your Activity

```kotlin
import com.security.guard.api.SecurityGuard
import com.security.guard.api.SecurityFeature

class MainActivity : AppCompatActivity() {
    
    private lateinit var securityGuard: SecurityGuard
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize
        securityGuard = SecurityGuard.getInstance(this)
        
        // Enable features
        securityGuard.enableFeature(SecurityFeature.CHARGER_DETECTION)
        securityGuard.enableFeature(SecurityFeature.MOTION_DETECTION)
    }
}
```

## Step 4: (Optional) Handle Callbacks

```kotlin
import com.security.guard.api.SecurityGuardCallback

securityGuard.setCallback(object : SecurityGuardCallback {
    override fun onSecurityEvent(feature: SecurityFeature, message: String) {
        // Handle event
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
})
```

## Step 5: Request Runtime Permissions

For features that need runtime permissions (like Clap Detection):

```kotlin
// Request microphone permission for clap detection
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
    != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.RECORD_AUDIO),
        PERMISSION_CODE
    )
}

// After permission is granted:
securityGuard.enableFeature(SecurityFeature.CLAP_DETECTION)
```

## Common Use Cases

### Anti-Theft Mode
```kotlin
// Enable motion and charger detection for anti-theft
securityGuard.enableFeatures(
    SecurityFeature.MOTION_DETECTION,
    SecurityFeature.CHARGER_DETECTION
)
```

### Find My Phone
```kotlin
// Enable clap detection to find phone
securityGuard.enableFeature(SecurityFeature.CLAP_DETECTION)
// Now just clap 2-3 times and your phone will make a sound!
```

### Battery Management
```kotlin
// Monitor battery level
securityGuard.enableFeature(SecurityFeature.BATTERY_MONITOR)
// Get notified when battery is full or low
```

### Network Monitoring
```kotlin
// Monitor WiFi connection
securityGuard.enableFeature(SecurityFeature.WIFI_MONITOR)
// Get notified of WiFi connection changes
```

## Customization

```kotlin
import com.security.guard.api.SecurityGuardConfig

// Customize behavior
val config = SecurityGuardConfig(
    enableNotifications = true,
    alarmDuration = 5000L,
    motionSensitivity = 600,  // 100-1000, higher = less sensitive
    clapSensitivity = 6500,   // 5000-10000, higher = less sensitive
    lowBatteryThreshold = 15   // Percentage (0-100)
)

securityGuard.configure(config)
```

## Stopping Features

```kotlin
// Stop a single feature
securityGuard.disableFeature(SecurityFeature.MOTION_DETECTION)

// Stop all features
securityGuard.stopAll()
```

## That's It!

You're now ready to use SecurityGuard in your app. Check the full README.md for more advanced features and configuration options.

## Need Help?

- See the demo app in the `app` module for a complete working example
- Check README.md for detailed documentation
- All features work out of the box with sensible defaults

