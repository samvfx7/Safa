package com.example.data.auth

data class AuthUser(
    val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val provider: AuthProvider = AuthProvider.GUEST
)

enum class AuthProvider {
    GOOGLE,
    EMAIL,
    GUEST
}

sealed class AuthResult {
    data class Success(val user: AuthUser, val message: String? = null) : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
    data object Loading : AuthResult()
}
