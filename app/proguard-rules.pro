# TouchGrass Launch-Ready Security & Optimization Rules

# Strip all logging calls if any exist
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Preserve Accessibility Service & Device Admin entry points
-keep class com.touchgrass.app.service.FocusAccessibilityService { *; }
-keep class com.touchgrass.app.receiver.FocusDeviceAdminReceiver { *; }
-keep class com.touchgrass.app.receiver.BootReceiver { *; }
-keep class com.touchgrass.app.receiver.RecoveryReceiver { *; }

