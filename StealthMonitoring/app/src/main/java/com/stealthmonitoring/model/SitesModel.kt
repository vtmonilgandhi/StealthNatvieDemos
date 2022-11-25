package com.stealthmonitoring.model

data class SitesModel(
    val address: String,
    val city: String,
    val customerId: Int,
    val id: Int,
    val isActive: Boolean,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val oldSiteId: Int,
    val postalCode: String,
    val properName: String,
    val stateName: String,
    val totalCameraCount: Int
)