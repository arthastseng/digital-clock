package com.ssc.android.vs_digital_clock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssc.android.vs_digital_clock.databinding.FragmentTimeDashBoardBinding

class TimeDashBoardFragment : Fragment() {
    private var _binding: FragmentTimeDashBoardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimeDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}