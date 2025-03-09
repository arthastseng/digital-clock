package com.ssc.android.vs_digital_clock.ui.setting.edit

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.databinding.WidgetSettingEditModeListItemBinding

class SettingEditModeListAdapter :
    ListAdapter<TimeZone, SettingEditModeListAdapter.ViewHolder>(TimeZoneDiffCallback) {

    private var selectedTimeZoneList = listOf<TimeZone>()
    private val deleteTimeZones = mutableListOf<TimeZone>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WidgetSettingEditModeListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding = binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            val data = selectedTimeZoneList[position]
            bind(data = data)
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    deleteTimeZones.add(data)
                } else {
                    deleteTimeZones.remove(data)
                }
            }
        }
    }

    fun setData(data: List<TimeZone>) {
        Log.d(TAG, "setData : ${data.toString()}")
        selectedTimeZoneList = data
    }

    fun getDeleteTimeZones(): List<TimeZone> {
        return deleteTimeZones
    }

    override fun getItemCount() = selectedTimeZoneList.size

    inner class ViewHolder(val binding: WidgetSettingEditModeListItemBinding) :
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