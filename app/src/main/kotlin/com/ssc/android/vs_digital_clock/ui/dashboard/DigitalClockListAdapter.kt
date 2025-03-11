package com.ssc.android.vs_digital_clock.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.databinding.WidgetClockViewBinding

class DigitalClockListAdapter :
    ListAdapter<TimeZoneInfo, DigitalClockListAdapter.ViewHolder>(TimeZoneDiffCallback) {

    private var timeZoneList = listOf<TimeZoneInfo>()
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(data: TimeZoneInfo)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WidgetClockViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding = binding)
    }

    fun setData(data: List<TimeZoneInfo>) {
        Log.d(TAG, "setData : ${data.toString()}")
        timeZoneList = data
    }

    override fun getItemCount() = timeZoneList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data = timeZoneList[position])
        holder.binding.root.setOnClickListener {
            val timezoneInfo = timeZoneList[position]
            itemClickListener?.onItemClick(data = timezoneInfo)
        }
    }

    inner class ViewHolder(val binding: WidgetClockViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TimeZoneInfo) {
            Log.d(TAG, "bind data : ${data.toString()}")

            with(binding) {
                time.text = data.time.toString()
                timezone.text = data.timeZone
            }
        }
    }

    object TimeZoneDiffCallback : DiffUtil.ItemCallback<TimeZoneInfo>() {
        override fun areItemsTheSame(oldItem: TimeZoneInfo, newItem: TimeZoneInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TimeZoneInfo, newItem: TimeZoneInfo): Boolean {
            return oldItem.time == newItem.time
        }
    }

    companion object {
        private const val TAG = "DigitalClockListAdapter"
    }
}