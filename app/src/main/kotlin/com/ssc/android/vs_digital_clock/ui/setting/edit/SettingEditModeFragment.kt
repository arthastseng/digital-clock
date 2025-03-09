package com.ssc.android.vs_digital_clock.ui.setting.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.databinding.FragmentSettingEditBinding
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEditEvent
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEditIntention
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEditViewState
import com.ssc.android.vs_digital_clock.presenteation.viewmodel.SettingEditModeViewModel
import com.ssc.android.vs_digital_clock.ui.util.collectFlowWhenStart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingEditModeFragment : Fragment() {
    private val viewModel: SettingEditModeViewModel by viewModels()
    private var _binding: FragmentSettingEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViewModel()
        initUI()
    }

    override fun onResume() {
        super.onResume()
        viewModel.sendIntention(SettingEditIntention.FetchTimeZones)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initUI() {
        with(binding) {

            back.setOnClickListener {
                activity?.finish()
            }

            delete.setOnClickListener {
                val adapter = binding.recyclerView.adapter
                if (adapter is SettingEditModeListAdapter) {
                    val deleteTimeZones = adapter.getDeleteTimeZones()
                    if (deleteTimeZones.isNotEmpty()) {
                        viewModel.sendIntention(
                            SettingEditIntention.DeleteTimeZones(data = deleteTimeZones)
                        )
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        collectFlowWhenStart(viewModel.stateFlow) {
            handleViewStateUpdate(state = it)
        }
        collectFlowWhenStart(viewModel.eventFlow) {
            handleViewModelEvent(event = it)
        }
    }

    private fun handleViewStateUpdate(state: SettingEditViewState) {
        Log.d(TAG, "handleViewStateUpdate: $state")
        when (state) {
            is SettingEditViewState.GetTimeZoneReady ->
                handleTimeZoneDatabaseDataReady(data = state.data)

            is SettingEditViewState.DeleteTimeZoneCompleted ->
                handleDeleteTimeZoneCompleted()

            else -> Unit
        }
    }

    private fun handleViewModelEvent(event: SettingEditEvent) {
        Log.d(TAG, "handleViewModelEvent: $event")
    }

    private fun initRecyclerView() {
        val layoutMgr = LinearLayoutManager(context)

        binding.recyclerView.apply {
            layoutManager = layoutMgr
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        //custom divider
        val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutMgr.orientation)
        val lineDrawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.shape_list_decoration
        )

        lineDrawable?.let {
            dividerItemDecoration.setDrawable(it)
            binding.recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun handleTimeZoneDatabaseDataReady(data: List<TimeZone>) {
        Log.d(TAG, "handleTimeZoneDatabaseDataReady: ${data.toString()}")
        val settingEditModeListAdapter = SettingEditModeListAdapter().apply {
            data?.let {
                setData(it)
            }
        }
        binding.recyclerView.apply {
            adapter = settingEditModeListAdapter
            adapter?.notifyDataSetChanged()
        }
    }

    private fun handleDeleteTimeZoneCompleted() {
        viewModel.sendIntention(SettingEditIntention.FetchTimeZones)
    }

    companion object {
        private const val TAG = "SettingEditModeFragment"
    }
}