package com.ssc.android.vs_digital_clock.ui.setting

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssc.android.vs_digital_clock.databinding.WidgetTimeZoneListItemBinding

class TimeZoneListAdapter :
    ListAdapter<String, TimeZoneListAdapter.ViewHolder>(TimeZoneDiffCallback) {

    private var timeZoneList = listOf<String>()
    private var clickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClicked(data: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WidgetTimeZoneListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding = binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data = timeZoneList[position])
        holder.binding.root.setOnClickListener {
            clickListener?.onItemClicked(timeZoneList[position])
        }
    }

    fun setData(data: List<String>) {
        Log.d(TAG, "setData : ${data.toString()}")
        timeZoneList = data
    }

    fun setOnItemClickedListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    override fun getItemCount() = timeZoneList.size

    inner class ViewHolder(val binding: WidgetTimeZoneListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.timeZone.text = data
        }
    }

    object TimeZoneDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val TAG = "DigitalClockListAdapter"
    }
}