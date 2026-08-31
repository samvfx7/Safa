package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthProvider
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.ArabicTextStyle
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val safaColors = LocalSafaColors.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var googleEmailInput by remember { mutableStateOf("") }
    var googleNameInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    // Google Connect Modal Dialog for real email linking
    if (uiState.showGoogleConnectDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.showGoogleConnectDialog(false) }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(if (safaColors.isLuxuryNavy) Color.White else Color(0xFFF1F5F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            color = Color(0xFFEA4335)
                        )
                    }

                    Text(
                        text = "Connect Google Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary
                    )

                    Text(
                        text = "Enter your Google email to synchronize your daily prayer streak across all your devices.",
                        style = MaterialTheme.typography.bodySmall,
                        color = safaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = googleEmailInput,
                        onValueChange = { googleEmailInput = it },
                        label = { Text("Google Email (e.g. name@gmail.com)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = safaColors.goldPrimary
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("google_email_dialog_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = safaColors.goldPrimary,
                            unfocusedBorderColor = safaColors.goldBorder.copy(alpha = 0.4f)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                    )

                    OutlinedTextField(
                        value = googleNameInput,
                        onValueChange = { googleNameInput = it },
                        label = { Text("Display Name (Optional)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = safaColors.goldPrimary
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("google_name_dialog_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = safaColors.goldPrimary,
                            unfocusedBorderColor = safaColors.goldBorder.copy(alpha = 0.4f)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )

                    Button(
                        onClick = {
                            viewModel.connectGoogleAccount(
                                email = googleEmailInput,
                                displayName = googleNameInput.ifBlank { null }
                            ) {
                                onAuthSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("google_dialog_connect_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = safaColors.goldPrimary,
                            contentColor = SafaNavyDark
                        )
                    ) {
                        Text(
                            text = "Connect & Sync Streak",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    TextButton(
                        onClick = { viewModel.showGoogleConnectDialog(false) }
                    ) {
                        Text(
                            text = "Cancel",
                            color = safaColors.textSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.currentUser != null) "Account & Streak Sync" else "Sign In & Streak Backup",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = safaColors.goldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = SafaSpacing.screenHorizontalPadding),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
                    colors = CardDefaults.cardColors(
                        containerColor = if (safaColors.isLuxuryNavy) safaColors.navyElevated else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = if (safaColors.isLuxuryNavy) {
                                        listOf(safaColors.navyElevated, safaColors.navySurface)
                                    } else {
                                        listOf(Color(0xFFFFFDF7), Color(0xFFF7EFE0))
                                    }
                                )
                            )
                            .padding(22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(safaColors.goldGlow, CircleShape)
                                    .border(1.dp, safaColors.goldPrimary.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (uiState.currentUser != null) Icons.Default.AccountCircle else Icons.Outlined.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = safaColors.goldPrimary,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (uiState.currentUser != null) {
                                    uiState.currentUser?.displayName ?: "Active Believer"
                                } else {
                                    "Preserve Your Prayer Streak"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFF7A580B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.currentUser != null) {
                                    when (uiState.currentUser?.provider) {
                                        AuthProvider.GOOGLE -> "Connected via Google • Cloud Streak Active"
                                        AuthProvider.EMAIL -> "${uiState.currentUser?.email} • Verified Account"
                                        AuthProvider.GUEST -> "Guest Pilgrim • Local Streak Saved"
                                        else -> "Safa Account"
                                    }
                                } else {
                                    "Sign in with Google or Email to sync your daily prayer streak across all devices, or continue seamlessly as a guest."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = safaColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // If user is already logged in, display Account details & Switch account options
            if (uiState.currentUser != null) {
                val user = uiState.currentUser!!
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(SafaSpacing.cardRadius),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CURRENT SESSION",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.textSecondary,
                                    letterSpacing = 1.sp
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = safaColors.goldPrimary.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.4f))
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
                                            text = user.provider.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = safaColors.goldPrimary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(safaColors.goldGlow, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = safaColors.goldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = user.displayName ?: "Believer",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = safaColors.textPrimary
                                    )
                                    Text(
                                        text = user.email ?: "Guest Mode (Local UUID: ${user.uid.take(10)})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = safaColors.textSecondary
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = safaColors.goldBorder.copy(alpha = 0.25f),
                                thickness = 1.dp
                            )

                            // Streak Sync feature highlights
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FeatureMiniBadge(
                                    icon = Icons.Outlined.CloudDone,
                                    title = "Streak Synced",
                                    subtitle = "Persistent",
                                    modifier = Modifier.weight(1f)
                                )
                                FeatureMiniBadge(
                                    icon = Icons.Outlined.Security,
                                    title = "Encrypted",
                                    subtitle = "Safe & Private",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedButton(
                                onClick = { viewModel.signOut() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("sign_out_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFD4745C).copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFD4745C)
                                )
                            ) {
                                Text(
                                    text = "Sign Out / Switch Account",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // AUTHENTICATION OPTIONS

                // 1. Google Sign-In Primary Button
                item {
                    Button(
                        onClick = {
                            viewModel.signInWithGoogle(context) {
                                onAuthSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("google_sign_in_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (safaColors.isLuxuryNavy) Color.White else Color(0xFF1E293B),
                            contentColor = if (safaColors.isLuxuryNavy) Color(0xFF1E293B) else Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        enabled = !uiState.isLoading
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Google "G" icon visual
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (safaColors.isLuxuryNavy) Color(0xFFF1F5F9) else Color(0xFF334155),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (safaColors.isLuxuryNavy) Color(0xFFEA4335) else Color(0xFFFFD54F)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                item {
                    TextButton(
                        onClick = { viewModel.showGoogleConnectDialog(true) },
                        modifier = Modifier.fillMaxWidth().testTag("enter_google_email_direct_button")
                    ) {
                        Text(
                            text = "Or enter Google email directly",
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.goldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // 2. Guest Sign-In Option
                item {
                    OutlinedButton(
                        onClick = {
                            viewModel.signInAsGuest {
                                onAuthSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("guest_sign_in_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = safaColors.goldPrimary
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PersonOutline,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continue as Guest (Keep Local Streak)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }

                // Divider OR
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = safaColors.goldBorder.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "  OR USE EMAIL  ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textSecondary,
                            letterSpacing = 1.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = safaColors.goldBorder.copy(alpha = 0.3f)
                        )
                    }
                }

                // 3. Email & Password Form Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(SafaSpacing.cardRadius),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = if (uiState.isSignUpMode) "Create Email Account" else "Sign In with Email",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary
                            )

                            if (uiState.isSignUpMode) {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Full Name (Optional)") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = safaColors.goldPrimary
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_name_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = safaColors.goldPrimary,
                                        unfocusedBorderColor = safaColors.goldBorder.copy(alpha = 0.4f)
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                                )
                            }

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = safaColors.goldPrimary
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_email_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = safaColors.goldPrimary,
                                    unfocusedBorderColor = safaColors.goldBorder.copy(alpha = 0.4f)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = safaColors.goldPrimary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = safaColors.textSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_password_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = safaColors.goldPrimary,
                                    unfocusedBorderColor = safaColors.goldBorder.copy(alpha = 0.4f)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                    if (uiState.isSignUpMode) {
                                        viewModel.signUpWithEmail(email, password, name) { onAuthSuccess() }
                                    } else {
                                        viewModel.signInWithEmail(email, password) { onAuthSuccess() }
                                    }
                                })
                            )

                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    if (uiState.isSignUpMode) {
                                        viewModel.signUpWithEmail(email, password, name) { onAuthSuccess() }
                                    } else {
                                        viewModel.signInWithEmail(email, password) { onAuthSuccess() }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("email_submit_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = safaColors.goldPrimary,
                                    contentColor = SafaNavyDark
                                ),
                                enabled = !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = SafaNavyDark,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = if (uiState.isSignUpMode) "Create Account & Sync Streak" else "Sign In & Keep Streak",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (uiState.isSignUpMode) "Already have an account?" else "Don't have an account?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = safaColors.textSecondary
                                )
                                TextButton(onClick = { viewModel.toggleAuthMode() }) {
                                    Text(
                                        text = if (uiState.isSignUpMode) "Sign In" else "Sign Up",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = safaColors.goldPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FeatureMiniBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val safaColors = LocalSafaColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = safaColors.goldGlow,
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = safaColors.goldPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary,
                    fontSize = 11.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = safaColors.textSecondary,
                    fontSize = 9.5.sp
                )
            }
        }
    }
}
