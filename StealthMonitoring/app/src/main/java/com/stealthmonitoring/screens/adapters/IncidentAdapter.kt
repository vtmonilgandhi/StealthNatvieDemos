package com.stealthmonitoring.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stealthmonitoring.R
import com.stealthmonitoring.model.IncidentsModel

class IncidentAdapter(private var dataSet: ArrayList<IncidentsModel>) :
    RecyclerView.Adapter<IncidentAdapter.ViewHolder>() {

    fun filterList(filterList: ArrayList<IncidentsModel>) {
        dataSet = filterList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val properName: TextView
        val incidentEventType: TextView
        val id: TextView

        init {
            properName = view.findViewById(R.id.properName)
            incidentEventType = view.findViewById(R.id.incidentEventType)
            id = view.findViewById(R.id.iId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.incident_inflater, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.properName.text = dataSet[position].properName
        holder.incidentEventType.text = dataSet[position].incidentEventType
        holder.id.text = dataSet[position].id.toString()

    }

    override fun getItemCount() = dataSet.size

}