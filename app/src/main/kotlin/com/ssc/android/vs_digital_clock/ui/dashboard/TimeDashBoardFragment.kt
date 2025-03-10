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

@AndroidEntryPoint
class TimeDashBoardFragment : Fragment() {
    private var _binding: FragmentTimeDashBoardBinding? = null
    private val binding get() = _binding!!
    private var digitalClockAdapter: DigitalClockListAdapter? = null
    private val viewModel: TimeDashBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimeDashBoardBinding.inflate(inflater, container, false)
        initActionbar()
        initViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        binding.loadingProgress.visibility = View.VISIBLE
        viewModel.sendIntention(TimeDashBoardIntention.FetchTimeZones)
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

    private fun initActionbar() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_time_dash_board, menu)
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

            else -> Unit
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val COLUMN_SIZE = 2
        private const val TAG = "TimeDashBoardFragment"
    }
}