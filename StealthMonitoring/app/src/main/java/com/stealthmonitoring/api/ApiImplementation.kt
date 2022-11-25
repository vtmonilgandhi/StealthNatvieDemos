package com.stealthmonitoring.api

import android.content.Context
import com.stealthmonitoring.ApiInterface
import com.stealthmonitoring.model.*
import retrofit2.Call
import retrofit2.Callback

class ApiImplementation {

    //Singletons class
    companion object {

        //Create retrofit callback
        fun authenticateUser(
            context: Context,
            body: MutableMap<String, String>,
            callback: Callback<LoginModel?>
        ) {
            val apiInterface: ApiInterface? =
                ApiClient.getClient(context)?.create(ApiInterface::class.java)
            val call: Call<LoginModel?>? = apiInterface?.authenticateUser(body)
            call?.enqueue(callback)
        }

        fun getAllSitesByUser(context: Context, callback: Callback<GetAllSiteModel>) {
            val apiInterface: ApiInterface? =
                ApiClient.getClient(context)?.create(ApiInterface::class.java)
            val call: Call<GetAllSiteModel>? = apiInterface?.getAllSitesByUser()
            call?.enqueue(callback)
        }

        fun getSites(context: Context, callback: Callback<List<SitesModel>>) {
            val apiInterface: ApiInterface? =
                ApiClient.getClient(context)?.create(ApiInterface::class.java)
            val call: Call<List<SitesModel>>? = apiInterface?.getSites()
            call?.enqueue(callback)
        }

        fun getCameraList(
            context: Context,
            id: Int,
            callback: Callback<ArrayList<StreamInfo>?>
        ) {
            val apiInterface: ApiInterface? =
                ApiClient.getClient(context)?.create(ApiInterface::class.java)
            val call: Call<ArrayList<StreamInfo>?>? = apiInterface?.getCameraList(id)
            call?.enqueue(callback)
        }

        fun getIncidents(
            context: Context,
            maxResultCount: Int,
            callback: Callback<ArrayList<IncidentsModel>?>
        ) {
            val apiInterface: ApiInterface? =
                ApiClient.getClient(context)?.create(ApiInterface::class.java)
            val call: Call<ArrayList<IncidentsModel>>? = apiInterface?.getIncidents(maxResultCount)
            call?.enqueue(callback)
        }

        fun connectWowzaStreamByName(
            context: Context,
            streamFileName: String,
            callback: Callback<ConnectWowzaModel>
        ) {
            val apiInterface: ApiInterface? =
                ApiClient.getClient(context)?.create(ApiInterface::class.java)
            val call: Call<ConnectWowzaModel>? =
                apiInterface?.connectWowzaStreamByName(streamFileName)
            call?.enqueue(callback)
        }

        fun disConnectWowzaStreamByName(
            context: Context,
            streamFileName: String,
            callback: Callback<DisconnectWowzaModel>
        ) {
            val apiInterface: ApiInterface? =
                ApiClient.getClient(context)?.create(ApiInterface::class.java)
            val call: Call<DisconnectWowzaModel>? =
                apiInterface?.disConnectWowzaStreamByName(streamFileName)
            call?.enqueue(callback)
        }
    }
}