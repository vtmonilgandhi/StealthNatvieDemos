package com.stealthmonitoring.model;

import android.content.Context;
import android.util.Log;
import com.stealthmonitoring.api.ApiImplementation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StreamInfo(
    var context:Context?,
    val cameraID: Int,
    val cameraIsMonitored: Boolean,
    val cameraName: String,
    val cameraStatus: Boolean,
    val cameraTypeId: Int,
    val canUseRTSP: Boolean,
    val highStreamURL: String,
    val ip: String,
    val isMobileServerInstalled: Boolean,
    val login: String,
    val lowStreamURL: String,
    val monitoringPlatformTypeId: Int,
    val nearestTalkDown: Int,
    val nvrId: Int,
    val nvrIsMonitored: Boolean,
    val nvrName: String,
    val nvrStatus: Boolean,
    val password: String,
    val port: Int,
    val rtspLogin: Any,
    val rtspPassword: Any,
    val rtspPort: Int,
    val rtspStream1URL: Any,
    val rtspStream2URL: Any,
    val serverId: Int,
    val setupScript: Any,
    val siteId: Int,
    val siteIsMonitored: Boolean,
    val storageName: String,
    val vendorID: String,
    val video: Int,
    val videoName: Any,
    val wowzaServerIP: String,
    val wowzaStreamLockFile: String,
    val wowzaStreamType: Int,
    var isConnected: Boolean = false,
    var connectionRetryCount: Int = 0,
    var connectionRetryTimes: Int = 2
) {
    fun connectWowzaStreamByName(listener: OnConnectCamera) {
        while (!isConnected && (connectionRetryCount <= connectionRetryTimes)) {
            connectionRetryCount += 1
            ApiImplementation.connectWowzaStreamByName(context!!, lowStreamURL, object : Callback<ConnectWowzaModel> {
                override fun onResponse(call: Call<ConnectWowzaModel>, response: Response<ConnectWowzaModel>) {
                    try {
                        var res = response.body()
                        Log.d("STREAM", "connectWowzaStreamByName $lowStreamURL $res")
                        if (res != null) {
                            isConnected = res.result.isConnected
                            listener.connectSuccess(res.result.isConnected)
                        }
                    } catch (e: Exception) {
                        Log.d(javaClass.simpleName, "ConnectWowzaSuccess $e")
                    }
                }

                override fun onFailure(call: Call<ConnectWowzaModel?>, t: Throwable) {
                    Log.e(javaClass.simpleName, "ConnectWowzaFail" + t.message.toString())
                }
            })
        }
    }

    fun disConnectWowzaStreamByName() {

        ApiImplementation.disConnectWowzaStreamByName(context!!, lowStreamURL, object : Callback<DisconnectWowzaModel> {
            override fun onResponse( call: Call<DisconnectWowzaModel>, response: Response<DisconnectWowzaModel> ) {
                Log.d(javaClass.simpleName, "DisconnectWowzaSuccess: " + response.body())
            }

            override fun onFailure(call: Call<DisconnectWowzaModel?>, t: Throwable) {
                Log.e(javaClass.simpleName, "DisconnectWowzaFail" + t.message.toString())
            }
        })
    }
}

interface OnConnectCamera {
    fun connectSuccess(isConnected: Boolean)
}
