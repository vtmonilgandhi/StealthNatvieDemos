package com.stealthmonitoring.screens.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stealthmonitoring.R
import com.stealthmonitoring.model.SitesModel

class SiteListAdapter(private val context: Context, private val dataSet: List<SitesModel>, private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<SiteListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val properNameTv: TextView
        val streetAddressTv: TextView
        val noOfCameraTv: TextView

        init {
            properNameTv = view.findViewById(R.id.properName)
            streetAddressTv = view.findViewById(R.id.streetAddress)
            noOfCameraTv = view.findViewById(R.id.noOfCameras)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.site_list_inflater, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.properNameTv.text = dataSet[position].properName
        holder.streetAddressTv.text = dataSet[position].address
        holder.noOfCameraTv.text = dataSet[position].totalCameraCount.toString() + " Cameras"

        val site = dataSet[position]
        holder.itemView.setOnClickListener {
            onClickListener.onClick(site)
        }
//        holder.itemView.setOnClickListener {
//             val intent = Intent(context, SitesModel::class.java)
//             intent.putExtra("ID", dataSet[position].id.toString())
//             intent.putExtra("SiteName", dataSet[position].properName)
//             context.startActivity(intent)
//         }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class OnClickListener(val clickListener: (site: SitesModel) -> Unit) {
        fun onClick(site: SitesModel) = clickListener(site)
    }
}