package com.touchgrass.app.ui.main

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.touchgrass.app.data.FilterMode
import com.touchgrass.app.model.AppInfo
import com.touchgrass.app.receiver.FocusDeviceAdminReceiver
import com.touchgrass.app.ui.theme.AppleAmber
import com.touchgrass.app.ui.theme.AppleAmberContainer
import com.touchgrass.app.ui.theme.AppleBlue
import com.touchgrass.app.ui.theme.AppleBlueContainer
import com.touchgrass.app.ui.theme.AppleCoral
import com.touchgrass.app.ui.theme.AppleCoralContainer
import com.touchgrass.app.ui.theme.AppleEmerald
import com.touchgrass.app.ui.theme.AppleEmeraldContainer
import com.touchgrass.app.ui.theme.AppleIndigo
import com.touchgrass.app.ui.theme.AppleIndigoContainer
import com.touchgrass.app.ui.theme.AppleLightBackground
import com.touchgrass.app.ui.theme.AppleLightBorder
import com.touchgrass.app.ui.theme.AppleLightCard
import com.touchgrass.app.ui.theme.AppleLightCardElevated
import com.touchgrass.app.ui.theme.AppleLightDivider
import com.touchgrass.app.ui.theme.ApplePurple
import com.touchgrass.app.ui.theme.ApplePurpleContainer
import com.touchgrass.app.ui.theme.AppleTextPrimary
import com.touchgrass.app.ui.theme.AppleTextSecondary
import com.touchgrass.app.ui.theme.AppleTextTertiary
import com.touchgrass.app.ui.components.CheekyVaultSentinel
import com.touchgrass.app.ui.components.CheekyTrollEarlyUnlockHeader
import com.touchgrass.app.ui.components.DynamicChildLifeAnimation
import com.touchgrass.app.util.AppIconLoader

enum class AppleTab(val label: String, val icon: ImageVector) {
    TIMER("Focus", Icons.Default.Timer),
    APPS("App Rules", Icons.Default.Apps),
    SECURITY("Protection", Icons.Default.Shield)
}

@Composable
fun TouchGrassScreen(
    viewModel: TouchGrassViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val filterMode by viewModel.filterMode.collectAsStateWithLifecycle()
    val blockedPackages by viewModel.blockedPackages.collectAsStateWithLifecycle()
    val allowedPackages by viewModel.allowedPackages.collectAsStateWithLifecycle()
    val isTimerActive by viewModel.isTimerActive.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val selectedDurationMinutes by viewModel.selectedDurationMinutes.collectAsStateWithLifecycle()
    val timeSelectionMode by viewModel.timeSelectionMode.collectAsStateWithLifecycle()
    val targetUntilTimeString by viewModel.targetUntilTimeString.collectAsStateWithLifecycle()
    val isPasscodeLockActive by viewModel.isPasscodeLockActive.collectAsStateWithLifecycle()
    val hasPasscode by viewModel.hasPasscode.collectAsStateWithLifecycle()
    val isAccessibilityEnabled by viewModel.isAccessibilityEnabled.collectAsStateWithLifecycle()
    val isDeviceAdminActive by viewModel.isDeviceAdminActive.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AppleTab.TIMER) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshAll()
                viewModel.loadInstalledApps()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    val isLockActive = isTimerActive || isPasscodeLockActive

    Scaffold(
        containerColor = AppleLightBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Apple iOS Unified Navigation Header
            AppleHeaderBar(
                currentTab = currentTab,
                isLockActive = isLockActive,
                filterMode = filterMode
            )
        },
        bottomBar = {
            // Authentic iOS Bottom Tab Bar (Instant touch, no Material pills)
            AppleBottomTabBar(
                currentTab = currentTab,
                onTabSelect = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppleTab.TIMER -> {
                    AppleTimerTabContent(
                        isTimerActive = isTimerActive,
                        isPasscodeLockActive = isPasscodeLockActive,
                        filterMode = filterMode,
                        remainingSeconds = remainingSeconds,
                        selectedMinutes = selectedDurationMinutes,
                        timeSelectionMode = timeSelectionMode,
                        targetUntilTimeString = targetUntilTimeString,
                        installedApps = installedApps,
                        blockedPackages = blockedPackages,
                        allowedPackages = allowedPackages,
                        onSelectMinutes = { viewModel.setDurationMinutes(it) },
                        onAdjustMinutes = { viewModel.adjustDuration(it) },
                        onTimeSelectionModeChange = { viewModel.setTimeSelectionMode(it) },
                        onStart = { viewModel.startFocusSession() },
                        onStartUntilTime = { duration, untilStr -> viewModel.startFocusSessionWithUntilTime(duration, untilStr) },
                        onStopTimer = { viewModel.requestStopTimer() },
                        onUnlockInstantLock = { viewModel.requestTogglePasscodeLock() },
                        onNavigateToApps = { currentTab = AppleTab.APPS }
                    )
                }
                AppleTab.APPS -> {
                    AppleAppsTabContent(
                        installedApps = installedApps,
                        filterMode = filterMode,
                        blockedPackages = blockedPackages,
                        allowedPackages = allowedPackages,
                        searchQuery = searchQuery,
                        onModeChange = { viewModel.requestSetFilterMode(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onToggleApp = { pkg -> viewModel.requestToggleApp(pkg) }
                    )
                }
                AppleTab.SECURITY -> {
                    AppleSecurityTabContent(
                        isAccessibilityEnabled = isAccessibilityEnabled,
                        isDeviceAdminActive = isDeviceAdminActive,
                        hasPasscode = hasPasscode,
                        isPasscodeLockActive = isPasscodeLockActive,
                        onOpenAccessibility = { openAccessibilitySettings(context) },
                        onActivateDeviceAdmin = { activateDeviceAdmin(context) },
                        onSetPasscode = { viewModel.showSetPasscodeDialog() },
                        onToggleInstantLock = { viewModel.requestTogglePasscodeLock() }
                    )
                }
            }
        }
    }

    // Modal Dialogs (Support Alphanumeric & Eye Toggle)
    when (val state = dialogState) {
        DialogState.None -> {}
        DialogState.SetPasscode -> {
            AppleSetPasscodeDialog(
                onDismiss = { viewModel.dismissDialog() },
                onSave = { viewModel.savePasscode(it) }
            )
        }
        DialogState.ConfirmInstantLock -> {
            AppleConfirmInstantLockDialog(
                onDismiss = { viewModel.dismissDialog() },
                onConfirm = { viewModel.confirmAndEngageInstantLock() }
            )
        }
        is DialogState.UnlockAction -> {
            AppleUnlockPasscodeDialog(
                actionTitle = state.actionDescription,
                isEarlyStop = state.isEarlyStop,
                onDismiss = { viewModel.dismissDialog() },
                onVerify = { candidate ->
                    val ok = viewModel.verifyPasscode(candidate)
                    if (ok) {
                        viewModel.dismissDialog()
                        state.onUnlocked()
                    }
                    ok
                }
            )
        }
    }
}

