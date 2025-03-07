package com.ssc.android.vs_digital_clock.ui.setting

import android.content.DialogInterface
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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.databinding.FragmentSettingBinding
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEvent
import com.ssc.android.vs_digital_clock.presenteation.state.SettingIntention
import com.ssc.android.vs_digital_clock.presenteation.state.SettingViewState
import com.ssc.android.vs_digital_clock.presenteation.viewmodel.SettingViewModel
import com.ssc.android.vs_digital_clock.ui.util.collectFlowWhenStart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private var timeZoneDialog: DialogFragment? = null

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
    }

    private fun initViewModel() {
        collectFlowWhenStart(viewModel.stateFlow) {
            handleViewStateUpdate(state = it)
        }
        collectFlowWhenStart(viewModel.eventFlow) {
            handleViewModelEvent(event = it)
        }

        //preload available timezones
        viewModel.sendIntention(SettingIntention.PreloadTimeZone)
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
                    R.id.action_add -> {
                        Log.d(TAG, "add action selected")
                        viewModel.sendIntention(SettingIntention.FetchAvailableTimeZone)
                        true
                    }

                    R.id.action_edit -> {
                        Log.d(TAG, "edit action selected")
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun handleViewStateUpdate(state: SettingViewState) {
        Log.d(TAG, "handleViewStateUpdate: $state")
        when (state) {
            is SettingViewState.TimeZoneDataReady -> showTimeZoneSelectDialog(data = state.data)
            else -> Unit
        }
    }

    private fun handleViewModelEvent(event: SettingEvent) {

    }

    private fun showTimeZoneSelectDialog(data: List<String>) {

        val args = Bundle()
        val bundleData = ArrayList<String>()
        data.forEach {
            bundleData.add(it)
        }
        args.putStringArrayList(TimezoneSelectedDialogFragment.TIME_ZONE_DATA, bundleData)

        if (timeZoneDialog == null) {
            timeZoneDialog = TimezoneSelectedDialogFragment().also {
                it.arguments = args
                it.setDismissListener(object : TimezoneSelectedDialogFragment.DismissListener {
                    override fun onDismiss() {
                        viewModel.sendIntention(SettingIntention.Idle)
                        timeZoneDialog = null
                    }
                })
            }

            timeZoneDialog?.show(childFragmentManager, TimeZoneSelectDialogTagTAG)
        }
    }

    companion object {
        private const val TAG = "SettingFragment"
        private const val TimeZoneSelectDialogTagTAG = "timeZoneSelect"
    }
}