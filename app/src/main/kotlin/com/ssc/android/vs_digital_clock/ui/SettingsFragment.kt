package com.ssc.android.vs_digital_clock.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.ssc.android.vs_digital_clock.databinding.FragmentSettingBinding
import com.ssc.android.vs_digital_clock.presenteation.state.SettingIntention
import com.ssc.android.vs_digital_clock.presenteation.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        fetchAvailableTimeZones()
    }

    private fun initViewModel() {
        collectFlowWhenStart(viewModel.stateFlow) {

        }
        collectFlowWhenStart(viewModel.eventFlow) {

        }
    }

    private fun fetchAvailableTimeZones() {
        viewModel.sendIntention(SettingIntention.FetchAvailableTimeZone)
    }
}