// ==========================================
// APPLE iOS TOP HEADER BAR
// ==========================================

@Composable
fun AppleHeaderBar(
    currentTab: AppleTab,
    isLockActive: Boolean,
    filterMode: FilterMode
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = AppleLightBackground
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Premium Apple-Grade Logo Squircle + Brand Title
                Row(
                    modifier = Modifier.weight(1f).padding(end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // TouchGrass Brand Apple Squircle Badge
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Transparent,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF30D158), Color(0xFF059669))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = com.touchgrass.app.R.drawable.ic_touchgrass_logo),
                                contentDescription = "TouchGrass Logo",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "TouchGrass",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.4).sp,
                                    fontSize = 17.sp
                                ),
                                color = AppleTextPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "• ${when (currentTab) {
                                    AppleTab.TIMER -> "Focus"
                                    AppleTab.APPS -> "App Rules"
                                    AppleTab.SECURITY -> "Protection"
                                }}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = (-0.2).sp,
                                    fontSize = 14.sp
                                ),
                                color = AppleEmerald
                            )
                        }
                        Text(
                            text = if (currentTab == AppleTab.APPS) {
                                if (filterMode == FilterMode.BLOCKLIST) "Go for a walk • Blocklist Shield" else "Go for a walk • Allowed Only"
                            } else {
                                "Go for a walk • 100% Offline"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = AppleTextTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Right: High-End iOS Dynamic Capsule
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isLockActive) AppleCoralContainer else AppleEmeraldContainer,
                    border = BorderStroke(
                        0.5.dp,
                        if (isLockActive) AppleCoral.copy(alpha = 0.5f) else AppleEmerald.copy(alpha = 0.5f)
                    ),
                    shadowElevation = 0.5.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isLockActive) AppleCoral else AppleEmerald)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLockActive) "LOCKED" else "SECURE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 0.4.sp,
                                fontSize = 11.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = if (isLockActive) AppleCoral else AppleEmerald,
                            maxLines = 1
                        )
                    }
                }
            }

            HorizontalDivider(color = AppleLightBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
        }
    }
}

// ==========================================
// APPLE iOS NATIVE BOTTOM TAB BAR
// ==========================================

@Composable
fun AppleBottomTabBar(
    currentTab: AppleTab,
    onTabSelect: (AppleTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = AppleLightCard,
        border = BorderStroke(0.5.dp, AppleLightBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppleTab.values().forEach { tab ->
                val selected = currentTab == tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelect(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) AppleIndigo else AppleTextTertiary,
                        modifier = Modifier.size(23.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) AppleIndigo else AppleTextTertiary
                    )
                }
            }
        }
    }
}

// ==========================================
// TAB 1: APPLE TIMER ROOM
// ==========================================

data class SuggestedUntilTime(
    val timeMillis: Long,
    val formattedTime: String,
    val formattedDuration: String,
    val durationMinutes: Int
)

