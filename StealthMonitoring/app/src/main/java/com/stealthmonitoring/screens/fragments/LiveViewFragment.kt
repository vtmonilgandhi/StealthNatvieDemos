package com.stealthmonitoring.screens.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.stealthmonitoring.R
import com.stealthmonitoring.api.ApiImplementation
import com.stealthmonitoring.databinding.FragmentLiveViewBinding
import com.stealthmonitoring.model.SitesModel
import com.stealthmonitoring.screens.CameraListActivity
import com.stealthmonitoring.screens.adapters.SiteListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LiveViewFragment : Fragment() {
    private var _binding: FragmentLiveViewBinding? = null
    private val binding get() = _binding!!
    val TAG: String = javaClass.simpleName
    var fragmentExists: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {
        _binding = FragmentLiveViewBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.pbSite.visibility = View.VISIBLE
        getSites()

        return view
    }

    private fun getSites() {

        ApiImplementation.getSites(requireContext(), object :
            Callback<List<SitesModel>> {
            override fun onResponse(
                call: Call<List<SitesModel>>,
                response: Response<List<SitesModel>>
            ) {
                if (response.body() != null) {
                    val adapter = SiteListAdapter(requireContext(), response.body()!!,
                        SiteListAdapter.OnClickListener { site ->

                            val intent = Intent (activity, CameraListActivity::class.java)
                            intent.putExtra("ID", site.id)
                            intent.putExtra("SiteName", site.properName)
                            activity?.startActivity(intent)
                        })
                    binding.rvSite.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvSite.adapter = adapter
                    binding.swipeContainer.setOnRefreshListener {
                        binding.swipeContainer.isRefreshing = false
                    }
                    adapter
                    binding.pbSite.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<SitesModel>>, t: Throwable) {
                Log.e(TAG, "SiteError" + t.message.toString())

            }
        })
    }
}