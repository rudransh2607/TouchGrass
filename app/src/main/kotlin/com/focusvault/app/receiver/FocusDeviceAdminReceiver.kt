package com.focusvault.app.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.focusvault.app.data.FocusPreferences

/**
 * DeviceAdminReceiver that prevents unauthorized uninstallation of FocusVault.
 * While Device Admin is active, Android blocks standard app uninstallation until
 * the user manually deactivates the administrator.
 *
 * When an active focus session or passcode lock is running, Settings is blocked
 * by the AccessibilityService, and any disable request presents a warning.
 */
class FocusDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, FocusDeviceAdminReceiver::class.java)
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(
            context,
            "TouchGrass: Uninstall Protection (Device Admin) activated",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        val prefs = FocusPreferences(context)
        if (prefs.isLockActive()) {
            return "TouchGrass is actively locked! Go for a walk. Deactivation is prohibited until the focus session completes or is unlocked with your passcode."
        }
        return "Deactivating TouchGrass Device Admin will disable uninstallation protection."
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(
            context,
            "TouchGrass: Device Admin deactivated",
            Toast.LENGTH_SHORT
        ).show()
    }
}

