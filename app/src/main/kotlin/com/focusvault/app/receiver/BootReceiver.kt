package com.focusvault.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Ensures lock state and configurations persist across reboots.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Persistent timestamp logic in FocusPreferences automatically maintains
            // lock enforcement after reboot without special intervention.
        }
    }
}
