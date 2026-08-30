package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class QiblaCompassState(
    val azimuthDegrees: Float = 0f,
    val qiblaBearingDegrees: Float = 119f, // Bearing from current location to Mecca
    val qiblaDirectionRelativeToDevice: Float = 0f, // (qiblaBearing - azimuth)
    val distanceToMeccaKm: Double = 4500.0,
    val isAlignedWithQibla: Boolean = false, // Within +- 4 degrees
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
)

class QiblaCompassManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _compassState = MutableStateFlow(QiblaCompassState())
    val compassState: StateFlow<QiblaCompassState> = _compassState.asStateFlow()

    private var currentLatitude: Double = 51.5074
    private var currentLongitude: Double = -0.1278

    // Kaaba Coordinates (Mecca, Saudi Arabia)
    companion object {
        const val MECCA_LATITUDE = 21.422487
        const val MECCA_LONGITUDE = 39.826206
    }

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private val gravityValues = FloatArray(3)
    private val magneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasMagnetic = false

    private var smoothedAzimuth = 0f
    private val alpha = 0.15f // Low pass filter factor

    fun updateCoordinates(lat: Double, lng: Double) {
        currentLatitude = lat
        currentLongitude = lng
        val bearing = calculateQiblaBearing(lat, lng)
        val distance = calculateDistanceToMecca(lat, lng)

        _compassState.value = _compassState.value.copy(
            qiblaBearingDegrees = bearing,
            distanceToMeccaKm = distance
        )
    }

    fun startListening() {
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometerSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            magnetometerSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                if (azimuth < 0) azimuth += 360f
                updateOrientation(azimuth)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravityValues, 0, 3)
                hasGravity = true
                computeOrientationFromGravityAndMagnetic()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magneticValues, 0, 3)
                hasMagnetic = true
                computeOrientationFromGravityAndMagnetic()
            }
        }
    }

    private fun computeOrientationFromGravityAndMagnetic() {
        if (hasGravity && hasMagnetic) {
            if (SensorManager.getRotationMatrix(rotationMatrix, null, gravityValues, magneticValues)) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                if (azimuth < 0) azimuth += 360f
                updateOrientation(azimuth)
            }
        }
    }

    private fun updateOrientation(newAzimuth: Float) {
        // Handle angle wrap around for low pass filter
        var diff = newAzimuth - smoothedAzimuth
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f

        smoothedAzimuth = (smoothedAzimuth + alpha * diff + 360f) % 360f

        val qiblaBearing = calculateQiblaBearing(currentLatitude, currentLongitude)
        var relativeDirection = qiblaBearing - smoothedAzimuth
        if (relativeDirection < 0) relativeDirection += 360f

        val isAligned = kotlin.math.abs(relativeDirection) <= 4.0f ||
                kotlin.math.abs(relativeDirection - 360f) <= 4.0f

        _compassState.value = _compassState.value.copy(
            azimuthDegrees = smoothedAzimuth,
            qiblaBearingDegrees = qiblaBearing,
            qiblaDirectionRelativeToDevice = relativeDirection,
            distanceToMeccaKm = calculateDistanceToMecca(currentLatitude, currentLongitude),
            isAlignedWithQibla = isAligned
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _compassState.value = _compassState.value.copy(accuracy = accuracy)
    }

    // Great-Circle bearing calculation to Kaaba
    private fun calculateQiblaBearing(lat: Double, lng: Double): Float {
        val latRad = Math.toRadians(lat)
        val lngRad = Math.toRadians(lng)
        val meccaLatRad = Math.toRadians(MECCA_LATITUDE)
        val meccaLngRad = Math.toRadians(MECCA_LONGITUDE)

        val deltaLng = meccaLngRad - lngRad

        val y = sin(deltaLng) * cos(meccaLatRad)
        val x = cos(latRad) * sin(meccaLatRad) - sin(latRad) * cos(meccaLatRad) * cos(deltaLng)

        var bearing = Math.toDegrees(atan2(y, x))
        if (bearing < 0) bearing += 360.0

        return bearing.toFloat()
    }

    // Haversine distance in km
    private fun calculateDistanceToMecca(lat: Double, lng: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(MECCA_LATITUDE - lat)
        val dLng = Math.toRadians(MECCA_LONGITUDE - lng)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(MECCA_LATITUDE)) *
                sin(dLng / 2) * sin(dLng / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
