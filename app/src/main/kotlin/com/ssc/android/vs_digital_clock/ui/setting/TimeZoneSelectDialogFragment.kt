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
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.databinding.FragmentTimeZoneListViewBinding

class TimeZoneSelectDialogFragment : DialogFragment() {
    private var _binding: FragmentTimeZoneListViewBinding? = null
    private val binding get() = _binding!!
    private var dataList: List<String>? = null
    private var eventListener: DialogEventListener? = null

    interface DialogEventListener {
        fun onDismiss()
        fun onItemSelected(data: TimeZone)
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
        val timeZoneSelectListAdapter = SelectTimeZoneListAdapter().apply {
            dataList?.let {
                setData(it)
            }
        }
        val layoutMgr = LinearLayoutManager(context)

        binding.recyclerView.apply {
            layoutManager = layoutMgr
            adapter = timeZoneSelectListAdapter.apply {
                setOnItemClickedListener(object: SelectTimeZoneListAdapter.OnItemClickListener{
                    override fun onItemClicked(data: String) {
                        Log.d(TAG,"onItemClicked: $data")
                        val timeZone = createTimeZone(timeZoneString = data)
                        Log.d(TAG,"TimeZone created: ${timeZone.toString()}")
                        eventListener?.onItemSelected(data = timeZone)
                        dismiss()
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

    fun setDismissListener(listener: DialogEventListener) {
        eventListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        eventListener?.onDismiss()
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

    private fun createTimeZone(timeZoneString: String) : TimeZone {
        val result = timeZoneString.split("/")
        val region = result[0]
        val city = result[1]
        return TimeZone(indexKey = timeZoneString, region = region, city = city)
    }

    companion object {
        private const val TAG = "TimezoneSelectedDialogFragment"
        const val TIME_ZONE_DATA = "timeZoneData"
    }
}