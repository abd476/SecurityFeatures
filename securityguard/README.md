# SecurityGuard Library Module

This is the main library module for SecurityGuard - a comprehensive Android security monitoring library.

## Package Structure

```
com.security.guard/
├── api/                    # Public API
│   ├── SecurityGuard       # Main singleton API class
│   ├── SecurityFeature     # Feature enum
│   ├── SecurityGuardConfig # Configuration
│   └── SecurityGuardCallback # Event callback interface
├── services/               # Internal services
│   └── [7 service implementations]
└── helpers/                # Internal helpers
    ├── NotificationHelper
    ├── SoundManager
    └── SecurityGuardPreferences
```

## Public API

Only classes in the `api` package are considered public API:

- `SecurityGuard` - Main entry point for the library
- `SecurityFeature` - Enum of available features
- `SecurityGuardConfig` - Configuration data class
- `SecurityGuardCallback` - Callback interface for events

All other classes are internal implementation details and may change without notice.

## Building

To build the library:

```bash
./gradlew :securityguard:assembleRelease
```

Output: `build/outputs/aar/securityguard-release.aar`

## Usage

See the main README.md in the project root for complete usage documentation.

## Development

When modifying the library:

1. Keep the public API stable and backward compatible
2. Mark internal classes as `internal` when possible
3. Update ProGuard rules if needed
4. Update the main README with any API changes
5. Test all features with the demo app

## Testing

The demo app in the `app` module serves as integration tests for all library features.

