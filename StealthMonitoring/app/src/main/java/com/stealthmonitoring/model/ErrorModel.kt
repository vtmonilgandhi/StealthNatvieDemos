package com.stealthmonitoring.model

data class ErrorModel(
    val code: Int,
    val details: String,
    val message: String,
    val validationErrors: Any
)