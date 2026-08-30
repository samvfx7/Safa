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
    val message: String
)

class PrayerMatDetector {

    private val _detectionState = MutableStateFlow(
        MatDetectionResult(
            isDetected = false,
            confidence = 0f,
            detectedColorName = "Scanning...",
            patternScore = 0f,
            message = "Align prayer mat inside the golden frame"
        )
    )
    val detectionState: StateFlow<MatDetectionResult> = _detectionState.asStateFlow()

    private var consecutiveSuccessCount = 0
    private var failedScanAttempts = 0

    // Analyzes a camera bitmap or frame buffer
    fun analyzeFrame(bitmap: Bitmap): MatDetectionResult {
        val width = bitmap.width
        val height = bitmap.height

        var greenPixels = 0
        var bluePixels = 0
        var redBurgundyPixels = 0
        var brownGoldPixels = 0
        var totalSampled = 0

        // Sample grid points across the center region (where the mat is framed)
        val startX = (width * 0.2).toInt()
        val endX = (width * 0.8).toInt()
        val startY = (height * 0.2).toInt()
        val endY = (height * 0.8).toInt()
        val step = 12

        var prevLum = -1
        var edgeTransitions = 0

        for (x in startX until endX step step) {
            for (y in startY until endY step step) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                val hsv = FloatArray(3)
                Color.RGBToHSV(r, g, b, hsv)
                val hue = hsv[0]
                val sat = hsv[1]
                val value = hsv[2]

                // Color classification for prayer mats:
                // 1. Green (hue 70-170, sat > 0.25)
                if (hue in 75f..165f && sat > 0.2f && value > 0.2f) {
                    greenPixels++
                }
                // 2. Royal Blue / Navy (hue 180-260, sat > 0.25)
                else if (hue in 180f..255f && sat > 0.2f && value > 0.2f) {
                    bluePixels++
                }
                // 3. Red / Burgundy / Crimson (hue < 20 or > 340, sat > 0.25)
                else if ((hue < 25f || hue > 335f) && sat > 0.25f && value > 0.25f) {
                    redBurgundyPixels++
                }
                // 4. Brown / Gold / Amber (hue 25-55, sat > 0.25)
                else if (hue in 25f..65f && sat > 0.25f && value > 0.25f) {
                    brownGoldPixels++
                }

                // Geometric texture / border symmetry
                val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                if (prevLum != -1 && abs(lum - prevLum) > 35) {
                    edgeTransitions++
                }
                prevLum = lum
                totalSampled++
            }
        }

        if (totalSampled == 0) totalSampled = 1

        val dominantCount = maxOf(greenPixels, bluePixels, redBurgundyPixels, brownGoldPixels)
        val colorDominance = (dominantCount.toFloat() / totalSampled.toFloat())

        val colorName = when (dominantCount) {
            greenPixels -> "Emerald Islamic Green"
            bluePixels -> "Royal Blue"
            redBurgundyPixels -> "Burgundy / Crimson"
            brownGoldPixels -> "Warm Gold / Amber"
            else -> "Neutral"
        }

        // Rectangular mat pattern score: carpet weave has dense edge transitions with dominant color
        val patternScore = (edgeTransitions.toFloat() / (totalSampled * 0.4f)).coerceIn(0f, 1f)

        // Confidence combining color match & pattern symmetry
        val confidence = ((colorDominance * 1.5f + patternScore * 0.6f) / 2.0f).coerceIn(0f, 1f)

        val isDetected = confidence >= 0.70f

        val result = if (isDetected) {
            consecutiveSuccessCount++
            MatDetectionResult(
                isDetected = true,
                confidence = confidence,
                detectedColorName = colorName,
                patternScore = patternScore,
                message = "Mat detected! ✓ ($colorName)"
            )
        } else {
            consecutiveSuccessCount = 0
            MatDetectionResult(
                isDetected = false,
                confidence = confidence,
                detectedColorName = colorName,
                patternScore = patternScore,
                message = "Align prayer mat inside the golden frame (${(confidence * 100).toInt()}%)"
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
            message = "Align prayer mat inside the golden frame"
        )
    }
}