fun generateSuggestedUntilTimes(nowMillis: Long = System.currentTimeMillis()): List<SuggestedUntilTime> {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }

    // Round up to next 15-minute interval
    val minute = cal.get(Calendar.MINUTE)
    val remainder = minute % 15
    val minutesToAdd = if (remainder == 0) 15 else (15 - remainder)
    cal.add(Calendar.MINUTE, minutesToAdd)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val list = mutableListOf<SuggestedUntilTime>()
    // Generate intervals starting from rounded current time forward
    val baseMillis = cal.timeInMillis
    val offsets = listOf(0, 15, 30, 45, 60, 75, 90, 105, 120, 150, 180, 240, 300, 360, 480, 600, 720)
    for (offset in offsets) {
        val targetMillis = baseMillis + (offset * 60 * 1000L)
        val diffMinutes = ((targetMillis - nowMillis) / (60 * 1000L)).toInt()
        if (diffMinutes >= 5) {
            val formattedTime = timeFormat.format(targetMillis)
            val h = diffMinutes / 60
            val m = diffMinutes % 60
            val durStr = when {
                h > 0 && m > 0 -> "${h}h ${m}m"
                h > 0 -> "${h}h"
                else -> "${m}m"
            }
            list.add(
                SuggestedUntilTime(
                    timeMillis = targetMillis,
                    formattedTime = formattedTime,
                    formattedDuration = durStr,
                    durationMinutes = diffMinutes
                )
            )
        }
    }
    return list
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppleTimerTabContent(
    isTimerActive: Boolean,
    isPasscodeLockActive: Boolean,
    filterMode: FilterMode,
    remainingSeconds: Long,
    selectedMinutes: Int,
    timeSelectionMode: TimeSelectionMode,
    targetUntilTimeString: String?,
    installedApps: List<AppInfo>,
    blockedPackages: Set<String>,
    allowedPackages: Set<String>,
    onSelectMinutes: (Int) -> Unit,
    onAdjustMinutes: (Int) -> Unit,
    onTimeSelectionModeChange: (TimeSelectionMode) -> Unit,
    onStart: () -> Unit,
    onStartUntilTime: (Int, String) -> Unit,
    onStopTimer: () -> Unit,
    onUnlockInstantLock: () -> Unit,
    onNavigateToApps: () -> Unit
) {
    val actualBlockedCount = remember(installedApps) { installedApps.count { it.isBlocked } }
    val actualAllowedCount = remember(installedApps) { installedApps.count { it.isAllowed } }
    val suggestedUntilTimes = remember { generateSuggestedUntilTimes() }
    var selectedUntilIndex by remember { mutableStateOf(1.coerceAtMost(suggestedUntilTimes.lastIndex)) }
    val currentSelectedUntil = suggestedUntilTimes.getOrNull(selectedUntilIndex) ?: suggestedUntilTimes.firstOrNull() ?: SuggestedUntilTime(
        timeMillis = System.currentTimeMillis() + 30 * 60 * 1000L,
        formattedTime = "30m",
        formattedDuration = "30m",
        durationMinutes = 30
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isPasscodeLockActive) {
                // HIGH-IMPACT VAULT LOCKDOWN UI (Supersedes any timer)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = AppleLightCard,
                    border = BorderStroke(1.5.dp, AppleCoral.copy(alpha = 0.6f)),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 26.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppleCoralContainer,
                            border = BorderStroke(0.5.dp, AppleCoral.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AppleCoral)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CONTINUOUS VAULT LOCKDOWN",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = AppleCoral
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = AppleCoral,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "LOCKED",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 3.sp,
                                    fontSize = 44.sp
                                ),
                                color = AppleCoral
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (filterMode == FilterMode.BLOCKLIST)
                                "Settings and $actualBlockedCount restricted apps locked"
                            else
                                "Settings and all non-whitelisted apps locked",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleTextSecondary,
                            textAlign = TextAlign.Center
                        )

                        // If a background timer was also running, show its remaining duration elegantly
                        if (isTimerActive && remainingSeconds > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AppleCoralContainer.copy(alpha = 0.5f),
                                border = BorderStroke(0.5.dp, AppleCoral.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = AppleCoral,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (!targetUntilTimeString.isNullOrBlank()) {
                                            "Session Timer: Locked until $targetUntilTimeString (${formatTime(remainingSeconds)} left, superseded by Instant Lock)"
                                        } else {
                                            "Session Timer: ${formatTime(remainingSeconds)} remaining (superseded by Instant Lock)"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppleCoral,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Impactful Lockdown Details Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = AppleLightCard,
                    border = BorderStroke(0.5.dp, AppleLightBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AppleCoralContainer,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = AppleCoral,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "High-Security Lockdown Active",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary
                            )
                        }

                        Text(
                            text = "No countdown limit. TouchGrass Accessibility Enforcer and Device Admin prevent bypassing restrictions or uninstalling. To release the lockdown, tap the button below and enter your secret passcode.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Cheeky Animated Vault Mascot in bottom vacant space
                CheekyVaultSentinel(isInstantLock = true)
            } else if (isTimerActive) {
                // TIMED FOCUS SESSION UI
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = AppleLightCard,
                    border = BorderStroke(0.5.dp, AppleLightBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (!targetUntilTimeString.isNullOrBlank()) "LOCKED UNTIL $targetUntilTimeString" else "SESSION REMAINING",
                            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
                            fontWeight = FontWeight.Bold,
                            color = AppleCoral
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatTime(remainingSeconds),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontSize = 46.sp
                            ),
                            color = AppleCoral
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (!targetUntilTimeString.isNullOrBlank()) {
                                "Focus locked until $targetUntilTimeString • ${if (filterMode == FilterMode.BLOCKLIST) "$actualBlockedCount apps blocked" else "$actualAllowedCount apps allowed"}"
                            } else {
                                if (filterMode == FilterMode.BLOCKLIST)
                                    "Blocklist enforced ($actualBlockedCount apps) • Settings blocked"
                                else
                                    "Whitelist strictly enforced ($actualAllowedCount apps)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleTextTertiary
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = AppleCoralContainer,
                    border = BorderStroke(0.5.dp, AppleCoral.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Focus Lock Engaged",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = AppleCoral
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (filterMode == FilterMode.BLOCKLIST)
                                "Android Settings and selected blocked apps are actively prohibited until timer elapses."
                            else
                                "Android Settings and all apps outside your whitelist are actively prohibited.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = AppleTextPrimary
                        )
                    }
                }

                // Cheeky Animated Vault Mascot in bottom vacant space
                CheekyVaultSentinel(isInstantLock = false)
            } else {
                // IDLE STATE: TOP TOGGLE [ Duration | Until Time ] + ADAPTIVE CONFIGURATION
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = AppleLightCardElevated,
                    border = BorderStroke(0.5.dp, AppleLightBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .clickable { onTimeSelectionModeChange(TimeSelectionMode.DURATION) },
                            shape = RoundedCornerShape(11.dp),
                            color = if (timeSelectionMode == TimeSelectionMode.DURATION) AppleLightCard else Color.Transparent,
                            shadowElevation = if (timeSelectionMode == TimeSelectionMode.DURATION) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = if (timeSelectionMode == TimeSelectionMode.DURATION) AppleIndigo else AppleTextTertiary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Duration",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (timeSelectionMode == TimeSelectionMode.DURATION) FontWeight.Bold else FontWeight.Medium,
                                    color = if (timeSelectionMode == TimeSelectionMode.DURATION) AppleTextPrimary else AppleTextSecondary
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .clickable { onTimeSelectionModeChange(TimeSelectionMode.UNTIL_TIME) },
                            shape = RoundedCornerShape(11.dp),
                            color = if (timeSelectionMode == TimeSelectionMode.UNTIL_TIME) AppleLightCard else Color.Transparent,
                            shadowElevation = if (timeSelectionMode == TimeSelectionMode.UNTIL_TIME) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (timeSelectionMode == TimeSelectionMode.UNTIL_TIME) AppleIndigo else AppleTextTertiary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Until Time",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (timeSelectionMode == TimeSelectionMode.UNTIL_TIME) FontWeight.Bold else FontWeight.Medium,
                                    color = if (timeSelectionMode == TimeSelectionMode.UNTIL_TIME) AppleTextPrimary else AppleTextSecondary
                                )
                            }
                        }
                    }
                }

                val effectiveMinutes = if (timeSelectionMode == TimeSelectionMode.UNTIL_TIME) {
                    currentSelectedUntil.durationMinutes
                } else {
                    selectedMinutes
                }

                if (timeSelectionMode == TimeSelectionMode.UNTIL_TIME) {
                    // UNIFIED UNTIL TIME CARD
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = AppleLightCard,
                        border = BorderStroke(0.5.dp, AppleLightBorder),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header row: Label + Steppers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "FOCUS UNTIL ${currentSelectedUntil.formattedTime.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextSecondary
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .clickable(enabled = selectedUntilIndex > 0) {
                                                if (selectedUntilIndex > 0) selectedUntilIndex--
                                            },
                                        color = AppleLightCardElevated,
                                        shape = CircleShape,
                                        border = BorderStroke(0.5.dp, AppleLightBorder)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = "Earlier",
                                                tint = if (selectedUntilIndex > 0) AppleTextPrimary else AppleTextTertiary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = currentSelectedUntil.formattedDuration,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AppleIndigo
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .clickable(enabled = selectedUntilIndex < suggestedUntilTimes.lastIndex) {
                                                if (selectedUntilIndex < suggestedUntilTimes.lastIndex) selectedUntilIndex++
                                            },
                                        color = AppleLightCardElevated,
                                        shape = CircleShape,
                                        border = BorderStroke(0.5.dp, AppleLightBorder)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Later",
                                                tint = if (selectedUntilIndex < suggestedUntilTimes.lastIndex) AppleTextPrimary else AppleTextTertiary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Big Clock
                            Text(
                                text = currentSelectedUntil.formattedTime,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 38.sp
                                ),
                                color = AppleTextPrimary
                            )

                            Text(
                                text = "${currentSelectedUntil.durationMinutes} min focus session from now",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppleTextTertiary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Suggested times scroll starting from current time
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp)
                            ) {
                                items(suggestedUntilTimes.size) { index ->
                                    val item = suggestedUntilTimes[index]
                                    val isSelected = (index == selectedUntilIndex)
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { selectedUntilIndex = index },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) AppleIndigo else AppleLightCardElevated,
                                        border = BorderStroke(
                                            0.5.dp,
                                            if (isSelected) AppleIndigo else AppleLightBorder
                                        ),
                                        shadowElevation = if (isSelected) 1.5.dp else 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.formattedTime,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else AppleTextPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.formattedDuration,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else AppleTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // UNIFIED DURATION CARD
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = AppleLightCard,
                        border = BorderStroke(0.5.dp, AppleLightBorder),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header row: Label + Steppers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${formatHoursMinutesVerbose(selectedMinutes).uppercase()} FOCUS",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextSecondary
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .clickable { onAdjustMinutes(-15) },
                                        color = AppleLightCardElevated,
                                        shape = CircleShape,
                                        border = BorderStroke(0.5.dp, AppleLightBorder)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = AppleTextPrimary, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "${selectedMinutes}m",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AppleIndigo
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .clickable { onAdjustMinutes(15) },
                                        color = AppleLightCardElevated,
                                        shape = CircleShape,
                                        border = BorderStroke(0.5.dp, AppleLightBorder)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = AppleTextPrimary, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Big Digits
                            Text(
                                text = formatMinutes(selectedMinutes),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    fontSize = 40.sp
                                ),
                                color = AppleTextPrimary
                            )

                            Text(
                                text = "Drag slider or tap steppers to adjust",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppleTextTertiary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Smooth Apple Slider
                            Slider(
                                value = selectedMinutes.toFloat(),
                                onValueChange = { onSelectMinutes(it.toInt()) },
                                valueRange = 5f..360f,
                                thumb = {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .shadow(elevation = 3.dp, shape = CircleShape)
                                            .background(Color.White, shape = CircleShape)
                                            .border(0.5.dp, Color(0x33000000), shape = CircleShape)
                                    )
                                },
                                track = { sliderState ->
                                    SliderDefaults.Track(
                                        sliderState = sliderState,
                                        modifier = Modifier.height(5.dp),
                                        colors = SliderDefaults.colors(
                                            activeTrackColor = AppleIndigo,
                                            inactiveTrackColor = AppleLightCardElevated
                                        ),
                                        drawStopIndicator = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Dynamic Child Life SVG/Canvas Animation (Transforms smoothly from 1m to 360m)
                DynamicChildLifeAnimation(
                    durationMinutes = effectiveMinutes,
                    modifier = Modifier.fillMaxWidth()
                )

                // Refined App Rules Status Pill
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToApps() },
                    shape = RoundedCornerShape(12.dp),
                    color = AppleLightCard,
                    border = BorderStroke(0.5.dp, AppleLightBorder),
                    shadowElevation = 0.5.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (filterMode == FilterMode.BLOCKLIST) AppleCoral else AppleIndigo,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (filterMode == FilterMode.BLOCKLIST) {
                                    if (actualBlockedCount == 0) "Blocklist: 0 apps selected"
                                    else "Blocklist: $actualBlockedCount apps restricted"
                                } else {
                                    if (actualAllowedCount == 0) "Whitelist: 0 apps allowed"
                                    else "Whitelist: $actualAllowedCount apps allowed"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AppleTextPrimary
                            )
                        }

                        Text(
                            text = "Rules →",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppleIndigo
                        )
                    }
                }
            }
        }

        // Bottom Action Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            when {
                isPasscodeLockActive -> {
                    Button(
                        onClick = onUnlockInstantLock,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleCoral)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Unlock Instant Lock",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
                isTimerActive -> {
                    Button(
                        onClick = onStopTimer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleCoral)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Unlock Early / End Session",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
                timeSelectionMode == TimeSelectionMode.UNTIL_TIME -> {
                    Button(
                        onClick = {
                            onStartUntilTime(
                                currentSelectedUntil.durationMinutes,
                                currentSelectedUntil.formattedTime
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleIndigo)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Focus (Until ${currentSelectedUntil.formattedTime})",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
                else -> {
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleIndigo)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Focus ($selectedMinutes min)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 2: APPLE APPS TAB (HIGH-PERFORMANCE VIRTUALIZED SCROLLING + BLOCKLIST)
// =========================================================================

@Composable
fun AppleAppsTabContent(
    installedApps: List<AppInfo>,
    filterMode: FilterMode,
    blockedPackages: Set<String>,
    allowedPackages: Set<String>,
    searchQuery: String,
    onModeChange: (FilterMode) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleApp: (String) -> Unit
) {
    val isBlocklist = filterMode == FilterMode.BLOCKLIST
    val activeCount = remember(installedApps, isBlocklist) {
        installedApps.count { if (isBlocklist) it.isBlocked else it.isAllowed }
    }

    val filtered = remember(installedApps, searchQuery, filterMode) {
        installedApps.filter {
            searchQuery.isBlank() ||
                    it.appName.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. iOS Segmented Mode Selector: Block Selected (Default) vs Allow Only
        item(key = "mode_selector") {
            AppleFilterModeSegmentedControl(
                selectedMode = filterMode,
                onModeSelect = onModeChange
            )
        }

        // 2. Status Header Banner
        item(key = "status_header") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = if (isBlocklist) AppleCoralContainer else AppleEmeraldContainer,
                border = BorderStroke(
                    0.5.dp,
                    if (isBlocklist) AppleCoral.copy(alpha = 0.3f) else AppleEmerald.copy(alpha = 0.3f)
                ),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBlocklist) "Blocked Apps Control" else "Allowed Apps Whitelist",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = AppleTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBlocklist)
                                "Selected apps (e.g. Insta, YouTube) are blocked when locked"
                            else
                                "Only selected apps remain accessible when locked",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleTextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isBlocklist) AppleCoral else AppleEmerald
                    ) {
                        Text(
                            text = if (isBlocklist) "$activeCount Blocked" else "$activeCount Allowed",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 3. Apple-style Search Bar
        item(key = "search_bar") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search installed applications...", color = AppleTextTertiary, style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppleTextSecondary, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = AppleTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AppleLightCard,
                    unfocusedContainerColor = AppleLightCard,
                    focusedBorderColor = if (isBlocklist) AppleCoral else AppleIndigo,
                    unfocusedBorderColor = AppleLightBorder,
                    focusedTextColor = AppleTextPrimary,
                    unfocusedTextColor = AppleTextPrimary
                )
            )
        }

        // 4. VIRTUALIZED INSTANT-RENDER APPS (BIG TECH 120 FPS ARCHITECTURE)
        if (filtered.isEmpty()) {
            item(key = "empty_placeholder") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "Loading applications..." else "No applications match \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppleTextSecondary
                    )
                }
            }
        } else {
            items(
                items = filtered,
                key = { it.packageName }
            ) { appInfo ->
                val isChecked = if (isBlocklist) appInfo.isBlocked else appInfo.isAllowed
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = AppleLightCard,
                    border = BorderStroke(0.5.dp, AppleLightBorder),
                    shadowElevation = 0.5.dp
                ) {
                    AppleAppItemRow(
                        appInfo = appInfo,
                        isChecked = isChecked,
                        isBlocklistMode = isBlocklist,
                        onToggle = { onToggleApp(appInfo.packageName) }
                    )
                }
            }
        }

        item(key = "footer_spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * iOS-Style Segmented Control: [ 🚫 Block Apps (Default) | 🛡️ Allow Apps ]
 */
@Composable
fun AppleFilterModeSegmentedControl(
    selectedMode: FilterMode,
    onModeSelect: (FilterMode) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),
        color = AppleLightCardElevated,
        border = BorderStroke(0.5.dp, AppleLightBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val isBlock = selectedMode == FilterMode.BLOCKLIST

            // Blocklist Option (Default)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onModeSelect(FilterMode.BLOCKLIST) },
                shape = RoundedCornerShape(10.dp),
                color = if (isBlock) AppleLightCard else Color.Transparent,
                shadowElevation = if (isBlock) 2.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = null,
                        tint = if (isBlock) AppleCoral else AppleTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Block Apps (Default)",
                        fontSize = 12.sp,
                        fontWeight = if (isBlock) FontWeight.Bold else FontWeight.Medium,
                        color = if (isBlock) AppleTextPrimary else AppleTextSecondary
                    )
                }
            }

            // Whitelist Option
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onModeSelect(FilterMode.WHITELIST) },
                shape = RoundedCornerShape(10.dp),
                color = if (!isBlock) AppleLightCard else Color.Transparent,
                shadowElevation = if (!isBlock) 2.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (!isBlock) AppleIndigo else AppleTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Allow Only",
                        fontSize = 12.sp,
                        fontWeight = if (!isBlock) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isBlock) AppleTextPrimary else AppleTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AppleAppItemRow(
    appInfo: AppInfo,
    isChecked: Boolean,
    isBlocklistMode: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Asynchronous, Non-Blocking App Icon from LRU Cache
            AsyncAppIcon(
                packageName = appInfo.packageName,
                modifier = Modifier.size(38.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = appInfo.appName,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppleTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (appInfo.isSystemApp) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AppleLightCardElevated
                        ) {
                            Text(
                                text = "System",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = AppleTextTertiary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBlocklistMode) {
                        if (isChecked) "Blocked during sessions" else appInfo.packageName
                    } else {
                        if (isChecked) "Allowed during sessions" else appInfo.packageName
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = if (isChecked) {
                        if (isBlocklistMode) AppleCoral else AppleIndigo
                    } else AppleTextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = if (isBlocklistMode) AppleCoral else AppleIndigo,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppleLightCardElevated
            )
        )
    }
}

