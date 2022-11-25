package com.stealthmonitoring.model

data class IncidentsModel(
    val id: Int,
    val incidentEventType: String,
    val incident_datetime: String,
    val properName: String,
    val siteName: String,
    val site_id: Int,
    val type_id: Int
)