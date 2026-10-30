package com.focusvault.app.service

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.focusvault.app.data.FocusPreferences

/**
 * FocusAccessibilityService:
 * Intercepts newly opened app packages, checks them against the user-defined whitelist,
 * and forces unauthorized applications and Android Settings back to the home screen
 * whenever a lock timer or separate passcode lock is active.
 *
 * Essential services (input keyboards like Gboard, incoming/emergency phone calls,
 * and system UI dialogs) are always permitted so the device remains functional and
 * unlockable.
 */
class FocusAccessibilityService : AccessibilityService() {

    private lateinit var prefs: FocusPreferences
    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastBlockedTimestamp: Long = 0L
    private var cachedLauncherPackage: String? = null
    private val launchableCache = java.util.concurrent.ConcurrentHashMap<String, Boolean>()

    companion object {
        @Volatile
        var isServiceRunning: Boolean = false
            private set

        /**
         * Checks whether FocusAccessibilityService is enabled in Android Accessibility settings.
         */
        fun isEnabled(context: Context): Boolean {
            val expectedComponent = ComponentName(context, FocusAccessibilityService::class.java).flattenToString()
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)
            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                if (componentNameString.equals(expectedComponent, ignoreCase = true) ||
                    componentNameString.contains(context.packageName)
                ) {
                    return true
                }
            }
            return false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isServiceRunning = true
        prefs = FocusPreferences(this)
        resolveLauncherPackage()
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
    }

    override fun onInterrupt() {
        // Accessibility service interrupted
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // We monitor window state changes to detect app switches
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOWS_CHANGED
        ) {
            return
        }

        val targetPackage = event.packageName?.toString() ?: return
        if (targetPackage.isBlank()) return

        if (!::prefs.isInitialized) {
            prefs = FocusPreferences(this)
        }

        // Check if either a duration timer or separate passcode lock is active
        if (!prefs.isLockActive()) {
            return
        }

        // 1. Never block FocusVault itself so the user can inspect status or unlock
        if (targetPackage == packageName) {
            return
        }

        // 2. Never block Input Method Editors (keyboards like Gboard, SwiftKey, etc.)
        // This ensures the user can always type their passcode to unlock!
        if (isInputMethod(targetPackage)) {
            return
        }

        // 3. Never block essential system components (emergency dialer, phone calls, system UI, Android Services Library)
        if (FocusPreferences.isEssentialSystemPackage(targetPackage) || isEssentialSystemPackage(targetPackage)) {
            return
        }

        // 4. Never block the System Home Launcher
        if (isLauncherPackage(targetPackage)) {
            return
        }

        // 5. If FocusVault is the active foreground window, do not block child/sub-windows (Autofill, clipboard)
        val activeWindowPackage = try { rootInActiveWindow?.packageName?.toString() } catch (_: Exception) { null }
        if (activeWindowPackage == packageName && !isSettingsPackage(targetPackage)) {
            return
        }

        // 6. Block if it is Android Settings (to prevent tampering)
        val isSettings = isSettingsPackage(targetPackage)
        if (isSettings) {
            blockApp(targetPackage, isSettings = true)
            return
        }

        // 7. Non-launchable system components, dialogs, and background services can never be blocked
        if (!isLaunchableApp(targetPackage)) {
            return
        }

        // 8. Block if app is restricted under current mode (Whitelist or Blocklist)
        if (prefs.isAppRestricted(targetPackage)) {
            blockApp(targetPackage, isSettings = false)
        }
    }

    /**
     * Checks if the package belongs to an active or installed Input Method (keyboard).
     * Vital for typing passcodes in unlock dialogs.
     */
    private fun isInputMethod(pkg: String): Boolean {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val enabledIMEs = imm?.enabledInputMethodList
            if (enabledIMEs != null && enabledIMEs.any { it.packageName == pkg }) {
                return true
            }
        } catch (_: Exception) {}

        // Fallback check for common keyboard identifiers
        val lower = pkg.lowercase()
        return lower.contains("inputmethod") ||
                lower.contains("keyboard") ||
                lower.contains("gboard") ||
                lower.contains("swiftkey") ||
                lower.contains("honeyboard")
    }

    private fun isLaunchableApp(packageName: String): Boolean {
        return launchableCache.computeIfAbsent(packageName) { pkg ->
            try {
                packageManager.getLaunchIntentForPackage(pkg) != null
            } catch (_: Exception) {
                false
            }
        }
    }

    /**
     * Checks if the package is a vital core system service (phone calls, emergency, UI, services library).
     */
    private fun isEssentialSystemPackage(pkg: String): Boolean {
        if (FocusPreferences.isEssentialSystemPackage(pkg)) {
            return true
        }

        val essentialPrefixes = listOf(
            "com.android.phone",
            "com.android.incallui",
            "com.google.android.dialer",
            "com.android.dialer",
            "com.samsung.android.dialer",
            "com.samsung.android.incallui",
            "com.android.server.telecom",
            "com.android.emergency",
            "com.google.android.emergency",
            "com.android.permissioncontroller",
            "com.google.android.permissioncontroller",
            "com.google.android.packageinstaller",
            "com.android.packageinstaller",
            "com.google.android.ext.services",
            "com.android.ext.services",
            "android.ext.services",
            "com.google.android.gms",
            "com.google.android.gsf"
        )
        if (essentialPrefixes.any { pkg.equals(it, ignoreCase = true) || pkg.startsWith("$it.") }) {
            return true
        }

        // OEM specific overlays (OnePlus / Oppo / ColorOS)
        if (pkg.startsWith("com.oneplus.") || pkg.startsWith("com.oplus.") || pkg.startsWith("com.coloros.")) {
            if (pkg.contains("incall") || pkg.contains("biometric") || pkg.contains("systemui") || pkg.contains("keyguard") || pkg.contains("service")) {
                return true
            }
        }

        return false
    }

    /**
     * Instantly forces the unauthorized app or Settings back to the home screen.
     */
    private fun blockApp(targetPackage: String, isSettings: Boolean) {
        // Action: immediately press Home via accessibility global action
        performGlobalAction(GLOBAL_ACTION_HOME)

        // Debounce user toast feedback to avoid spamming the screen
        val now = System.currentTimeMillis()
        if (now - lastBlockedTimestamp > 2500L) {
            lastBlockedTimestamp = now
            val appLabel = getAppLabel(targetPackage)
            val message = if (isSettings) {
                "TouchGrass: Android Settings is locked to prevent tampering during focus sessions."
            } else {
                "TouchGrass: \"$appLabel\" is restricted. Go for a walk!"
            }

            mainHandler.post {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isSettingsPackage(pkg: String): Boolean {
        return pkg.equals("com.android.settings", ignoreCase = true) ||
                pkg.equals("com.google.android.settings", ignoreCase = true) ||
                pkg.endsWith(".settings")
    }

    private fun isLauncherPackage(pkg: String): Boolean {
        if (pkg == cachedLauncherPackage) return true

        // Refresh and check current default launcher
        val currentLauncher = resolveLauncherPackage()
        if (pkg == currentLauncher) return true

        val commonLaunchers = setOf(
            "com.google.android.apps.nexuslauncher",
            "com.sec.android.app.launcher",
            "com.android.launcher3",
            "com.miui.home",
            "com.oppo.launcher",
            "com.huawei.android.launcher",
            "com.oneplus.launcher",
            "com.teslacoilsw.launcher"
        )
        return commonLaunchers.contains(pkg)
    }

    private fun resolveLauncherPackage(): String? {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }
            val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            val launcherPkg = resolveInfo?.activityInfo?.packageName
            if (!launcherPkg.isNullOrBlank()) {
                cachedLauncherPackage = launcherPkg
                return launcherPkg
            }
        } catch (_: Exception) {
        }
        return cachedLauncherPackage
    }

    private fun getAppLabel(pkg: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(pkg, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            pkg
        }
    }
}
