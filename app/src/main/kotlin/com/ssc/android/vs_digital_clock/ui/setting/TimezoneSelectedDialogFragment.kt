package com.ssc.android.vs_digital_clock.ui.setting

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.databinding.FragmentTimeZoneListViewBinding

class TimezoneSelectedDialogFragment : DialogFragment() {
    private var _binding: FragmentTimeZoneListViewBinding? = null
    private val binding get() = _binding!!
    private var dataList: List<String>? = null
    private var dismissListener: DismissListener? = null

    interface DismissListener {
        fun onDismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimeZoneListViewBinding.inflate(inflater, container, false)
        isCancelable = true
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        super.onStart()
        dialog?.window?.apply {
            val width = resources.getDimension(R.dimen.time_zone_select_dialog_width).toInt()
            val height = resources.getDimension(R.dimen.time_zone_select_dialog_height).toInt()
            this.setLayout(width, height)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = arguments
        dataList = args?.getStringArrayList(TIME_ZONE_DATA) ?: ArrayList()
        initKeyEventListener()
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        val digitalClockAdapter = TimeZoneListAdapter().apply {
            dataList?.let {
                setData(it)
            }
        }
        val layoutMgr = LinearLayoutManager(context)

        binding.recyclerView.apply {
            layoutManager = layoutMgr
            adapter = digitalClockAdapter.apply {
                setOnItemClickedListener(object: TimeZoneListAdapter.OnItemClickListener{
                    override fun onItemClicked(data: String) {
                        Log.d(TAG,"onItemClicked: $data")
                    }
                })
            }
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter?.notifyDataSetChanged()
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

    fun setDismissListener(listener: DismissListener) {
        dismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss()
    }

    private fun initKeyEventListener() {
        dialog?.setOnKeyListener { _, i, keyEvent ->
            val keyCode = keyEvent.keyCode
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
                true
            } else {
                false
            }
        }
    }

    companion object {
        private const val TAG = "TimezoneSelectedDialogFragment"
        const val TIME_ZONE_DATA = "timeZoneData"
    }
}