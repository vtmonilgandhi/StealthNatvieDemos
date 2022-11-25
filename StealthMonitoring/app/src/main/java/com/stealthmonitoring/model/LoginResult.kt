package com.stealthmonitoring.model

data class LoginResult(
    val accessToken: String,
    val encryptedAccessToken: String,
    val expireInSeconds: Int,
    val passwordResetCode: Any,
    val permissions: List<Any>,
    val requiresTwoFactorVerification: Boolean,
    val returnUrl: Any,
    val roles: List<Any>,
    val shouldResetPassword: Boolean,
    val tenantId: Int,
    val twoFactorAuthProviders: Any,
    val twoFactorRememberClientToken: Any,
    val userFullName: String,
    val userId: Int
)