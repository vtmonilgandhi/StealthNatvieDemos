package com.stealthmonitoring.model

data class DisconnectWowzaModel(
    val isConnected: Boolean,
    val isWowzaOnboarded: Boolean,
    val message: String,
    val success: Boolean
)