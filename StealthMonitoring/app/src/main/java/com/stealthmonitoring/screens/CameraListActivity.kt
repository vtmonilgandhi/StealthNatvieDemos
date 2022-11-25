package com.stealthmonitoring.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.stealthmonitoring.api.ApiImplementation
import com.stealthmonitoring.databinding.ActivityCameraListBinding
import com.stealthmonitoring.model.StreamInfo
import com.stealthmonitoring.screens.adapters.CameraListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CameraListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraListBinding
    val tag: String = javaClass.simpleName
    private var index = 0
    lateinit var displayedList: ArrayList<StreamInfo>
    private lateinit var cameraList: MutableList<StreamInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val siteName = intent.getStringExtra("SiteName")
        supportActionBar!!.title = siteName
        binding.pbCamera.visibility = View.VISIBLE
        getCameraList()

        binding.nextBtn.setOnClickListener {
            index += 6
            if (index > cameraList.size) index -= 6
            else
                setDisplayListPosition()
            print("nextIndex: $index")
        }

        binding.previousBtn.setOnClickListener {
            index -= 6
            if (index < 0) index = 0
            else
                setDisplayListPosition()
            print("prevIndex: $index")
        }
    }

    private fun setDisplayListPosition() {
        if(::displayedList.isInitialized) {
            displayedList.forEach {
                val stream = it
                stream.context = this@CameraListActivity
                stream.disConnectWowzaStreamByName()
            }
        }
        displayedList = ArrayList()
        displayedList.clear()
        var i = 0
        while (i < 6 && i + index < cameraList.size) {
            displayedList.add(cameraList[index + i])
            i++
        }
        val adapter = CameraListAdapter(this@CameraListActivity, displayedList)
        binding.rvCamera.layoutManager = GridLayoutManager(this@CameraListActivity, 2)
        binding.rvCamera.adapter = adapter
    }

    fun extractInt(s: StreamInfo): Int {
        val num = s.cameraName.replace("\\D".toRegex(), "")
        // return 0 if no digits found
        return if (num.isEmpty()) 0 else Integer.parseInt(num)
    }

    private fun getCameraList() {
        val getId = intent.getIntExtra("ID", 0)

        ApiImplementation.getCameraList(this@CameraListActivity, getId!!.toInt(), object :
            Callback<ArrayList<StreamInfo>?> {
            override fun onResponse(
                call: Call<ArrayList<StreamInfo>?>,
                response: Response<ArrayList<StreamInfo>?>
            ) {
                var data : MutableList<StreamInfo> = response.body()!!
                val sortData = data.sortedWith { o1, o2 -> extractInt(o1) - extractInt(o2) }
                cameraList = sortData as MutableList<StreamInfo>

                setDisplayListPosition()
                val adapter = CameraListAdapter(this@CameraListActivity, displayedList)
                binding.rvCamera.layoutManager = GridLayoutManager(this@CameraListActivity, 2)
                binding.rvCamera.adapter = adapter
                binding.nextBtn.visibility = View.VISIBLE
                binding.previousBtn.visibility = View.VISIBLE
                binding.pbCamera.visibility = View.GONE
            }

            override fun onFailure(call: Call<ArrayList<StreamInfo>?>, t: Throwable) {
                Log.e(tag, "CameraListFail" + t.message.toString())
            }
        })
    }

    //Back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}