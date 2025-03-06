package com.ssc.android.vs_digital_clock.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.ssc.android.vs_digital_clock.R
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
        initActionbar()
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

    private fun initActionbar() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_settings, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_refresh -> {
                        Log.d(TAG,"refresh action selected")
                        true
                    }
                    R.id.action_language -> {
                        Log.d(TAG,"language action selected")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun fetchAvailableTimeZones() {
        viewModel.sendIntention(SettingIntention.FetchAvailableTimeZone)
    }

    companion object {
        private const val TAG = "SettingFragment"
    }
}