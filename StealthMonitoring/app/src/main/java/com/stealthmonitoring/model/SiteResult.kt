package com.stealthmonitoring.model

data class SiteResult(
    val city: String,
    val id: Int,
    val properName: String,
    val siteName: String,
    val state: String,
    val streetAddress: String,
    val zipcode: String
)