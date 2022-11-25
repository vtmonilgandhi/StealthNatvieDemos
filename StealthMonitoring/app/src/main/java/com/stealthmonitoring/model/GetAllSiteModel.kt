package com.stealthmonitoring.model

data class GetAllSiteModel(
    val __abp: Boolean,
    val error: Any,
    val result: List<SiteResult>,
    val success: Boolean,
    val targetUrl: Any,
    val unAuthorizedRequest: Boolean
)