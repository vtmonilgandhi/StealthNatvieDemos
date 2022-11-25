package com.stealthmonitoring

import com.stealthmonitoring.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("/api/TokenAuth/Authenticate")
    fun authenticateUser(@Body body: MutableMap<String, String>): Call<LoginModel?>

    @GET("/api/services/app/Site/GetAllSitesByUser")
    fun getAllSitesByUser(): Call<GetAllSiteModel>

    @GET("/api/Sites")
    fun getSites(): Call<List<SitesModel>>

    @GET("/api/Sites/{siteId}/cameras")
    fun getCameraList(@Path("siteId") id: Int): Call<ArrayList<StreamInfo>?>

    @GET("/api/incidents")
    fun getIncidents(@Query("maxResultCount") maxResultCount: Int): Call<ArrayList<IncidentsModel>>

    @POST("/api/services/app/Wowza/ConnectWowzaStreamByName")
    fun connectWowzaStreamByName(@Query("streamFileName") streamFileName: String): Call<ConnectWowzaModel>

    @POST("/api/services/app/Wowza/DisconnectWowzaStreamByName")
    fun disConnectWowzaStreamByName(@Query("streamFileName") streamFileName: String): Call<DisconnectWowzaModel>
}