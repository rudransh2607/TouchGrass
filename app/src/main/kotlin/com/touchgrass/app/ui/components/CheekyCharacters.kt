package com.touchgrass.app.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.touchgrass.app.ui.theme.AppleAmber
import com.touchgrass.app.ui.theme.AppleAmberContainer
import com.touchgrass.app.ui.theme.AppleCoral
import com.touchgrass.app.ui.theme.AppleCoralContainer
import com.touchgrass.app.ui.theme.AppleEmerald
import com.touchgrass.app.ui.theme.AppleEmeraldContainer
import com.touchgrass.app.ui.theme.AppleIndigo
import com.touchgrass.app.ui.theme.AppleIndigoContainer
import com.touchgrass.app.ui.theme.AppleLightBorder
import com.touchgrass.app.ui.theme.AppleLightCard
import com.touchgrass.app.ui.theme.AppleLightCardElevated
import com.touchgrass.app.ui.theme.AppleLightDivider
import com.touchgrass.app.ui.theme.AppleTextPrimary
import com.touchgrass.app.ui.theme.AppleTextSecondary
import com.touchgrass.app.ui.theme.AppleTextTertiary

/**
 * Soothing Nature & Sunshine Sanctuary Card:
 * Displayed in the bottom section of the Focus screen when a lock is active.
 * Replaces technical robot sentinels with a calming, organic, Apple-grade illustration
 * of a warm golden sun, rolling green hills, and swaying grass with mindful "touch grass" remarks.
 */
