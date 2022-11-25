package com.stealthmonitoring.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.stealthmonitoring.R
import com.stealthmonitoring.api.ApiImplementation
import com.stealthmonitoring.databinding.ActivityCameraDetailBinding
import com.stealthmonitoring.model.ConnectWowzaModel
import com.stealthmonitoring.model.DisconnectWowzaModel
import com.stealthmonitoring.screens.adapters.CameraListAdapter
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CameraDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraDetailBinding
    val tag: String = javaClass.simpleName
    var mSurface: SurfaceView? = null
    private var mediaPlayer: MediaPlayer?=null
    var vholder: SurfaceHolder?= null
    private var libvlc: LibVLC?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCameraDetailBinding.inflate(layoutInflater)
        val view = binding.root
        mSurface = view.findViewById(R.id.pvDetail)
        setContentView(view)

        //Back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val cameraName = intent.getStringExtra("CameraName")
        supportActionBar!!.title = cameraName

        binding.pbCameraDetail.visibility = View.VISIBLE
        connectWowzaStreamByName()
    }

    private fun configurePlayerView() {
        val wowzaServerIP = intent.getStringExtra("WowzaServerIP")
        val lowStreamURL = intent.getStringExtra("LowStreamURL")

        val rtspUrl = "rtsp://$wowzaServerIP:1935/connect/$lowStreamURL.stream"

        libvlc = LibVLC(this)
        vholder = mSurface!!.holder
        vholder!!.setKeepScreenOn(true)

        mediaPlayer = MediaPlayer(libvlc)

        val vout = mediaPlayer!!.vlcVout
        vout.setVideoView(mSurface)
        vout.attachViews()

        val media = Media(libvlc, Uri.parse(rtspUrl))
        media.setHWDecoderEnabled(true, false);
        mediaPlayer!!.media = media
        mediaPlayer!!.play()

        mediaPlayer!!.setEventListener { event ->
            when (event.type) {
                MediaPlayer.Event.Buffering -> {}
                MediaPlayer.Event.Stopped -> {
                    Log.d("STREAM-EVENT", "Stopped $lowStreamURL")
                    connectWowzaStreamByName()
                }
                MediaPlayer.Event.Playing -> Log.d("STREAM-EVENT", "Playing $lowStreamURL")
                MediaPlayer.Event.EncounteredError -> {
                    Log.d("STREAM-EVENT", "Error $lowStreamURL")
                    connectWowzaStreamByName()
                }
            }
        }
    }

    private fun connectWowzaStreamByName() {
        val getLowStreamURL = intent.getStringExtra("LowStreamURL")

        ApiImplementation.connectWowzaStreamByName(this@CameraDetailActivity, getLowStreamURL.toString(),
            object : Callback<ConnectWowzaModel> {
                override fun onResponse(
                    call: Call<ConnectWowzaModel>,
                    response: Response<ConnectWowzaModel>
                ) {
                    Log.d(tag, "ConnectWowzaSuccess" + response.body())
                    binding.pbCameraDetail.visibility = View.GONE
                    configurePlayerView()
                }

                override fun onFailure(call: Call<ConnectWowzaModel?>, t: Throwable) {
                    Log.e(tag, "ConnectWowzaFail" + t.message.toString())
                }
            })
    }

    private fun disConnectWowzaStreamByName() {
        val getLowStreamURL = intent.getStringExtra("LowStreamURL")

        ApiImplementation.disConnectWowzaStreamByName(this@CameraDetailActivity, getLowStreamURL.toString(),
            object : Callback<DisconnectWowzaModel> {
                override fun onResponse(
                    call: Call<DisconnectWowzaModel>,
                    response: Response<DisconnectWowzaModel>
                ) {
                    Log.d(tag, "DisconnectWowzaSuccess: " + response.body())
                }

                override fun onFailure(call: Call<DisconnectWowzaModel?>, t: Throwable) {
                    Log.e(tag, "DisconnectWowzaFail" + t.message.toString())
                }
            })
    }

    //Back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}


