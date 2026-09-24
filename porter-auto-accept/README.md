# Porter Auto Accept

Standalone Android utility for authorized personal/fleet use with the Porter driver app.

Features:
- Porter package targeting
- Minimum fare filter
- Maximum pickup distance
- Minimum drop distance
- Optional allowed-area filter
- Accessibility Service
- Rightward swipe-to-accept gesture
- No subscription or premium backend

The app is independent of FastClicker and does not bypass FastClicker's licensing.

## Build
The GitHub Actions workflow builds a debug APK and publishes it as an artifact.

## Important
UI automation depends on the current Porter app UI. Test with auto-accept OFF first and verify the detected slider position before enabling live acceptance.