@Composable
fun CheekyVaultSentinel(
    isInstantLock: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nature_anim")

    // Gentle sunbeam radiance breathing
    val sunRayScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun_pulse"
    )

    // Gentle natural breeze sway for foreground sprout
    val leafSway by infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaf_sway"
    )

    val badgeColor = AppleEmerald
    val badgeContainer = AppleEmeraldContainer

    val natureQuote = remember(isInstantLock) {
        if (isInstantLock) {
            "DIGITAL DETOX" to (
                "Rooted in Reality" to
                "No notifications, no blue light, just open air. Step away from the glass and reconnect with the physical world."
            )
        } else {
            "TOUCH GRASS" to (
                "Unplug & Touch Grass" to
                "The real world has infinite resolution. Put your phone face down, feel the warm sunlight, and go touch some grass."
            )
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = AppleLightCard,
        border = BorderStroke(0.5.dp, AppleLightBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Handcrafted Soothing Nature & Sunshine Vector Canvas
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF0FDF4)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(78.dp)) {
                    val w = size.width
                    val h = size.height

                    // 1. Soft sky frame border
                    drawRoundRect(
                        color = Color(0xFFDCFCE7),
                        cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // 2. Radiant Golden Sun & Warm Ambient Halo
                    val sunCenterX = w * 0.72f
                    val sunCenterY = h * 0.28f
                    val sunRadius = 11.dp.toPx()

                    // Ambient soft glow
                    drawCircle(
                        color = Color(0xFFFEF08A).copy(alpha = 0.45f),
                        radius = sunRadius * 1.6f * sunRayScale,
                        center = Offset(sunCenterX, sunCenterY)
                    )

                    // Sun rays radiating outward
                    val rayAngles = listOf(0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0)
                    val rayInner = sunRadius * 1.25f
                    val rayOuter = sunRadius * 1.75f * sunRayScale
                    for (deg in rayAngles) {
                        val rad = Math.toRadians(deg)
                        val start = Offset(
                            (sunCenterX + rayInner * Math.cos(rad)).toFloat(),
                            (sunCenterY + rayInner * Math.sin(rad)).toFloat()
                        )
                        val end = Offset(
                            (sunCenterX + rayOuter * Math.cos(rad)).toFloat(),
                            (sunCenterY + rayOuter * Math.sin(rad)).toFloat()
                        )
                        drawLine(
                            color = Color(0xFFFBBF24),
                            start = start,
                            end = end,
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Golden sun core
                    drawCircle(
                        color = Color(0xFFF59E0B),
                        radius = sunRadius,
                        center = Offset(sunCenterX, sunCenterY)
                    )

                    // 3. Tiny flying birds in the morning sky
                    val bird1 = Path().apply {
                        moveTo(w * 0.22f, h * 0.26f)
                        quadraticTo(w * 0.26f, h * 0.21f, w * 0.30f, h * 0.26f)
                        quadraticTo(w * 0.34f, h * 0.21f, w * 0.38f, h * 0.26f)
                    }
                    drawPath(bird1, color = Color(0xFF0284C7).copy(alpha = 0.55f), style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round))

                    val bird2 = Path().apply {
                        moveTo(w * 0.36f, h * 0.35f)
                        quadraticTo(w * 0.39f, h * 0.31f, w * 0.42f, h * 0.35f)
                        quadraticTo(w * 0.45f, h * 0.31f, w * 0.48f, h * 0.35f)
                    }
                    drawPath(bird2, color = Color(0xFF0284C7).copy(alpha = 0.4f), style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round))

                    // 4. Background gentle rolling mountain/hill (Sage Green)
                    val backHill = Path().apply {
                        moveTo(0f, h * 0.62f)
                        cubicTo(w * 0.28f, h * 0.52f, w * 0.62f, h * 0.60f, w, h * 0.54f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(backHill, color = Color(0xFF86EFAC))

                    // 5. Foreground rich meadow hill (Lush Emerald)
                    val frontHill = Path().apply {
                        moveTo(0f, h * 0.72f)
                        cubicTo(w * 0.38f, h * 0.78f, w * 0.68f, h * 0.65f, w, h * 0.73f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(frontHill, color = Color(0xFF10B981))

                    // 6. Fresh organic sprout & blades of grass in the breeze
                    val sproutBaseX = w * 0.26f
                    val sproutBaseY = h * 0.82f
                    val tipX = sproutBaseX + leafSway.dp.toPx()

                    // Central stem
                    val stem = Path().apply {
                        moveTo(sproutBaseX, sproutBaseY)
                        quadraticTo(sproutBaseX, sproutBaseY - 10.dp.toPx(), tipX, sproutBaseY - 18.dp.toPx())
                    }
                    drawPath(stem, color = Color(0xFF065F46), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

                    // Left leaf
                    val leftLeaf = Path().apply {
                        moveTo(sproutBaseX, sproutBaseY - 8.dp.toPx())
                        quadraticTo(sproutBaseX - 8.dp.toPx(), sproutBaseY - 14.dp.toPx(), sproutBaseX - 10.dp.toPx() + leafSway.dp.toPx() * 0.5f, sproutBaseY - 12.dp.toPx())
                        quadraticTo(sproutBaseX - 5.dp.toPx(), sproutBaseY - 7.dp.toPx(), sproutBaseX, sproutBaseY - 8.dp.toPx())
                        close()
                    }
                    drawPath(leftLeaf, color = Color(0xFF047857))

                    // Right leaf
                    val rightLeaf = Path().apply {
                        moveTo(sproutBaseX, sproutBaseY - 11.dp.toPx())
                        quadraticTo(sproutBaseX + 8.dp.toPx(), sproutBaseY - 17.dp.toPx(), sproutBaseX + 11.dp.toPx() + leafSway.dp.toPx() * 0.5f, sproutBaseY - 15.dp.toPx())
                        quadraticTo(sproutBaseX + 5.dp.toPx(), sproutBaseY - 9.dp.toPx(), sproutBaseX, sproutBaseY - 11.dp.toPx())
                        close()
                    }
                    drawPath(rightLeaf, color = Color(0xFF047857))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Soothing Text Column
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = natureQuote.first,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            fontSize = 10.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = natureQuote.second.first,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = natureQuote.second.second,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * Cheeky Trolling Mascot for Early Unlock Dialog:
 * Shown when the user attempts to stop a session before the timer finishes!
 * Features an animated teasing troll character questioning the user's resolve.
 */
@Composable
fun CheekyTrollEarlyUnlockHeader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "troll_anim")

    // Smug head tilt animation
    val tiltAngle by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )

    // Eyebrow twitch
    val twitchOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twitch"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated Smug Troll Mascot Face
        Box(
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer(rotationZ = tiltAngle),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(72.dp)) {
                val w = size.width
                val h = size.height

                // Head (Warm Amber circle)
                drawCircle(
                    color = Color(0xFFFDE68A),
                    radius = w * 0.45f,
                    center = Offset(w / 2f, h / 2f)
                )

                // Face outline
                drawCircle(
                    color = Color(0xFFD97706),
                    radius = w * 0.45f,
                    center = Offset(w / 2f, h / 2f),
                    style = Stroke(width = 2.5.dp.toPx())
                )

                // Left Eye: Raised, wide-eyed side glance
                val leftEyeCenter = Offset(w * 0.35f, h * 0.42f)
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = 4.5.dp.toPx(),
                    center = leftEyeCenter
                )
                // Raised Eyebrow (twitching)
                val eyebrowPath = Path().apply {
                    moveTo(w * 0.22f, h * 0.32f + twitchOffset.dp.toPx())
                    quadraticTo(
                        w * 0.35f, h * 0.22f + twitchOffset.dp.toPx(),
                        w * 0.45f, h * 0.32f + twitchOffset.dp.toPx()
                    )
                }
                drawPath(
                    path = eyebrowPath,
                    color = Color(0xFFB45309),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Right Eye: Smug wink / half-closed smirk eye
                val rightEyePath = Path().apply {
                    moveTo(w * 0.58f, h * 0.44f)
                    quadraticTo(
                        w * 0.68f, h * 0.38f,
                        w * 0.78f, h * 0.44f
                    )
                }
                drawPath(
                    path = rightEyePath,
                    color = Color(0xFF1E293B),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Sarcastic Smug Grin (Curving upward to the right)
                val smirkPath = Path().apply {
                    moveTo(w * 0.32f, h * 0.65f)
                    quadraticTo(
                        w * 0.50f, h * 0.72f,
                        w * 0.72f, h * 0.58f
                    )
                }
                drawPath(
                    path = smirkPath,
                    color = Color(0xFFB45309),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Cheek blush (teasing pink dots)
                drawCircle(
                    color = Color(0xFFFCA5A5).copy(alpha = 0.6f),
                    radius = 5.dp.toPx(),
                    center = Offset(w * 0.22f, h * 0.52f)
                )
                drawCircle(
                    color = Color(0xFFFCA5A5).copy(alpha = 0.6f),
                    radius = 5.dp.toPx(),
                    center = Offset(w * 0.78f, h * 0.52f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Troll Title & Roasting Micro-copy
        Text(
            text = "Quitting So Soon? 👀",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = AppleTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = AppleCoralContainer.copy(alpha = 0.6f),
            border = BorderStroke(0.5.dp, AppleCoral.copy(alpha = 0.3f))
        ) {
            Text(
                text = "CAUTION: WEAK WILLPOWER DETECTED",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = AppleCoral,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your secret passcode of shame to surrender early and return to doomscrolling.",
            style = MaterialTheme.typography.bodySmall,
            color = AppleTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

// ==========================================
// STUDIO-GRADE FOCUS STAGES (1m – 360m)
// Continuous Lottie vector animations with
// varied, poetic, and grounding Apple-grade copy
// ==========================================

data class StudioFocusStage(
    val badge: String,
    val title: String,
    val headline: String,
    val caption: String,
    val assetName: String,
    val containerColor: Color,
    val accentColor: Color
)

fun getStudioFocusStage(minutes: Int): StudioFocusStage {
    return when {
        minutes <= 2 -> StudioFocusStage(
            badge = "1 – 2 MINS",
            title = "Breaking Tether",
            headline = "Sever the feed.",
            caption = "Notice the unconscious impulse to reach for your screen. Dopamine fades; the real world waits just past the bezel.",
            assetName = "animations/stage_phone.json",
            containerColor = Color(0xFFF1F5F9),
            accentColor = Color(0xFF475569)
        )
        minutes <= 5 -> StudioFocusStage(
            badge = "3 – 5 MINS",
            title = "Screen Slump Reset",
            headline = "Reclaim your stillness.",
            caption = "Five minutes of intentional quiet to break the doomscroll autopilot. Take one deep, grounding breath.",
            assetName = "animations/stage_phone.json",
            containerColor = Color(0xFFF1F5F9),
            accentColor = Color(0xFF475569)
        )
        minutes <= 15 -> StudioFocusStage(
            badge = "6 – 15 MINS",
            title = "Sensory Return",
            headline = "Feet on the earth.",
            caption = "Breath lengthens. The horizon widens when notifications cease. Thoughts untangle in natural light.",
            assetName = "animations/stage_walk.json",
            containerColor = Color(0xFFECFDF5),
            accentColor = Color(0xFF059669)
        )
        minutes <= 25 -> StudioFocusStage(
            badge = "16 – 25 MINS",
            title = "Mindful Cadence",
            headline = "Thoughts untangle in the open.",
            caption = "A focused 25-minute Pomodoro. Ground yourself in quiet forward motion and undivided clarity.",
            assetName = "animations/stage_walk.json",
            containerColor = Color(0xFFECFDF5),
            accentColor = Color(0xFF059669)
        )
        minutes <= 45 -> StudioFocusStage(
            badge = "26 – 45 MINS",
            title = "Active Flow",
            headline = "Momentum takes over.",
            caption = "Heart rate elevates, mental friction dissolves. When the screen goes dark, deep concentration takes flight.",
            assetName = "animations/stage_run.json",
            containerColor = Color(0xFFEEF2FF),
            accentColor = Color(0xFF4F46E5)
        )
        minutes <= 60 -> StudioFocusStage(
            badge = "46 – 60 MINS",
            title = "Unbroken Stride",
            headline = "Mental clarity follows stride.",
            caption = "One uninterrupted hour of athletic focus. Rare, powerful, and entirely reclaimed for what matters.",
            assetName = "animations/stage_run.json",
            containerColor = Color(0xFFEEF2FF),
            accentColor = Color(0xFF4F46E5)
        )
        minutes <= 120 -> StudioFocusStage(
            badge = "1 – 2 HOURS",
            title = "Boundless Horizon",
            headline = "Living life unleashed.",
            caption = "Gliding across open terrain with boundless vitality. You are not an observer of life—you are moving inside it.",
            assetName = "animations/stage_sport.json",
            containerColor = Color(0xFFFEF3C7),
            accentColor = Color(0xFFD97706)
        )
        else -> StudioFocusStage(
            badge = "${minutes / 60}H ${minutes % 60}M",
            title = "Digital Liberation",
            headline = "Total embodiment in the real world.",
            caption = "Sustained mastery. Hours of total freedom from the feed, fully immersed in your work and living life.",
            assetName = "animations/stage_sport.json",
            containerColor = Color(0xFFFEF3C7),
            accentColor = Color(0xFFD97706)
        )
    }
}

/**
 * Studio-Grade Focus Stage Animation.
 * Replaces rudimentary canvas graphics with authentic, continuous 60 FPS Lottie vector animations,
 * accompanied by dynamic, poetic Apple-grade editorial reflections that evolve across 8 duration stages.
 */
@Composable
fun DynamicChildLifeAnimation(
    durationMinutes: Int,
    modifier: Modifier = Modifier
) {
    val stage = remember(durationMinutes) { getStudioFocusStage(durationMinutes) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = AppleLightCard,
        border = BorderStroke(0.5.dp, AppleLightBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stage Indicator Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = stage.containerColor,
                border = BorderStroke(0.5.dp, stage.accentColor.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(stage.accentColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stage.badge} • ${stage.title.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                        fontWeight = FontWeight.Bold,
                        color = stage.accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Studio-Grade Lottie Vector Animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = stage.assetName,
                    animationSpec = tween(350),
                    label = "stageLottieCrossfade"
                ) { asset ->
                    StudioLottiePlayer(
                        assetName = asset,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Editorial Headline
            Text(
                text = stage.headline,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp
                ),
                color = AppleTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Varied, Poetic Reflective Micro-Copy
            Text(
                text = stage.caption,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                ),
                color = AppleTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun StudioLottiePlayer(
    assetName: String,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetName))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

