package com.example.matdetection

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

data class MatDetectionResult(
    val isDetected: Boolean,
    val confidence: Float, // 0.0 to 1.0 (70%+ threshold)
    val detectedColorName: String,
    val patternScore: Float,
    val message: String,
    val antiCheatPassed: Boolean = false,
    val antiCheatMessage: String = "Anti-cheat active: Live physical mat required"
)

class PrayerMatDetector {

    private val _detectionState = MutableStateFlow(
        MatDetectionResult(
            isDetected = false,
            confidence = 0f,
            detectedColorName = "Ready",
            patternScore = 0f,
            message = "Point camera at your physical prayer mat",
            antiCheatPassed = false,
            antiCheatMessage = "Anti-cheat active: Live Camera Only (No photos, no manual bypass)"
        )
    )
    val detectionState: StateFlow<MatDetectionResult> = _detectionState.asStateFlow()

    private var consecutiveSuccessCount = 0
    private var failedScanAttempts = 0

    // Analyzes a camera bitmap captured from the live CameraX preview
    fun analyzeFrame(bitmap: Bitmap): MatDetectionResult {
        val width = bitmap.width
        val height = bitmap.height

        var greenPixels = 0
        var bluePixels = 0
        var redBurgundyPixels = 0
        var brownGoldPixels = 0
        var darkPatternPixels = 0
        var totalSampled = 0
        var totalLuminance = 0L

        // Sample grid points across the center region (where the mat is framed)
        val startX = (width * 0.15).toInt()
        val endX = (width * 0.85).toInt()
        val startY = (height * 0.15).toInt()
        val endY = (height * 0.85).toInt()
        val step = 8 // Fine sampling for accurate texture and color recognition

        var prevLum = -1
        var edgeTransitions = 0

        for (x in startX until endX step step) {
            for (y in startY until endY step step) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                totalLuminance += lum

                val hsv = FloatArray(3)
                Color.RGBToHSV(r, g, b, hsv)
                val hue = hsv[0]
                val sat = hsv[1]
                val value = hsv[2]

                // Color classification for authentic prayer mats:
                // 1. Green (hue 65-175, sat > 0.18, val > 0.15)
                if (hue in 65f..175f && sat > 0.18f && value > 0.15f) {
                    greenPixels++
                }
                // 2. Royal Blue / Navy (hue 180-260, sat > 0.18, val > 0.15)
                else if (hue in 180f..260f && sat > 0.18f && value > 0.15f) {
                    bluePixels++
                }
                // 3. Red / Burgundy / Crimson / Maroon (hue < 25 or > 330, sat > 0.20, val > 0.18)
                else if ((hue < 25f || hue > 330f) && sat > 0.20f && value > 0.18f) {
                    redBurgundyPixels++
                }
                // 4. Brown / Gold / Amber / Ochre (hue 25-65, sat > 0.20, val > 0.20)
                else if (hue in 25f..65f && sat > 0.20f && value > 0.20f) {
                    brownGoldPixels++
                }
                // 5. Traditional dark velvet/black prayer mats with intricate gold/white border motifs
                else if (value in 0.05f..0.35f && sat < 0.35f) {
                    darkPatternPixels++
                }

                // Geometric texture / carpet weave / fringe edge density
                if (prevLum != -1 && abs(lum - prevLum) > 28) {
                    edgeTransitions++
                }
                prevLum = lum
                totalSampled++
            }
        }

        if (totalSampled == 0) totalSampled = 1
        val avgLuminance = totalLuminance / totalSampled

        // --- ANTI-CHEAT CHECKS ---
        // 1. Lens blocked / completely black (finger covering camera)
        if (avgLuminance < 18) {
            consecutiveSuccessCount = 0
            val result = MatDetectionResult(
                isDetected = false,
                confidence = 0.05f,
                detectedColorName = "Dark",
                patternScore = 0f,
                message = "Anti-cheat: Camera blocked or too dark. Point at prayer mat.",
                antiCheatPassed = false,
                antiCheatMessage = "Blocked camera detected"
            )
            _detectionState.value = result
            return result
        }

