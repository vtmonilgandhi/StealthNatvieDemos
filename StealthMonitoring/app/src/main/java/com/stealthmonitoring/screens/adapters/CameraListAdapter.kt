package com.stealthmonitoring.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.stealthmonitoring.R
import com.stealthmonitoring.api.ApiImplementation
import com.stealthmonitoring.model.ConnectWowzaModel
import com.stealthmonitoring.model.OnConnectCamera
import com.stealthmonitoring.model.StreamInfo
import com.stealthmonitoring.screens.CameraDetailActivity
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CameraListAdapter(val context: Context, private val dataSet: MutableList<StreamInfo>) :
    RecyclerView.Adapter<CameraListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val txtConnect: TextView
        var mSurface: SurfaceView? = null

        init {
            textView = view.findViewById(R.id.cameraName)
            mSurface = view.findViewById(R.id.pvAll)
            txtConnect = view.findViewById(R.id.txtConnect)
        }
    }

    private var mediaPlayer: MediaPlayer?=null
    var vholder: SurfaceHolder?= null
    private var libvlc: LibVLC?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.camera_inflater, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        loadStream(position, holder)
    }

    private fun loadStream(position: Int, holder: ViewHolder) {
        var stream = dataSet[position]
        stream.context = context
        holder.textView.text = stream?.cameraName

        Log.d("STREAM", stream?.lowStreamURL.toString())

        stream.connectWowzaStreamByName(object : OnConnectCamera {
            override fun connectSuccess(isConnected: Boolean) {
                holder.txtConnect.visibility = if (isConnected) INVISIBLE else VISIBLE
                holder.txtConnect.text = if (isConnected) "" else "Offline"
                configurePlayerView(position, holder)
            }
        })
    }

    private fun configurePlayerView(position: Int, holder: ViewHolder) {
        val wowzaServerIP = dataSet[position].wowzaServerIP
        val lowStreamURL = dataSet[position].lowStreamURL

        val rtspUrl = "rtsp://$wowzaServerIP:1935/connect/$lowStreamURL.stream"

        libvlc = LibVLC(context)
        vholder = holder.mSurface!!.holder
        vholder!!.setKeepScreenOn(true)

        mediaPlayer = MediaPlayer(libvlc)

        val vout = mediaPlayer!!.vlcVout
        vout.setVideoView(holder.mSurface)
        vout.attachViews()

        val sw = holder.mSurface!!.width
        val sh = holder.mSurface!!.height

        mediaPlayer!!.vlcVout.setWindowSize(sw, sh)
        mediaPlayer!!.aspectRatio="4:3"
        mediaPlayer!!.scale = 0f

        val media = Media(libvlc, Uri.parse(rtspUrl))
        media.setHWDecoderEnabled(true, false);
//        mediaPlayer!!.media?.addOption()
        mediaPlayer!!.media = media
        mediaPlayer!!.play()

        mediaPlayer!!.setEventListener { event ->
            when (event.type) {
                MediaPlayer.Event.Buffering -> {}
                MediaPlayer.Event.Stopped -> {
                    Log.d("STREAM-EVENT", "Stopped $lowStreamURL")
                    loadStream(position, holder)
                }
                MediaPlayer.Event.Playing -> Log.d("STREAM-EVENT", "Playing $lowStreamURL")
                MediaPlayer.Event.EncounteredError -> {
                    Log.d("STREAM-EVENT", "Error $lowStreamURL")
                    loadStream(position, holder)
                }
            }
        }

        holder.mSurface!!.setOnClickListener {
            val intent = Intent(context, CameraDetailActivity::class.java)
            intent.putExtra("CameraName", dataSet[position].cameraName)
            intent.putExtra("WowzaServerIP", dataSet[position].wowzaServerIP)
            intent.putExtra("LowStreamURL", dataSet[position].lowStreamURL)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataSet.size
}