package com.example.ui.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Custom Easing specs as per M3 guidelines & Islamic aesthetic polish.
 */
val EaseInOutCubic = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1.000f)
val EaseOutCubic = CubicBezierEasing(0.215f, 0.610f, 0.355f, 1.000f)
val FastOutSlowIn = FastOutSlowInEasing

/**
 * Modifier that scales an element down slightly (0.95f) when pressed with spring bounce.
 */
fun Modifier.pressScale(
    pressedScale: Float = 0.95f
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "pressScaleAnimation"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Staggered entrance animation for list items (fade + slight slide up).
 */
fun Modifier.staggeredEntrance(
    index: Int,
    delayPerItemMs: Int = 35,
    maxDelayMs: Int = 350
): Modifier = composed {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val calculatedDelay = (index * delayPerItemMs).coerceAtMost(maxDelayMs)
        delay(calculatedDelay.toLong())
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 350, easing = EaseOutCubic),
        label = "staggerAlpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else 28f,
        animationSpec = tween(durationMillis = 380, easing = EaseOutCubic),
        label = "staggerTranslationY"
    )

    this.graphicsLayer {
        this.alpha = alpha
        this.translationY = translationY.dp.toPx()
    }
}

/**
 * Pulse scale modifier for subtle highlight/call-to-action attention (e.g. Next Prayer Card).
 */
fun Modifier.subtlePulse(
    enabled: Boolean = true,
    pulsePeriodMs: Int = 2000,
    minScale: Float = 1.0f,
    maxScale: Float = 1.015f
): Modifier = composed {
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteSubtlePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(pulsePeriodMs / 2, easing = FastOutSlowIn),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    this.graphicsLayer {
        scaleX = pulseScale
        scaleY = pulseScale
    }
}

/**
 * Animated number/text composable for smooth digit counter transitions.
 */
@Composable
fun AnimatedDigitText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null
) {
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            (slideInVertically(animationSpec = tween(280, easing = EaseOutCubic)) { height -> height / 2 } +
                    fadeIn(tween(200))) togetherWith
                    (slideOutVertically(animationSpec = tween(200, easing = EaseOutCubic)) { height -> -height / 2 } +
                            fadeOut(tween(160)))
        },
        label = "animatedDigitText",
        modifier = modifier
    ) { targetText ->
        androidx.compose.material3.Text(
            text = targetText,
            style = style,
            color = color,
            fontWeight = fontWeight ?: style.fontWeight
        )
    }
}

/**
 * Animated heart / bookmark button with spring bounce on click.
 */
@Composable
fun AnimatedHeartIconButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    activeTint: Color,
    inactiveTint: Color,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    var isTapped by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isTapped) 1.28f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        finishedListener = { isTapped = false },
        label = "heartScale"
    )

    IconButton(
        onClick = {
            isTapped = true
            onClick()
        },
        modifier = modifier.scale(scale)
    ) {
        Icon(
            imageVector = if (isSelected) activeIcon else inactiveIcon,
            contentDescription = contentDescription,
            tint = if (isSelected) activeTint else inactiveTint
        )
    }
}
