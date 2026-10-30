package com.focusvault.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.focusvault.app.data.FocusPreferences

/**
 * Emergency Recovery Receiver:
 * Protected by android.permission.DUMP so only ADB shell (USB/Wireless debugging)
 * can invoke it. Unauthorized third-party apps on the device cannot trigger it.
 */
class RecoveryReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_RESET_LOCK = "com.touchgrass.app.RESET_LOCK"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_RESET_LOCK) {
            val prefs = FocusPreferences(context)
            prefs.stopTimer()
            prefs.setPasscodeLockActive(false)
            Toast.makeText(context, "TouchGrass: Emergency reset executed via ADB.", Toast.LENGTH_SHORT).show()
        }
    }
}