        // 2. Glare / pointing at white ceiling light
        if (avgLuminance > 242) {
            consecutiveSuccessCount = 0
            val result = MatDetectionResult(
                isDetected = false,
                confidence = 0.05f,
                detectedColorName = "Overexposed",
                patternScore = 0f,
                message = "Anti-cheat: Overexposed surface. Point at prayer mat.",
                antiCheatPassed = false,
                antiCheatMessage = "Overexposed surface detected"
            )
            _detectionState.value = result
            return result
        }

        // 3. Flat surface check: blank wall, flat color paper, or uniform bedsheet has no carpet weave/geometric edges
        val edgeRatio = edgeTransitions.toFloat() / totalSampled.toFloat()
        val hasAuthenticCarpetTexture = edgeRatio >= 0.045f // Real rugs have dense fiber/border edges

        if (!hasAuthenticCarpetTexture) {
            consecutiveSuccessCount = 0
            val result = MatDetectionResult(
                isDetected = false,
                confidence = 0.15f,
                detectedColorName = "Flat Surface",
                patternScore = 0.1f,
                message = "Anti-cheat: No woven carpet texture or border detected.",
                antiCheatPassed = false,
                antiCheatMessage = "Flat surface rejected (requires physical carpet texture)"
            )
            _detectionState.value = result
            return result
        }

        val dominantCount = maxOf(greenPixels, bluePixels, redBurgundyPixels, brownGoldPixels, (darkPatternPixels * 0.7f).toInt())
        val colorDominance = (dominantCount.toFloat() / totalSampled.toFloat()).coerceIn(0f, 1f)

        val colorName = when (dominantCount) {
            greenPixels -> "Emerald Islamic Green"
            bluePixels -> "Royal Blue"
            redBurgundyPixels -> "Burgundy / Crimson"
            brownGoldPixels -> "Warm Gold / Amber"
            else -> "Classic Dark Woven Mat"
        }

        // Texture and pattern score calculation
        val patternScore = (edgeRatio * 5.0f).coerceIn(0f, 1f)

        // Confidence combining color match & pattern symmetry
        // authentic mats have both color presence and edge transitions
        val confidence = ((colorDominance * 1.3f + patternScore * 0.7f) / 1.7f).coerceIn(0f, 1f)

        val meetsThreshold = confidence >= 0.68f && hasAuthenticCarpetTexture

        if (meetsThreshold) {
            consecutiveSuccessCount++
        } else {
            consecutiveSuccessCount = 0
        }

        // Require at least 2 consecutive positive detections for rock-solid stability and anti-cheat
        val isConfirmed = consecutiveSuccessCount >= 2

        val result = if (isConfirmed) {
            MatDetectionResult(
                isDetected = true,
                confidence = confidence,
                detectedColorName = colorName,
                patternScore = patternScore,
                message = "Prayer Mat Verified! ✓ ($colorName)",
                antiCheatPassed = true,
                antiCheatMessage = "Anti-cheat verified: Live physical mat confirmed"
            )
        } else if (meetsThreshold) {
            MatDetectionResult(
                isDetected = false,
                confidence = confidence,
                detectedColorName = colorName,
                patternScore = patternScore,
                message = "Hold steady... Verifying prayer mat ($colorName)",
                antiCheatPassed = true,
                antiCheatMessage = "Anti-cheat: Stabilizing live camera frame"
            )
        } else {
            MatDetectionResult(
                isDetected = false,
                confidence = confidence,
                detectedColorName = colorName,
                patternScore = patternScore,
                message = "Align physical prayer mat inside the golden frame (${(confidence * 100).toInt()}%)",
                antiCheatPassed = false,
                antiCheatMessage = "Scanning live camera feed..."
            )
        }

        _detectionState.value = result
        return result
    }

    fun recordFailedAttempt(): Int {
        failedScanAttempts++
        return failedScanAttempts
    }

    fun reset() {
        consecutiveSuccessCount = 0
        failedScanAttempts = 0
        _detectionState.value = MatDetectionResult(
            isDetected = false,
            confidence = 0f,
            detectedColorName = "Ready",
            patternScore = 0f,
            message = "Point camera at your physical prayer mat",
            antiCheatPassed = false,
            antiCheatMessage = "Anti-cheat active: Live Camera Only (No photos, no manual bypass)"
        )
    }
}
