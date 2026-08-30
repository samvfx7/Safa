package com.example.ui.permission

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.IslamicApp
import com.example.sensor.AppPermissionState
import com.example.sensor.PermissionManager
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import kotlinx.coroutines.launch

@Composable
fun SafaPermissionDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onPermissionsUpdated: () -> Unit = {}
) {
    if (!isOpen) return

    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp
    val permissionManager = app.permissionManager
    val scope = rememberCoroutineScope()
    val permState by permissionManager.permissionState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionManager.checkAllPermissions()
        permissionManager.markInitialPromptShown()

        // If location granted, attempt auto GPS update
        if (result.any { it.key.contains("LOCATION") && it.value }) {
            scope.launch {
                val loc = permissionManager.getDeviceCurrentLocation()
                if (loc != null) {
                    app.settingsRepository.updateLocation(
                        city = loc.city,
                        country = loc.country,
                        lat = loc.latitude,
                        lng = loc.longitude
                    )
                    app.prayerRepository.refreshPrayerTimes(force = true)
                    app.qiblaCompassManager.updateCoordinates(loc.latitude, loc.longitude)
                }
                onPermissionsUpdated()
            }
        } else {
            onPermissionsUpdated()
        }
    }

    val safaColors = LocalSafaColors.current

    Dialog(
        onDismissRequest = {
            permissionManager.markInitialPromptShown()
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .shadow(24.dp, RoundedCornerShape(28.dp))
                .testTag("safa_permission_dialog"),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Icon with Gold Glow
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    safaColors.goldGlow.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(1.5.dp, safaColors.goldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Title & Subtitle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Permissions & Accuracy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Enable permissions to unlock accurate prayer times, real-time Qibla alignment, and Adhan alerts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = safaColors.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }

                // Permission Cards List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PermissionItemCard(
                        icon = Icons.Default.LocationOn,
                        title = "Precise Location (GPS)",
                        description = "Calculates exact local prayer times and orients the live Kaaba compass.",
                        isGranted = permState.hasLocationPermission,
                        isRequired = true
                    )

                    PermissionItemCard(
                        icon = Icons.Default.Notifications,
                        title = "Adhan & Prayer Alerts",
                        description = "Delivers timely notifications, Fajr alarms, and daily Islamic reminders.",
                        isGranted = permState.hasNotificationPermission,
                        isRequired = false
                    )

                    PermissionItemCard(
                        icon = Icons.Default.CameraAlt,
                        title = "Camera & Mat Scanner",
                        description = "Enables AR Qibla direction view and interactive prayer mat detection.",
                        isGranted = permState.hasCameraPermission,
                        isRequired = false
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allGranted = permState.hasLocationPermission &&
                            permState.hasNotificationPermission &&
                            permState.hasCameraPermission

                    if (!allGranted) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(PermissionManager.ALL_APP_PERMISSIONS)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("grant_permissions_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = safaColors.goldPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Grant Permissions",
                                color = SafaNavyDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                permissionManager.openAppSettings(context)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = safaColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Device Settings",
                                    color = safaColors.textSecondary,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                permissionManager.markInitialPromptShown()
                                onDismiss()
                            }
                        ) {
                            Text(
                                text = if (allGranted) "Done" else "Later",
                                color = safaColors.goldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionItemCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    isRequired: Boolean
) {
    val safaColors = LocalSafaColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) {
                safaColors.goldGlow.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        border = BorderStroke(
            1.dp,
            if (isGranted) {
                safaColors.goldPrimary.copy(alpha = 0.4f)
            } else {
                safaColors.navyBorder.copy(alpha = 0.25f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isGranted) safaColors.goldGlow.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) safaColors.goldPrimary else safaColors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary
                    )
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = safaColors.textSecondary,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                )
            }

            // Status Badge
            if (isGranted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = safaColors.goldPrimary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = safaColors.goldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Allowed",
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.goldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, safaColors.navyBorder.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = if (isRequired) "Required" else "Optional",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

/**
 * Compact permission request banner for individual screens (e.g. Prayer Times, Qibla)
 */
@Composable
fun LocationPermissionBanner(
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safaColors = LocalSafaColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onGrantClick() }
            .testTag("location_permission_banner"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = safaColors.goldGlow.copy(alpha = 0.25f)
        ),
        border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(safaColors.goldPrimary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Enable GPS Location",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary
                )
                Text(
                    text = "Get automatic prayer times & precise Qibla for your exact city.",
                    style = MaterialTheme.typography.bodySmall,
                    color = safaColors.textSecondary,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onGrantClick,
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Enable",
                    color = SafaNavyDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
