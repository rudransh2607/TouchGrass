package com.focusvault.app.ui.main

import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusvault.app.data.FilterMode
import com.focusvault.app.data.FocusPreferences
import com.focusvault.app.model.AppInfo
import com.focusvault.app.receiver.FocusDeviceAdminReceiver
import com.focusvault.app.service.FocusAccessibilityService
import com.focusvault.app.util.AppIconLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface DialogState {
    data object None : DialogState
    data object SetPasscode : DialogState
    data object ConfirmInstantLock : DialogState
    data class UnlockAction(
        val actionDescription: String,
        val isEarlyStop: Boolean = false,
        val onUnlocked: () -> Unit
    ) : DialogState
}

enum class TimeSelectionMode {
    DURATION,
    UNTIL_TIME
}

class FocusVaultViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = FocusPreferences(application)
    private val dpm = application.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val adminComponent = FocusDeviceAdminReceiver.getComponentName(application)

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _filterMode = MutableStateFlow(FilterMode.BLOCKLIST)
    val filterMode: StateFlow<FilterMode> = _filterMode.asStateFlow()

    private val _blockedPackages = MutableStateFlow<Set<String>>(emptySet())
    val blockedPackages: StateFlow<Set<String>> = _blockedPackages.asStateFlow()

    private val _allowedPackages = MutableStateFlow<Set<String>>(emptySet())
    val allowedPackages: StateFlow<Set<String>> = _allowedPackages.asStateFlow()

    private val _isTimerActive = MutableStateFlow(false)
    val isTimerActive: StateFlow<Boolean> = _isTimerActive.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private val _selectedDurationMinutes = MutableStateFlow(60)
    val selectedDurationMinutes: StateFlow<Int> = _selectedDurationMinutes.asStateFlow()

    private val _isPasscodeLockActive = MutableStateFlow(false)
    val isPasscodeLockActive: StateFlow<Boolean> = _isPasscodeLockActive.asStateFlow()

    private val _hasPasscode = MutableStateFlow(false)
    val hasPasscode: StateFlow<Boolean> = _hasPasscode.asStateFlow()

    private val _isAccessibilityEnabled = MutableStateFlow(false)
    val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled.asStateFlow()

    private val _isDeviceAdminActive = MutableStateFlow(false)
    val isDeviceAdminActive: StateFlow<Boolean> = _isDeviceAdminActive.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.None)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private val _timeSelectionMode = MutableStateFlow(TimeSelectionMode.DURATION)
    val timeSelectionMode: StateFlow<TimeSelectionMode> = _timeSelectionMode.asStateFlow()

    private val _targetUntilTimeString = MutableStateFlow<String?>(null)
    val targetUntilTimeString: StateFlow<String?> = _targetUntilTimeString.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        refreshAll()
        startTimerTicker()
        loadInstalledApps()
    }

    fun refreshAll() {
        val context = getApplication<Application>()
        _filterMode.value = prefs.getFilterMode()
        _blockedPackages.value = prefs.getBlockedPackages()
        _allowedPackages.value = prefs.getAllowedPackages()
        _isTimerActive.value = prefs.isTimerActive()
        _remainingSeconds.value = prefs.getRemainingSeconds()
        _selectedDurationMinutes.value = prefs.getTimerDurationMinutes()
        _targetUntilTimeString.value = prefs.getTargetUntilTime()
        _isPasscodeLockActive.value = prefs.isPasscodeLockActive()
        _hasPasscode.value = prefs.hasPasscode()
        _isAccessibilityEnabled.value = FocusAccessibilityService.isEnabled(context)
        _isDeviceAdminActive.value = dpm.isAdminActive(adminComponent)
    }

    private fun startTimerTicker() {
        viewModelScope.launch {
            while (isActive) {
                val timerActive = prefs.isTimerActive()
                _isTimerActive.value = timerActive
                _remainingSeconds.value = prefs.getRemainingSeconds()
                _isPasscodeLockActive.value = prefs.isPasscodeLockActive()

                delay(1000L)
            }
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val currentBlocked = prefs.getBlockedPackages()
            val currentAllowed = prefs.getAllowedPackages()
            val currentMode = prefs.getFilterMode()
            val ownPkg = context.packageName

            val apps = resolveInfos.mapNotNull { resolveInfo ->
                val pkg = resolveInfo.activityInfo.packageName
                if (pkg == ownPkg) return@mapNotNull null
                val label = resolveInfo.loadLabel(pm).toString()
                val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                AppInfo(
                    packageName = pkg,
                    appName = label,
                    isBlocked = currentBlocked.contains(pkg),
                    isAllowed = currentAllowed.contains(pkg),
                    isSystemApp = isSystem
                )
            }.distinctBy { it.packageName }
                .sortedWith(
                    if (currentMode == FilterMode.BLOCKLIST) {
                        compareByDescending<AppInfo> { it.isBlocked }.thenBy { it.appName.lowercase() }
                    } else {
                        compareByDescending<AppInfo> { it.isAllowed }.thenBy { it.appName.lowercase() }
                    }
                )

            _installedApps.value = apps

            // Sync actual installed blocked/allowed counts and prune uninstalled ghost packages
            val actualInstalledBlocked = currentBlocked.filter { pkg -> apps.any { it.packageName == pkg } }.toSet()
            val actualInstalledAllowed = currentAllowed.filter { pkg -> apps.any { it.packageName == pkg } }.toSet()
            if (actualInstalledBlocked.size != currentBlocked.size) {
                prefs.setBlockedPackages(actualInstalledBlocked)
            }
            if (actualInstalledAllowed.size != currentAllowed.size) {
                prefs.setAllowedPackages(actualInstalledAllowed)
            }
            _blockedPackages.value = actualInstalledBlocked
            _allowedPackages.value = actualInstalledAllowed

            // High performance pre-warming: pre-decode icons for top apps on background thread
            apps.take(40).forEach { app ->
                AppIconLoader.loadIcon(context, app.packageName)
            }
        }
    }

    fun requestSetFilterMode(newMode: FilterMode) {
        if (_filterMode.value == newMode) return
        if (prefs.isLockActive() && prefs.hasPasscode()) {
            _dialogState.value = DialogState.UnlockAction("Switch App Rule Mode") {
                setFilterModeInternal(newMode)
            }
        } else {
            setFilterModeInternal(newMode)
        }
    }

    private fun setFilterModeInternal(newMode: FilterMode) {
        prefs.setFilterMode(newMode)
        _filterMode.value = newMode
        // Re-sort apps based on new active criteria
        _installedApps.value = _installedApps.value.sortedWith(
            if (newMode == FilterMode.BLOCKLIST) {
                compareByDescending<AppInfo> { it.isBlocked }.thenBy { it.appName.lowercase() }
            } else {
                compareByDescending<AppInfo> { it.isAllowed }.thenBy { it.appName.lowercase() }
            }
        )
        showMessage("Switched to ${if (newMode == FilterMode.BLOCKLIST) "Blocklist Mode (Default)" else "Whitelist Mode"}")
    }

    fun setDurationMinutes(minutes: Int) {
        val clamped = minutes.coerceIn(1, 720)
        _selectedDurationMinutes.value = clamped
        prefs.setTimerDurationMinutes(clamped)
    }

    fun adjustDuration(deltaMinutes: Int) {
        setDurationMinutes(_selectedDurationMinutes.value + deltaMinutes)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showSetPasscodeDialog() {
        _dialogState.value = DialogState.SetPasscode
    }

    fun dismissDialog() {
        _dialogState.value = DialogState.None
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun savePasscode(newPasscode: String) {
        if (newPasscode.isBlank()) {
            showMessage("Passcode cannot be empty")
            return
        }
        prefs.setPasscode(newPasscode)
        _hasPasscode.value = true
        _dialogState.value = DialogState.None
        showMessage("Passcode successfully hashed (SHA-256) and saved!")
    }

    fun startFocusSession() {
        if (!_isAccessibilityEnabled.value) {
            showMessage("Please enable the Accessibility Service to enforce focus locking!")
            return
        }
        val duration = _selectedDurationMinutes.value
        prefs.startTimer(duration)
        refreshAll()
        val mode = _filterMode.value
        if (mode == FilterMode.BLOCKLIST) {
            val count = _blockedPackages.value.size
            showMessage("Focus session started for $duration min! $count blocked apps are locked.")
        } else {
            val count = _allowedPackages.value.size
            showMessage("Focus session started for $duration min! Only $count allowed apps stay open.")
        }
    }

    fun setTimeSelectionMode(mode: TimeSelectionMode) {
        _timeSelectionMode.value = mode
    }

    fun startFocusSessionWithUntilTime(durationMinutes: Int, untilTimeString: String) {
        if (!_isAccessibilityEnabled.value) {
            showMessage("Please enable the Accessibility Service to enforce focus locking!")
            return
        }
        val clamped = durationMinutes.coerceIn(1, 720)
        prefs.startTimer(clamped, untilTimeString)
        refreshAll()
        val mode = _filterMode.value
        if (mode == FilterMode.BLOCKLIST) {
            val count = _blockedPackages.value.size
            showMessage("Focus locked until $untilTimeString! $count blocked apps are locked.")
        } else {
            val count = _allowedPackages.value.size
            showMessage("Focus locked until $untilTimeString! Only $count allowed apps stay open.")
        }
    }

    fun requestStopTimer() {
        if (prefs.hasPasscode()) {
            _dialogState.value = DialogState.UnlockAction(
                actionDescription = "Stop Focus Timer",
                isEarlyStop = true
            ) {
                prefs.stopTimer()
                refreshAll()
                showMessage("Focus timer ended.")
            }
        } else {
            prefs.stopTimer()
            refreshAll()
            showMessage("Focus timer ended.")
        }
    }

    /**
     * Toggles Instant Continuous Lock.
     * When turning ON: requires passcode existence and displays a clear warning confirmation dialog.
     * When turning OFF: requires passcode verification.
     */
    fun requestTogglePasscodeLock() {
        val currentlyActive = prefs.isPasscodeLockActive()
        if (currentlyActive) {
            if (prefs.hasPasscode()) {
                _dialogState.value = DialogState.UnlockAction("Deactivate Instant Lock") {
                    prefs.setPasscodeLockActive(false)
                    refreshAll()
                    showMessage("Instant Lock deactivated.")
                }
            } else {
                prefs.setPasscodeLockActive(false)
                refreshAll()
                showMessage("Instant Lock deactivated.")
            }
        } else {
            if (!_isAccessibilityEnabled.value) {
                showMessage("Please enable the Accessibility Service first!")
                return
            }
            if (!prefs.hasPasscode()) {
                showMessage("Please set an unlock passcode first so you don't lock yourself out.")
                showSetPasscodeDialog()
                return
            }
            // Prompt warning and confirmation
            _dialogState.value = DialogState.ConfirmInstantLock
        }
    }

    fun confirmAndEngageInstantLock() {
        prefs.setPasscodeLockActive(true)
        refreshAll()
        _dialogState.value = DialogState.None
        showMessage("Instant Lock engaged! Continuous lockdown active until passcode is entered.")
    }

    /**
     * Unlock early from either Instant Lock or Timer Session.
     */
    fun requestUnlockActiveLock() {
        if (prefs.isPasscodeLockActive()) {
            requestTogglePasscodeLock()
        } else if (prefs.isTimerActive()) {
            requestStopTimer()
        }
    }

    fun requestToggleApp(packageName: String) {
        if (prefs.isLockActive() && prefs.hasPasscode()) {
            _dialogState.value = DialogState.UnlockAction("Modify App Rules") {
                toggleAppInternal(packageName)
            }
        } else {
            toggleAppInternal(packageName)
        }
    }

    private fun toggleAppInternal(packageName: String) {
        val mode = _filterMode.value
        if (mode == FilterMode.BLOCKLIST) {
            val newlyBlocked = prefs.toggleAppBlocked(packageName)
            val updatedSet = prefs.getBlockedPackages()
            _blockedPackages.value = updatedSet

            _installedApps.value = _installedApps.value.map {
                if (it.packageName == packageName) it.copy(isBlocked = newlyBlocked) else it
            }.sortedWith(
                compareByDescending<AppInfo> { it.isBlocked }.thenBy { it.appName.lowercase() }
            )

            showMessage(
                if (newlyBlocked) "App added to Blocklist (${updatedSet.size} blocked)"
                else "App removed from Blocklist (${updatedSet.size} blocked)"
            )
        } else {
            val newlyAllowed = prefs.toggleAppAllowed(packageName)
            val updatedSet = prefs.getAllowedPackages()
            _allowedPackages.value = updatedSet

            _installedApps.value = _installedApps.value.map {
                if (it.packageName == packageName) it.copy(isAllowed = newlyAllowed) else it
            }.sortedWith(
                compareByDescending<AppInfo> { it.isAllowed }.thenBy { it.appName.lowercase() }
            )

            showMessage(
                if (newlyAllowed) "App added to Whitelist (${updatedSet.size} allowed)"
                else "App removed from Whitelist (${updatedSet.size} allowed)"
            )
        }
    }

    fun verifyPasscode(candidate: String): Boolean {
        return prefs.verifyPasscode(candidate)
    }
}