/**
 * Asynchronous, non-blocking icon loader using AppIconLoader LRU cache.
 * Eliminates main-thread hitches by loading in background and fading in instantly.
 */
@Composable
fun AsyncAppIcon(packageName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var imageBitmap by remember(packageName) {
        mutableStateOf<ImageBitmap?>(AppIconLoader.getCached(packageName))
    }

    LaunchedEffect(packageName) {
        if (imageBitmap == null) {
            imageBitmap = AppIconLoader.loadIcon(context, packageName)
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = null,
            modifier = modifier.clip(RoundedCornerShape(9.dp))
        )
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(9.dp),
            color = AppleLightCardElevated,
            border = BorderStroke(0.5.dp, AppleLightBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null,
                    tint = AppleTextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==========================================
// TAB 3: APPLE PROTECTION & SETTINGS
// ==========================================

@Composable
fun AppleSecurityTabContent(
    isAccessibilityEnabled: Boolean,
    isDeviceAdminActive: Boolean,
    hasPasscode: Boolean,
    isPasscodeLockActive: Boolean,
    onOpenAccessibility: () -> Unit,
    onActivateDeviceAdmin: () -> Unit,
    onSetPasscode: () -> Unit,
    onToggleInstantLock: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // SECTION 1: SYSTEM ENFORCEMENT
            Text(
                text = "ENFORCEMENT POLICIES",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                fontWeight = FontWeight.Bold,
                color = AppleTextSecondary,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = AppleLightCard,
                border = BorderStroke(0.5.dp, AppleLightBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    // Row 1: Accessibility App Enforcer (Clean tinted button, NO TRUNCATION!)
                    AppleSettingsRow(
                        icon = Icons.Default.Security,
                        iconTint = ApplePurple,
                        iconBg = ApplePurpleContainer,
                        title = "Accessibility App Enforcer",
                        subtitle = "Redirects unauthorized apps and Android Settings to home screen.",
                        isActive = isAccessibilityEnabled,
                        actionLabel = "Enable",
                        onAction = onOpenAccessibility
                    )

                    HorizontalDivider(
                        color = AppleLightDivider,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    // Row 2: Device Admin Uninstall Protection (Clean tinted button, NO TRUNCATION!)
                    AppleSettingsRow(
                        icon = Icons.Default.AdminPanelSettings,
                        iconTint = AppleBlue,
                        iconBg = AppleBlueContainer,
                        title = "Uninstall Protection",
                        subtitle = "Device Admin blocks app uninstallation during active sessions.",
                        isActive = isDeviceAdminActive,
                        actionLabel = "Activate",
                        onAction = onActivateDeviceAdmin
                    )
                }
            }
        }

        item {
            // SECTION 2: PASSCODE & LOCKDOWN
            Text(
                text = "PASSCODE & CONTINUOUS LOCK",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                fontWeight = FontWeight.Bold,
                color = AppleTextSecondary,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = AppleLightCard,
                border = BorderStroke(0.5.dp, AppleLightBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    // Passcode Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(38.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = AppleAmberContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = AppleAmber, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Unlock Passcode",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppleTextPrimary
                                )
                                Text(
                                    text = if (hasPasscode) "SHA-256 Hashed (Alphanumeric/PIN)" else "Not Configured",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (hasPasscode) AppleEmerald else AppleTextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = onSetPasscode,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppleIndigoContainer,
                                contentColor = AppleIndigo
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (hasPasscode) "Change" else "Set PIN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(
                        color = AppleLightDivider,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Instant Continuous Lock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(38.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = AppleCoralContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = AppleCoral, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Instant Lock",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppleTextPrimary
                                )
                                Text(
                                    text = if (isPasscodeLockActive) "Continuous lock is active" else "Engage continuous lock without timer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isPasscodeLockActive) AppleCoral else AppleTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isPasscodeLockActive,
                            onCheckedChange = { onToggleInstantLock() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AppleCoral,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = AppleLightCardElevated
                            )
                        )
                    }
                }
            }
        }

        item {
            // SECTION 3: PRIVACY & OFFLINE INTEGRITY
            Text(
                text = "PRIVACY & INTEGRITY GUARANTEE",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                fontWeight = FontWeight.Bold,
                color = AppleTextSecondary,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = AppleLightCard,
                border = BorderStroke(0.5.dp, AppleLightBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrivacyItem(
                        title = "Zero Internet Access",
                        description = "android.permission.INTERNET is completely absent. Sockets are physically blocked by the Linux kernel."
                    )
                    HorizontalDivider(color = AppleLightDivider)
                    PrivacyItem(
                        title = "Zero Advertisements & Trackers",
                        description = "100% offline, privacy-first software containing zero third-party telemetry."
                    )
                    HorizontalDivider(color = AppleLightDivider)
                    PrivacyItem(
                        title = "SHA-256 Cryptography",
                        description = "Plaintext passcodes are never saved to disk. Stored only as SHA-256 hashes."
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PrivacyItem(title: String, description: String) {
    Column {
        Text(text = title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, color = AppleTextPrimary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = description, style = MaterialTheme.typography.bodySmall, color = AppleTextSecondary)
    }
}

@Composable
fun AppleSettingsRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    isActive: Boolean,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                color = iconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppleTextPrimary
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (isActive) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppleEmeraldContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = AppleEmerald, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppleEmerald
                    )
                }
            }
        } else {
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppleIndigoContainer,
                    contentColor = AppleIndigo
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(actionLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// MODAL DIALOGS (ALPHANUMERIC & EYE TOGGLE)
// ==========================================

@Composable
fun AppleSetPasscodeDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var passcode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppleLightCard,
        shape = RoundedCornerShape(22.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AppleAmberContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = AppleAmber, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Set Unlock Passcode",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = AppleTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Supports alphanumeric passwords & PINs (SHA-256)",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleTextSecondary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = passcode,
                    onValueChange = {
                        passcode = it
                        errorMessage = null
                    },
                    label = { Text("New Passcode (Letters / Digits)") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = AppleTextSecondary
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppleLightCardElevated,
                        unfocusedContainerColor = AppleLightCardElevated,
                        focusedBorderColor = AppleIndigo,
                        unfocusedBorderColor = AppleLightBorder
                    )
                )

                OutlinedTextField(
                    value = confirmPasscode,
                    onValueChange = {
                        confirmPasscode = it
                        errorMessage = null
                    },
                    label = { Text("Confirm Passcode") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppleLightCardElevated,
                        unfocusedContainerColor = AppleLightCardElevated,
                        focusedBorderColor = AppleIndigo,
                        unfocusedBorderColor = AppleLightBorder
                    )
                )

                errorMessage?.let {
                    Text(text = it, color = AppleCoral, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (passcode.isBlank()) {
                        errorMessage = "Passcode cannot be empty"
                    } else if (passcode != confirmPasscode) {
                        errorMessage = "Passcodes do not match"
                    } else {
                        onSave(passcode)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppleIndigo)
            ) {
                Text("Save Passcode", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AppleTextSecondary)
            }
        }
    )
}

@Composable
fun AppleUnlockPasscodeDialog(
    actionTitle: String,
    isEarlyStop: Boolean = false,
    onDismiss: () -> Unit,
    onVerify: (String) -> Boolean
) {
    var candidatePasscode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppleLightCard,
        shape = RoundedCornerShape(22.dp),
        title = {
            if (isEarlyStop) {
                CheekyTrollEarlyUnlockHeader()
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AppleCoralContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AppleCoral, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Passcode Required",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppleTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        actionTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppleTextSecondary
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = candidatePasscode,
                    onValueChange = {
                        candidatePasscode = it
                        errorMessage = null
                    },
                    label = {
                        Text(
                            text = if (isEarlyStop) "Passcode of Shame" else "Passcode",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    placeholder = {
                        Text(
                            text = if (isEarlyStop) "Passcode of Shame" else "Enter Passcode",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = AppleTextTertiary
                        )
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val ok = onVerify(candidatePasscode)
                            if (!ok) errorMessage = "Incorrect passcode. SHA-256 mismatch."
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = AppleTextSecondary
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppleLightCardElevated,
                        unfocusedContainerColor = AppleLightCardElevated,
                        focusedBorderColor = AppleCoral,
                        unfocusedBorderColor = AppleLightBorder
                    )
                )

                errorMessage?.let {
                    Text(text = it, color = AppleCoral, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ok = onVerify(candidatePasscode)
                    if (!ok) errorMessage = "Incorrect passcode. SHA-256 mismatch."
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppleCoral)
            ) {
                Text(if (isEarlyStop) "Surrender & End" else "Unlock", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AppleTextSecondary)
            }
        }
    )
}

@Composable
fun AppleConfirmInstantLockDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppleLightCard,
        shape = RoundedCornerShape(22.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(13.dp),
                    color = AppleCoralContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = AppleCoral,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Engage Instant Lock?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = AppleTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Indefinite Continuous Lockdown",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleCoral,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppleLightCardElevated,
                    border = BorderStroke(0.5.dp, AppleLightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Continuous Enforcement",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppleTextPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Instant Lock has NO countdown timer. Blocked apps and Android Settings remain locked until you manually enter your passcode.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppleCoralContainer.copy(alpha = 0.5f),
                    border = BorderStroke(0.5.dp, AppleCoral.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = AppleCoral,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Requires your unlock passcode to disengage.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppleCoral)
            ) {
                Text("Engage Lock", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AppleTextSecondary)
            }
        }
    )
}

// ==========================================
// TIME FORMATTING UTILITIES
// ==========================================

fun formatTime(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    val remMin = minutes % 60
    return String.format(Locale.US, "%02d:%02d:00", hours, remMin)
}

fun formatHoursMinutesVerbose(minutes: Int): String {
    val hours = minutes / 60
    val remMin = minutes % 60
    return when {
        hours > 0 && remMin > 0 -> "$hours HR $remMin MIN"
        hours > 0 -> "$hours HOUR"
        else -> "$minutes MINUTE"
    }
}

fun openAccessibilitySettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

fun activateDeviceAdmin(context: Context) {
    try {
        val componentName = FocusDeviceAdminReceiver.getComponentName(context)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "TouchGrass requires Device Admin privileges to prevent uninstallation during active focus sessions."
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
