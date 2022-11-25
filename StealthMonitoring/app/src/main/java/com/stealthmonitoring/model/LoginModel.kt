package com.stealthmonitoring.model

data class LoginModel(
    val __abp: Boolean,
    val error: ErrorModel,
    val result: LoginResult,
    val success: Boolean,
    val targetUrl: Any,
    val unAuthorizedRequest: Boolean
)