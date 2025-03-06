package com.ssc.android.vs_digital_clock.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssc.android.vs_digital_clock.data.MockDataUtil
import com.ssc.android.vs_digital_clock.databinding.FragmentTimeDashBoardBinding
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
            layoutManager = GridLayoutManager(context, COLUMN_SIZE).apply {
                
            }
            adapter = digitalClockAdapter
            addItemDecoration(DividerItemDecoration(context,GridLayoutManager.VERTICAL))
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val COLUMN_SIZE = 2
    }
}