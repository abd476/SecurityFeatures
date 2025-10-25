# SecurityGuard - Build Instructions

This document provides instructions for building and using the SecurityGuard library.

## Project Structure

```
SecuirtyFeatures/
├── securityguard/          # Library module (produces AAR)
│   ├── src/
│   │   └── main/
│   │       ├── java/com/security/guard/
│   │       │   ├── api/           # Public API
│   │       │   ├── services/      # Service implementations
│   │       │   └── helpers/       # Helper classes
│   │       ├── res/
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── app/                    # Demo application
│   └── src/main/
│       ├── java/com/example/secuirtyfeatures/
│       │   └── MainActivity.kt
│       └── res/layout/
│           └── activity_main.xml
└── build.gradle.kts
```

## Building the Library (AAR)

### Using Gradle Command

To build the release AAR:

```bash
cd C:\Users\abdul\AndroidStudioProjects\SecuirtyFeatures
.\gradlew :securityguard:assembleRelease
```

The AAR file will be located at:
```
securityguard\build\outputs\aar\securityguard-release.aar
```

### Using Android Studio

1. Open the project in Android Studio
2. In the Gradle panel, navigate to: `securityguard > Tasks > build > assembleRelease`
3. Double-click to run
4. Find the AAR in `securityguard/build/outputs/aar/`

## Building Both Debug and Release

```bash
.\gradlew :securityguard:assemble
```

This will create both:
- `securityguard-debug.aar`
- `securityguard-release.aar`

## Running the Demo App

### From Android Studio
1. Open the project in Android Studio
2. Select `app` configuration from the run menu
3. Click Run (Shift+F10)

### From Command Line
```bash
.\gradlew :app:installDebug
```

## Clean Build

To clean and rebuild everything:

```bash
.\gradlew clean
.\gradlew :securityguard:assembleRelease
.\gradlew :app:assembleDebug
```

## Using the AAR in Another Project

### Method 1: Local AAR

1. Copy `securityguard-release.aar` to your project's `libs` folder
2. In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(files("libs/securityguard-release.aar"))
}
```

### Method 2: Local Maven

1. Publish to local Maven:
```bash
.\gradlew :securityguard:publishToMavenLocal
```

2. In your project's `settings.gradle.kts`:
```kotlin
repositories {
    mavenLocal()
}
```

3. In your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.security.guard:securityguard:1.0.0")
}
```

## Testing

### Manual Testing with Demo App

The demo app provides a comprehensive UI to test all features:

1. Build and install the demo app
2. Grant required permissions when prompted
3. Use the switches to enable/disable features
4. Test each feature:
   - **Charger Detection**: Unplug charger
   - **Battery Monitor**: Wait for battery level changes
   - **WiFi Monitor**: Toggle WiFi on/off
   - **Clap Detection**: Clap 2-3 times
   - **Motion Detection**: Move the device
   - **Proximity Detection**: Cover the proximity sensor
   - **Handsfree Detection**: Plug/unplug headphones

### Debugging

To see logs:

```bash
adb logcat | findstr /i "security"
```

Or filter by specific tags:
- `ChargerService`
- `BatteryService`
- `WifiService`
- `ClapService`
- `MotionService`
- `ProximityService`
- `HandsfreeService`

## Library Configuration

All library features can be configured via `SecurityGuardConfig`:

```kotlin
val config = SecurityGuardConfig(
    enableNotifications = true,      // Show notifications
    customAlarmSound = null,          // Custom alarm URI
    alarmDuration = 5000L,            // 5 seconds
    motionSensitivity = 600,          // 100-1000
    clapSensitivity = 6500,           // 5000-10000
    lowBatteryThreshold = 15,         // 15%
    vibrationEnabled = true           // Enable vibration
)

securityGuard.configure(config)
```

## ProGuard/R8

The library includes ProGuard rules. If you're using ProGuard/R8 in your app, the rules will be automatically applied via consumer ProGuard rules.

## Minimum Requirements

- **Android Studio**: Arctic Fox (2020.3.1) or higher
- **Gradle**: 7.0 or higher
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Kotlin**: 2.0.21
- **Java**: 11

## Troubleshooting

### Build Fails

1. Sync Gradle files: `File > Sync Project with Gradle Files`
2. Invalidate caches: `File > Invalidate Caches / Restart`
3. Clean build: `.\gradlew clean`

### Permission Issues

Make sure all required permissions are declared in `AndroidManifest.xml` and runtime permissions are requested for:
- `RECORD_AUDIO` (for Clap Detection)
- `POST_NOTIFICATIONS` (for Android 13+)

### Service Not Starting

Check Logcat for error messages. Common issues:
- Missing permissions
- Service already running
- Sensor not available (e.g., no proximity sensor on device)

## Distribution

### AAR Distribution

Distribute the `securityguard-release.aar` file with:
- README.md (usage documentation)
- QUICKSTART.md (quick start guide)
- Sample integration code

### Maven/Artifactory

To publish to a Maven repository, add publication configuration to `securityguard/build.gradle.kts`:

```kotlin
publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["release"])
            groupId = "com.security"
            artifactId = "guard"
            version = "1.0.0"
        }
    }
}
```

Then publish:
```bash
.\gradlew :securityguard:publish
```

## Version Information

- **Library Version**: 1.0.0
- **Package**: com.security.guard
- **Module Name**: securityguard

## Support

For issues or questions:
1. Check the demo app for working examples
2. Review the README.md and QUICKSTART.md
3. Check Logcat for error messages

