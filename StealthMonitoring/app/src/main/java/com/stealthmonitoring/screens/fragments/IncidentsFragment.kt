package com.stealthmonitoring.screens.fragments

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.stealthmonitoring.api.ApiImplementation
import com.stealthmonitoring.databinding.FragmentIncidentsBinding
import com.stealthmonitoring.model.IncidentsModel
import com.stealthmonitoring.screens.adapters.IncidentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class IncidentsFragment : Fragment() {
    private var _binding: FragmentIncidentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var incidentFilterArrayList: ArrayList<IncidentsModel>
    private lateinit var adapter: IncidentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidentsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.pbIncident.visibility = View.VISIBLE
        getIncidentList()
        binding.searchId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })
        return view
    }

    private fun filter(text: String) {
        val filteredList = ArrayList<IncidentsModel>()
        for (item in incidentFilterArrayList) {
            if (item.properName.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault())) || (item.incidentEventType.uppercase(
                    Locale.getDefault()
                ).contains(text.uppercase(Locale.getDefault())))
            ) {
                filteredList.add(item)
            }
        }

        if (filteredList.isEmpty()) {
            Snackbar.make(
                requireActivity().findViewById(R.id.content),
                "No Data Found..", Snackbar.LENGTH_LONG
            ).show()
        }
        adapter.filterList(filteredList)

    }

    private fun getIncidentList() {

        ApiImplementation.getIncidents(requireContext(), 30, object :
            Callback<ArrayList<IncidentsModel>?> {
            override fun onResponse(
                call: Call<ArrayList<IncidentsModel>?>,
                response: Response<ArrayList<IncidentsModel>?>
            ) {
                try {
                    incidentFilterArrayList = response.body()!!
                    adapter = IncidentAdapter(response.body()!!)
                    binding.rvIncident.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvIncident.adapter = adapter
                    Log.d(javaClass.simpleName, "IncidentList " + response.body())
                    binding.swipeContainer.setOnRefreshListener {
                        binding.swipeContainer.isRefreshing = false
                    }
                    binding.pbIncident.visibility = View.GONE
                } catch (_: Exception) {
                }
            }

            override fun onFailure(call: Call<ArrayList<IncidentsModel>?>, t: Throwable) {
                Log.e(javaClass.simpleName, t.message.toString())
            }
        })
    }
}