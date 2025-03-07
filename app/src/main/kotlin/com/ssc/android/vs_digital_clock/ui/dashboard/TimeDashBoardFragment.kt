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
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.data.MockDataUtil
import com.ssc.android.vs_digital_clock.databinding.FragmentTimeDashBoardBinding
import com.ssc.android.vs_digital_clock.ui.dashboard.DigitalClockListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeDashBoardFragment : Fragment() {
    private var _binding: FragmentTimeDashBoardBinding? = null
    private val binding get() = _binding!!
    private var adapter: DigitalClockListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimeDashBoardBinding.inflate(inflater, container, false)
        initActionbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {

        //TODO mock data
        val mockData = MockDataUtil.createMockData()

        val digitalClockAdapter = DigitalClockListAdapter().apply {
            setData(data = mockData)
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, COLUMN_SIZE)
            adapter = digitalClockAdapter
            addItemDecoration(DividerItemDecoration(context,GridLayoutManager.VERTICAL))
            adapter?.notifyDataSetChanged()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val COLUMN_SIZE = 2
        private const val TAG = "TimeDashBoardFragment"
    }
}