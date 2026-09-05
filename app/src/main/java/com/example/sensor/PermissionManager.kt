package com.example.sensor

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class AppPermissionState(
    val hasLocationPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val hasExactAlarmPermission: Boolean = true,
    val isInitialPromptShown: Boolean = false
)

data class DeviceLocationResult(
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val country: String
)

class PermissionManager(private val context: Context) {

    private val prefs by lazy { context.getSharedPreferences("safa_permissions_pref", Context.MODE_PRIVATE) }

    private val _permissionState = MutableStateFlow(AppPermissionState())
    val permissionState: StateFlow<AppPermissionState> = _permissionState.asStateFlow()

    init {
        checkAllPermissions()
    }

    companion object {
        val REQUIRED_LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val ALL_APP_PERMISSIONS: Array<String>
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.CAMERA
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA
                )
            }
    }

    fun checkAllPermissions(): AppPermissionState {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasLocation = hasFine || hasCoarse

        val hasNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notification permission automatically granted on Android < 13
        }

        val hasCamera = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val hasExactAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.canScheduleExactAlarms() ?: true
        } else {
            true
        }

        val isShown = prefs.getBoolean("has_prompted_initial_permissions", false)

        val state = AppPermissionState(
            hasLocationPermission = hasLocation,
            hasNotificationPermission = hasNotification,
            hasCameraPermission = hasCamera,
            hasExactAlarmPermission = hasExactAlarm,
            isInitialPromptShown = isShown
        )
        _permissionState.value = state
        return state
    }

    fun markInitialPromptShown() {
        prefs.edit().putBoolean("has_prompted_initial_permissions", true).apply()
        _permissionState.value = _permissionState.value.copy(isInitialPromptShown = true)
    }

    fun shouldShowInitialPrompt(): Boolean {
        checkAllPermissions()
        val state = _permissionState.value
        return !state.isInitialPromptShown || (!state.hasLocationPermission && !state.hasNotificationPermission)
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openNotificationSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // Fallback to app settings
            }
        }
        openAppSettings(context)
    }

    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                openAppSettings(context)
            }
        } else {
            openAppSettings(context)
        }
    }

    /**
     * Attempts to retrieve real device location via GPS or Network providers.
     */
    suspend fun getDeviceCurrentLocation(): DeviceLocationResult? = withContext(Dispatchers.IO) {
        val hasLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocation) return@withContext null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null

        var bestLocation: Location? = null

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (bestLocation == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            if (bestLocation == null && locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            }
        } catch (e: SecurityException) {
            return@withContext null
        } catch (e: Exception) {
            // Ignore
        }

        if (bestLocation == null) {
            // Try single update
            bestLocation = suspendCancellableCoroutine<Location?> { cont ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(loc: Location) {
                        try {
                            locationManager.removeUpdates(this)
                        } catch (_: Exception) {}
                        if (cont.isActive) cont.resume(loc)
                    }
                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                    override fun onProviderEnabled(p0: String) {}
                    override fun onProviderDisabled(p0: String) {}
                }

                try {
                    val provider = when {
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                        else -> null
                    }
                    if (provider != null) {
                        locationManager.requestSingleUpdate(provider, listener, null)
                        cont.invokeOnCancellation {
                            try {
                                locationManager.removeUpdates(listener)
                            } catch (_: Exception) {}
                        }
                    } else {
                        if (cont.isActive) cont.resume(null)
                    }
                } catch (e: Exception) {
                    if (cont.isActive) cont.resume(null)
                }
            }
        }

        if (bestLocation != null) {
            val lat = bestLocation.latitude
            val lng = bestLocation.longitude

            // Reverse geocode
            var cityName = "Current Location"
            var countryName = "Global"

            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    cityName = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "Current Location"
                    countryName = addr.countryName ?: "Global"
                }
            } catch (e: Exception) {
                // Keep default names if geocoder offline
            }

            return@withContext DeviceLocationResult(
                latitude = lat,
                longitude = lng,
                city = cityName,
                country = countryName
            )
        }

        return@withContext null
    }
}
