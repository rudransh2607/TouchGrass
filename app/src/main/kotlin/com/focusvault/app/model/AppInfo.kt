package com.focusvault.app.model

/**
 * Represents an installed application available for focus rules.
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val isBlocked: Boolean = false,
    val isAllowed: Boolean = false,
    val isSystemApp: Boolean = false
)
