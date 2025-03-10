package com.ssc.android.vs_digital_clock.ui.dashboard

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.databinding.FragmentTimeDashBoardBinding
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardEvent
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardIntention
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardViewState
import com.ssc.android.vs_digital_clock.presenteation.viewmodel.TimeDashBoardViewModel
import com.ssc.android.vs_digital_clock.ui.util.collectFlowWhenStart
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class TimeDashBoardFragment : Fragment() {
    private var _binding: FragmentTimeDashBoardBinding? = null
    private val binding get() = _binding!!
    private var digitalClockAdapter: DigitalClockListAdapter? = null
    private val viewModel: TimeDashBoardViewModel by viewModels()
    private var actionbarMenu: Menu? = null
    private var refreshRate: Int = 0
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimeDashBoardBinding.inflate(inflater, container, false)
        initViewModel()
        initActionbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        binding.loadingProgress.visibility = View.VISIBLE
        getRefreshRatePreference()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            context?.let {
                layoutManager = GridLayoutManager(it, COLUMN_SIZE)
                val decoration = GridItemDecoration(
                    context = it,
                    itemWidth = it.resources.getDimensionPixelOffset(R.dimen.digital_clock_item_width),
                    columnCount = COLUMN_SIZE
                )
                addItemDecoration(decoration)
            }
        }
    }

    private fun getRefreshRatePreference() {
        viewModel.sendIntention(TimeDashBoardIntention.GetRefreshRate)
    }

    private fun getTimeZones() {
        viewModel.sendIntention(TimeDashBoardIntention.FetchTimeZones)
    }

    private fun initActionbar() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                handleRefreshRateUpdate(refreshRate)
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_time_dash_board, menu)
                actionbarMenu = menu
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_refresh -> {
                        Log.d(TAG, "refresh action selected")
                        true
                    }

                    R.id.action_language -> {
                        Log.d(TAG, "language action selected")
                        true
                    }

                    R.id.action_refresh_1_min -> {
                        Log.d(TAG, "refresh 5 min selected")
                        viewModel.sendIntention(TimeDashBoardIntention.RefreshRateChanged(rate = 1))
                        true
                    }

                    R.id.action_refresh_5_min -> {
                        Log.d(TAG, "refresh 5 min selected")
                        viewModel.sendIntention(TimeDashBoardIntention.RefreshRateChanged(rate = 5))
                        true
                    }

                    R.id.action_refresh_10_min -> {
                        Log.d(TAG, "refresh 10 min selected")
                        viewModel.sendIntention(TimeDashBoardIntention.RefreshRateChanged(rate = 10))
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initViewModel() {
        collectFlowWhenStart(viewModel.stateFlow) {
            handleViewStateUpdate(state = it)
        }
        collectFlowWhenStart(viewModel.eventFlow) {
            handleViewModelEvent(event = it)
        }
    }

    private fun handleViewStateUpdate(state: TimeDashBoardViewState) {
        Log.d(TAG, "handleViewStateUpdate: $state")
        when (state) {
            is TimeDashBoardViewState.FetchTimeZoneReady ->
                handleTimeZoneDataReady(data = state.data)

            is TimeDashBoardViewState.GetRefreshRateReady -> {
                refreshRate = state.data
                activity?.invalidateOptionsMenu()
                startTimerTask()
            }

            is TimeDashBoardViewState.RefreshRateUpdateCompleted -> {
                refreshRate = state.rate
                activity?.invalidateOptionsMenu()
                stopTimerTask()
                startTimerTask()
            }

            else -> Unit
        }
    }

    private fun handleRefreshRateUpdate(data: Int) {
        //reset menu checked status
        actionbarMenu?.apply {
            findItem(R.id.action_refresh_1_min).isChecked = false
            findItem(R.id.action_refresh_5_min).isChecked = false
            findItem(R.id.action_refresh_10_min).isChecked = false
        }

        when (data) {
            1 -> {
                actionbarMenu?.apply {
                    findItem(R.id.action_refresh_1_min).isChecked = true
                }
            }

            5 -> {
                actionbarMenu?.apply {
                    findItem(R.id.action_refresh_5_min).isChecked = true
                }
            }

            10 -> {
                actionbarMenu?.apply {
                    findItem(R.id.action_refresh_10_min).isChecked = true
                }
            }
        }
    }

    private fun handleViewModelEvent(event: TimeDashBoardEvent) {
        Log.d(TAG, "handleViewModelEvent: $event")

    }

    private fun handleTimeZoneDataReady(data: List<TimeZoneInfo>) {
        Log.d(TAG, "handleTimeZoneDatabaseDataReady: $data")
        binding.loadingProgress.visibility = View.INVISIBLE

        digitalClockAdapter = DigitalClockListAdapter().apply {
            data?.let {
                setData(it)
            }
        }
        binding.recyclerView.apply {
            adapter = digitalClockAdapter
            digitalClockAdapter?.notifyDataSetChanged()
        }
    }

    private fun startTimerTask() {
        if (timer == null) {
            timer = Timer()
        }

        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.d(TAG, "timer task exec")
                getTimeZones()
            }
        }, 0, (refreshRate * 60 * 1000).toLong())
    }

    private fun stopTimerTask() {
        Log.d(TAG, "timer task stop")
        timer?.cancel()
        timer = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimerTask()
        _binding = null
    }

    companion object {
        private const val COLUMN_SIZE = 2
        private const val TAG = "TimeDashBoardFragment"
    }
}