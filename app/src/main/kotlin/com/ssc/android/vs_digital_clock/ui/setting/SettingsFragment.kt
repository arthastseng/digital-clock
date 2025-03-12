package com.ssc.android.vs_digital_clock.ui.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.databinding.FragmentSettingBinding
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import com.ssc.android.vs_digital_clock.presentation.state.SettingEvent
import com.ssc.android.vs_digital_clock.presentation.state.SettingIntention
import com.ssc.android.vs_digital_clock.presentation.state.SettingViewState
import com.ssc.android.vs_digital_clock.presentation.viewmodel.SettingViewModel
import com.ssc.android.vs_digital_clock.ui.setting.edit.SettingEditModeActivity
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.sendIntention(SettingIntention.FetchTimeZonesFromDB)
    }

    private fun initViewModel() {
        collectFlowWhenStart(viewModel.stateFlow) {
            handleViewStateUpdate(state = it)
        }
        collectFlowWhenStart(viewModel.eventFlow) {
            handleViewModelEvent(event = it)
        }

        //preload available timezones
        preloadTimeZone()
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
                        startActivity(Intent(requireContext(), SettingEditModeActivity::class.java))
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

    private fun preloadTimeZone() {
        //preload available timezones
        viewModel.sendIntention(SettingIntention.PreloadTimeZone)
    }

    private fun handleViewStateUpdate(state: SettingViewState) {
        Log.d(TAG, "handleViewStateUpdate: $state")
        when (state) {
            is SettingViewState.TimeZoneDataReady ->
                showTimeZoneSelectDialog(data = state.data)

            is SettingViewState.GetTimeZoneFromDbReady ->
                handleTimeZoneDatabaseDataReady(data = state.data)

            is SettingViewState.InsertTimeZoneToDbCompleted ->
                viewModel.sendIntention(SettingIntention.FetchTimeZonesFromDB)

            else -> Unit
        }
    }

    private fun handleViewModelEvent(event: SettingEvent) {
        if (event is SettingEvent.ErrorOccur) {
            showErrorDialog(error = event.error)
        }

        if (event is SettingEvent.FetchAvailableTimeZoneError) {
            showFetchAvailableTimeZoneErrorDialog()
        }
    }

    private fun showFetchAvailableTimeZoneErrorDialog() {
        context?.let {
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            builder
                .setMessage(it.resources.getString(R.string.fetch_available_timezone_error))
                .setTitle(it.resources.getString(R.string.error_occur))
                .setPositiveButton(it.resources.getString(R.string.retry)) { _, _ ->
                    preloadTimeZone()
                }
                .setNegativeButton(it.resources.getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun showErrorDialog(error: SystemError) {
        context?.let {
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            builder
                .setMessage(error.errorMsg)
                .setTitle(it.resources.getString(R.string.error_occur))
                .setPositiveButton(it.resources.getString(R.string.retry)) { _, _ ->
                    viewModel.sendIntention(SettingIntention.FetchTimeZonesFromDB)
                }
                .setNegativeButton(it.resources.getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun handleTimeZoneDatabaseDataReady(data: List<TimeZone>) {
        Log.d(TAG, "handleTimeZoneDatabaseDataReady: ${data.toString()}")
        val selectedTimeZoneListAdapter = SelectedTimeZoneListAdapter().apply {
            data?.let {
                setData(it)
            }
        }
        binding.recyclerView.apply {
            adapter = selectedTimeZoneListAdapter
            adapter?.notifyDataSetChanged()
        }
    }

    private fun showTimeZoneSelectDialog(data: List<String>) {

        val args = Bundle()
        val bundleData = ArrayList<String>()
        data.forEach {
            bundleData.add(it)
        }
        args.putStringArrayList(TimeZoneSelectDialogFragment.TIME_ZONE_DATA, bundleData)

        if (timeZoneDialog == null) {
            timeZoneDialog = TimeZoneSelectDialogFragment().also {
                it.arguments = args
                it.setDismissListener(object : TimeZoneSelectDialogFragment.DialogEventListener {
                    override fun onDismiss() {
                        viewModel.sendIntention(SettingIntention.Idle)
                        timeZoneDialog = null
                        viewModel.sendIntention(SettingIntention.FetchTimeZonesFromDB)
                    }

                    override fun onItemSelected(data: TimeZone) {
                        Log.d(TAG, "onItemSelected : $data")
                        viewModel.sendIntention(SettingIntention.AddTimeZone(data = data))
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