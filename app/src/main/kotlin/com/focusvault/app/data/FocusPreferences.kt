package com.focusvault.app.data

import android.content.Context
import android.content.SharedPreferences
import com.focusvault.app.util.SecurityUtils

class FocusPreferences(val context: Context, prefsName: String = PREFS_NAME) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "focus_vault_preferences"
        private const val KEY_FILTER_MODE = "filter_mode"
        private const val KEY_BLOCKED_PACKAGES = "blocked_packages"
        private const val KEY_ALLOWED_PACKAGES = "allowed_packages"
        private const val KEY_PASSCODE_HASH = "passcode_sha256_hash"
        private const val KEY_PASSCODE_LOCK_ACTIVE = "passcode_lock_active"
        private const val KEY_TIMER_END_MILLIS = "timer_end_timestamp_millis"
        private const val KEY_TIMER_DURATION_MINUTES = "timer_duration_minutes"
        private const val KEY_LAST_SESSION_DURATION_MINUTES = "last_session_duration_minutes"
        private const val KEY_TARGET_UNTIL_TIME = "target_until_time_string"

        // Default selections: empty set by default so only user-selected apps are restricted/allowed
        val DEFAULT_BLOCKED_PACKAGES = emptySet<String>()
        val DEFAULT_ESSENTIAL_PACKAGES = emptySet<String>()

        // System core packages that must NEVER be blocked under any filter mode
        val ESSENTIAL_SYSTEM_PACKAGES = setOf(
            "android",
            "com.android.systemui",
            "com.google.android.ext.services",
            "com.android.ext.services",
            "android.ext.services",
            "com.google.android.gms",
            "com.google.android.gsf",
            "com.android.permissioncontroller",
            "com.google.android.permissioncontroller",
            "com.android.packageinstaller",
            "com.google.android.packageinstaller",
            "com.android.phone",
            "com.android.incallui",
            "com.google.android.dialer",
            "com.android.dialer",
            "com.samsung.android.dialer",
            "com.samsung.android.incallui",
            "com.android.server.telecom",
            "com.android.emergency",
            "com.google.android.emergency"
        )

        fun isEssentialSystemPackage(packageName: String): Boolean {
            val lower = packageName.lowercase()
            if (lower in ESSENTIAL_SYSTEM_PACKAGES) return true
            if (lower.startsWith("com.android.providers.") || lower.startsWith("com.google.android.providers.")) return true
            if (lower.contains("inputmethod") || lower.contains("keyboard") || lower.contains("gboard") || lower.contains("swiftkey") || lower.contains("honeyboard")) return true
            if (lower.startsWith("com.oneplus.") || lower.startsWith("com.oplus.") || lower.startsWith("com.coloros.")) {
                if (lower.contains("incall") || lower.contains("biometric") || lower.contains("systemui") || lower.contains("keyguard") || lower.contains("service")) {
                    return true
                }
            }
            return false
        }
    }

    /**
     * Filter Mode: BLOCKLIST (Default) or WHITELIST
     */
    fun getFilterMode(): FilterMode {
        val stored = prefs.getString(KEY_FILTER_MODE, FilterMode.BLOCKLIST.name)
        return try {
            FilterMode.valueOf(stored ?: FilterMode.BLOCKLIST.name)
        } catch (_: Exception) {
            FilterMode.BLOCKLIST
        }
    }

    fun setFilterMode(mode: FilterMode) {
        prefs.edit().putString(KEY_FILTER_MODE, mode.name).apply()
    }

    /**
     * Blocklist Mode: Set of applications to actively block during focus sessions.
     */
    fun getBlockedPackages(): Set<String> {
        val stored = prefs.getStringSet(KEY_BLOCKED_PACKAGES, null)
        return if (stored != null) {
            HashSet(stored)
        } else {
            emptySet()
        }
    }

    fun setBlockedPackages(packages: Set<String>) {
        prefs.edit().putStringSet(KEY_BLOCKED_PACKAGES, HashSet(packages)).apply()
    }

    fun isAppBlocked(packageName: String): Boolean {
        return getBlockedPackages().contains(packageName)
    }

    fun toggleAppBlocked(packageName: String): Boolean {
        val current = HashSet(getBlockedPackages())
        val newlyBlocked = if (current.contains(packageName)) {
            current.remove(packageName)
            false
        } else {
            current.add(packageName)
            true
        }
        setBlockedPackages(current)
        return newlyBlocked
    }

    /**
     * Whitelist Mode: Set of user-whitelisted allowed packages.
     */
    fun getAllowedPackages(): Set<String> {
        val stored = prefs.getStringSet(KEY_ALLOWED_PACKAGES, null)
        return if (stored != null) {
            HashSet(stored)
        } else {
            emptySet()
        }
    }

    fun setAllowedPackages(packages: Set<String>) {
        prefs.edit().putStringSet(KEY_ALLOWED_PACKAGES, HashSet(packages)).apply()
    }

    fun isAppAllowed(packageName: String): Boolean {
        return getAllowedPackages().contains(packageName)
    }

    fun toggleAppAllowed(packageName: String): Boolean {
        val current = HashSet(getAllowedPackages())
        val newlyAllowed = if (current.contains(packageName)) {
            current.remove(packageName)
            false
        } else {
            current.add(packageName)
            true
        }
        setAllowedPackages(current)
        return newlyAllowed
    }

    /**
     * Checks if the package is prohibited under the active filter mode.
     * Core system components, keyboards, and FocusVault itself can NEVER be restricted.
     */
    fun isAppRestricted(packageName: String): Boolean {
        if (packageName == context.packageName || packageName == "com.touchgrass.app" || packageName == "com.focusvault.app" || isEssentialSystemPackage(packageName)) {
            return false
        }
        return when (getFilterMode()) {
            FilterMode.BLOCKLIST -> isAppBlocked(packageName)
            FilterMode.WHITELIST -> !isAppAllowed(packageName)
        }
    }

    /**
     * Hashes the passcode using SHA-256 and stores the hex digest.
     * The plaintext passcode is NEVER stored.
     */
    fun setPasscode(passcode: String) {
        val hash = SecurityUtils.hashSha256(passcode.trim())
        prefs.edit().putString(KEY_PASSCODE_HASH, hash).apply()
    }

    fun hasPasscode(): Boolean {
        return !prefs.getString(KEY_PASSCODE_HASH, null).isNullOrBlank()
    }

    fun getPasscodeHash(): String? {
        return prefs.getString(KEY_PASSCODE_HASH, null)
    }

    fun clearPasscode() {
        prefs.edit().remove(KEY_PASSCODE_HASH).apply()
    }

    fun verifyPasscode(candidatePasscode: String): Boolean {
        val storedHash = getPasscodeHash() ?: return false
        return SecurityUtils.verifyPasscode(candidatePasscode.trim(), storedHash)
    }

    /**
     * Passcode Lock state: separate toggleable lock mode.
     */
    fun isPasscodeLockActive(): Boolean {
        return prefs.getBoolean(KEY_PASSCODE_LOCK_ACTIVE, false)
    }

    fun setPasscodeLockActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_PASSCODE_LOCK_ACTIVE, active).apply()
    }

    /**
     * Timer Lock state: active countdown until timestamp.
     */
    fun startTimer(durationMinutes: Int, untilTimeString: String? = null) {
        val endMillis = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        val editor = prefs.edit()
            .putLong(KEY_TIMER_END_MILLIS, endMillis)
            .putInt(KEY_TIMER_DURATION_MINUTES, durationMinutes)
            .putInt(KEY_LAST_SESSION_DURATION_MINUTES, durationMinutes)
        if (untilTimeString != null) {
            editor.putString(KEY_TARGET_UNTIL_TIME, untilTimeString)
        } else {
            editor.remove(KEY_TARGET_UNTIL_TIME)
        }
        editor.apply()
    }

    fun stopTimer() {
        prefs.edit()
            .putLong(KEY_TIMER_END_MILLIS, 0L)
            .remove(KEY_TARGET_UNTIL_TIME)
            .apply()
    }

    fun getTargetUntilTime(): String? {
        return prefs.getString(KEY_TARGET_UNTIL_TIME, null)
    }

    fun getTimerEndMillis(): Long {
        return prefs.getLong(KEY_TIMER_END_MILLIS, 0L)
    }

    fun getTimerDurationMinutes(): Int {
        return prefs.getInt(KEY_LAST_SESSION_DURATION_MINUTES, 60)
    }

    fun setTimerDurationMinutes(minutes: Int) {
        prefs.edit()
            .putInt(KEY_LAST_SESSION_DURATION_MINUTES, minutes)
            .putInt(KEY_TIMER_DURATION_MINUTES, minutes)
            .apply()
    }

    fun isTimerActive(): Boolean {
        val endMillis = getTimerEndMillis()
        return endMillis > 0L && System.currentTimeMillis() < endMillis
    }

    fun getRemainingSeconds(): Long {
        val endMillis = getTimerEndMillis()
        val remainingMillis = endMillis - System.currentTimeMillis()
        return if (remainingMillis > 0L) remainingMillis / 1000L else 0L
    }

    /**
     * Primary Lock check: returns true if either the duration lock timer
     * or the separate passcode lock is actively engaged.
     */
    fun isLockActive(): Boolean {
        return isTimerActive() || isPasscodeLockActive()
    }
}
