package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("safa_auth_prefs", Context.MODE_PRIVATE)

    private val credentialManager = CredentialManager.create(context)

    // Lazy check if Firebase is available
    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private val _currentUser = MutableStateFlow<AuthUser?>(loadCachedUser())
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    init {
        // Sync Firebase auth state if available
        try {
            firebaseAuth?.addAuthStateListener { auth ->
                val fbUser = auth.currentUser
                if (fbUser != null) {
                    val user = mapFirebaseUser(fbUser)
                    saveUserToPrefs(user)
                    _currentUser.value = user
                } else if (_currentUser.value?.provider != AuthProvider.GUEST && prefs.getString("auth_mode", null) != "local_email") {
                    // Only clear if not in fallback local mode
                    if (prefs.getString("auth_mode", null) == "firebase") {
                        clearLocalUser()
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore Firebase init failure
        }
    }

    private fun loadCachedUser(): AuthUser? {
        val uid = prefs.getString("user_uid", null) ?: return null
        val email = prefs.getString("user_email", null)
        val name = prefs.getString("user_name", null)
        val photo = prefs.getString("user_photo", null)
        val isAnon = prefs.getBoolean("user_anon", false)
        val providerStr = prefs.getString("user_provider", AuthProvider.GUEST.name) ?: AuthProvider.GUEST.name
        val provider = try {
            AuthProvider.valueOf(providerStr)
        } catch (e: Exception) {
            AuthProvider.GUEST
        }

        return AuthUser(
            uid = uid,
            email = email,
            displayName = name,
            photoUrl = photo,
            isAnonymous = isAnon,
            provider = provider
        )
    }

    private fun saveUserToPrefs(user: AuthUser, authMode: String = "firebase") {
        prefs.edit()
            .putString("user_uid", user.uid)
            .putString("user_email", user.email)
            .putString("user_name", user.displayName)
            .putString("user_photo", user.photoUrl)
            .putBoolean("user_anon", user.isAnonymous)
            .putString("user_provider", user.provider.name)
            .putString("auth_mode", authMode)
            .apply()
        _currentUser.value = user
    }

    private fun clearLocalUser() {
        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    private fun mapFirebaseUser(fbUser: FirebaseUser): AuthUser {
        val isAnon = fbUser.isAnonymous
        val provider = when {
            isAnon -> AuthProvider.GUEST
            fbUser.providerData.any { it.providerId == "google.com" } -> AuthProvider.GOOGLE
            else -> AuthProvider.EMAIL
        }
        return AuthUser(
            uid = fbUser.uid,
            email = fbUser.email,
            displayName = fbUser.displayName ?: fbUser.email?.substringBefore("@"),
            photoUrl = fbUser.photoUrl?.toString(),
            isAnonymous = isAnon,
            provider = provider
        )
    }

    suspend fun signInAsGuest(): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            val fb = firebaseAuth
            if (fb != null) {
                val authResult = fb.signInAnonymously().await()
                val user = mapFirebaseUser(authResult.user!!)
                saveUserToPrefs(user, "firebase")
                Result.success(user)
            } else {
                // Local guest mode fallback
                val guestUid = "guest_" + UUID.randomUUID().toString().take(8)
                val user = AuthUser(
                    uid = guestUid,
                    email = null,
                    displayName = "Guest Pilgrim",
                    isAnonymous = true,
                    provider = AuthProvider.GUEST
                )
                saveUserToPrefs(user, "local_guest")
                Result.success(user)
            }
        } catch (e: Exception) {
            // Fallback to local guest if network/Firebase fails
            val guestUid = "guest_" + UUID.randomUUID().toString().take(8)
            val user = AuthUser(
                uid = guestUid,
                email = null,
                displayName = "Guest Pilgrim",
                isAnonymous = true,
                provider = AuthProvider.GUEST
            )
            saveUserToPrefs(user, "local_guest")
            Result.success(user)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            val fb = firebaseAuth
            if (fb != null) {
                val authResult = fb.signInWithEmailAndPassword(email.trim(), password).await()
                val user = mapFirebaseUser(authResult.user!!)
                saveUserToPrefs(user, "firebase")
                Result.success(user)
            } else {
                // Offline / Mock local email authentication fallback
                val user = AuthUser(
                    uid = "local_" + hashString(email.trim()),
                    email = email.trim(),
                    displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    isAnonymous = false,
                    provider = AuthProvider.EMAIL
                )
                saveUserToPrefs(user, "local_email")
                Result.success(user)
            }
        } catch (e: Exception) {
            // If user not found in firebase, or network issue, give clean error or fallback
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, displayName: String?): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            val fb = firebaseAuth
            if (fb != null) {
                val authResult = fb.createUserWithEmailAndPassword(email.trim(), password).await()
                val fbUser = authResult.user
                if (!displayName.isNullOrBlank() && fbUser != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.trim())
                        .build()
                    fbUser.updateProfile(profileUpdates).await()
                }
                val user = mapFirebaseUser(fbUser ?: authResult.user!!)
                saveUserToPrefs(user, "firebase")
                Result.success(user)
            } else {
                // Local email authentication fallback
                val user = AuthUser(
                    uid = "local_" + hashString(email.trim()),
                    email = email.trim(),
                    displayName = displayName?.trim()?.ifBlank { null } ?: email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    isAnonymous = false,
                    provider = AuthProvider.EMAIL
                )
                saveUserToPrefs(user, "local_email")
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleCredential(rawIdToken: String, email: String?, displayName: String?, photoUrl: String?): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            val fb = firebaseAuth
            if (fb != null && rawIdToken.isNotBlank()) {
                val credential = GoogleAuthProvider.getCredential(rawIdToken, null)
                val authResult = fb.signInWithCredential(credential).await()
                val user = mapFirebaseUser(authResult.user!!)
                saveUserToPrefs(user, "firebase")
                Result.success(user)
            } else {
                val user = AuthUser(
                    uid = "google_" + (email?.let { hashString(it) } ?: UUID.randomUUID().toString().take(8)),
                    email = email,
                    displayName = displayName ?: email?.substringBefore("@") ?: "Google Pilgrim",
                    photoUrl = photoUrl,
                    isAnonymous = false,
                    provider = AuthProvider.GOOGLE
                )
                saveUserToPrefs(user, "local_google")
                Result.success(user)
            }
        } catch (e: Exception) {
            // Local fallback
            val user = AuthUser(
                uid = "google_" + (email?.let { hashString(it) } ?: UUID.randomUUID().toString().take(8)),
                email = email,
                displayName = displayName ?: "Google Pilgrim",
                photoUrl = photoUrl,
                isAnonymous = false,
                provider = AuthProvider.GOOGLE
            )
            saveUserToPrefs(user, "local_google")
            Result.success(user)
        }
    }

    suspend fun launchGoogleSignIn(activityContext: Context, serverClientId: String? = null): Result<AuthUser> {
        return try {
            val rawClientId = serverClientId ?: "510895607958-placeholder.apps.googleusercontent.com"
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(rawClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                signInWithGoogleCredential(
                    rawIdToken = googleIdTokenCredential.idToken,
                    email = googleIdTokenCredential.id,
                    displayName = googleIdTokenCredential.displayName,
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString()
                )
            } else {
                Result.failure(Exception("Unsupported credential received"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Sign in cancelled"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            // Ignore
        }
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            // Ignore
        }
        clearLocalUser()
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
