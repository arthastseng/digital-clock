package com.ssc.android.vs_digital_clock.ui.setting

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.databinding.WidgetSelectedTimeZoneListItemBinding

class SelectedTimeZoneListAdapter :
    ListAdapter<TimeZone, SelectedTimeZoneListAdapter.ViewHolder>(TimeZoneDiffCallback) {

    private var selectedTimeZoneList = listOf<TimeZone>()
    private var clickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClicked(data: TimeZone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WidgetSelectedTimeZoneListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding = binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data = selectedTimeZoneList[position])
        holder.binding.root.setOnClickListener {
            clickListener?.onItemClicked(selectedTimeZoneList[position])
        }
    }

    fun setData(data: List<TimeZone>) {
        Log.d(TAG, "setData : ${data.toString()}")
        selectedTimeZoneList = data
    }

    fun setOnItemClickedListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    override fun getItemCount() = selectedTimeZoneList.size

    inner class ViewHolder(val binding: WidgetSelectedTimeZoneListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TimeZone) {
            binding.timeZone.text = data.indexKey
        }
    }

    object TimeZoneDiffCallback : DiffUtil.ItemCallback<TimeZone>() {
        override fun areItemsTheSame(oldItem: TimeZone, newItem: TimeZone): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: TimeZone, newItem: TimeZone): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    companion object {
        private const val TAG = "SelectedTimeZoneListAdapter"
    }
}