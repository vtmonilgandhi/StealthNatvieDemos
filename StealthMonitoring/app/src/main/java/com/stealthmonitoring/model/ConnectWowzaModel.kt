package com.stealthmonitoring.model

data class ConnectWowzaModel(
    val __abp: Boolean,
    val error: ErrorModel,
    val result: ConnectWowzaResult,
    val success: Boolean,
    val targetUrl: Any,
    val unAuthorizedRequest: Boolean
)

data class ConnectWowzaResult(
    val isConnected: Boolean,
    val isWowzaOnboarded: Boolean,
    val message: String,
    val success: Boolean
)