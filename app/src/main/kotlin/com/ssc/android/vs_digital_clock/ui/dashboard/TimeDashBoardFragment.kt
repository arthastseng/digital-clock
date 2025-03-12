package com.ssc.android.vs_digital_clock.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.data.datastore.RefreshRate
import com.ssc.android.vs_digital_clock.data.datastore.SystemLanguage
import com.ssc.android.vs_digital_clock.databinding.FragmentTimeDashBoardBinding
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import com.ssc.android.vs_digital_clock.presentation.state.TimeDashBoardEvent
import com.ssc.android.vs_digital_clock.presentation.state.TimeDashBoardIntention
import com.ssc.android.vs_digital_clock.presentation.state.TimeDashBoardViewState
import com.ssc.android.vs_digital_clock.presentation.viewmodel.TimeDashBoardViewModel
import com.ssc.android.vs_digital_clock.service.FloatingWindowService
import com.ssc.android.vs_digital_clock.service.FloatingWindowService.Companion.DATA_BUNDLE_KEY
import com.ssc.android.vs_digital_clock.service.FloatingWindowUpdateUtil
import com.ssc.android.vs_digital_clock.ui.util.LocaleUtil
import com.ssc.android.vs_digital_clock.ui.util.collectFlowWhenStart
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class TimeDashBoardFragment : Fragment() {
    private var _binding: FragmentTimeDashBoardBinding? = null
    private val binding get() = _binding!!
    private var digitalClockAdapter: DigitalClockListAdapter? = null
    private val viewModel: TimeDashBoardViewModel by viewModels()
    private var actionbarMenu: Menu? = null
    private var refreshRate: Int = RefreshRate.ONE_MINUTE.rate
    private var systemLanguage: String = SystemLanguage.EN.code
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
        getPreference()
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

    private fun getPreference() {
        binding.noDataHint.visibility = View.INVISIBLE
        viewModel.sendIntention(TimeDashBoardIntention.GetPreference)
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
                updateActionbarMenuCheckStatus()
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
                        viewModel.sendIntention(
                            TimeDashBoardIntention.RefreshRateChanged(
                                rate = RefreshRate.ONE_MINUTE.rate
                            )
                        )
                        true
                    }

                    R.id.action_refresh_5_min -> {
                        Log.d(TAG, "refresh 5 min selected")
                        viewModel.sendIntention(
                            TimeDashBoardIntention.RefreshRateChanged(
                                rate = RefreshRate.FIVE_MINUTES.rate
                            )
                        )
                        true
                    }

                    R.id.action_refresh_10_min -> {
                        Log.d(TAG, "refresh 10 min selected")
                        viewModel.sendIntention(
                            TimeDashBoardIntention.RefreshRateChanged(
                                rate = RefreshRate.TEN_MINUTES.rate
                            )
                        )
                        true
                    }

                    R.id.action_language_en -> {
                        viewModel.sendIntention(
                            TimeDashBoardIntention.LanguageChanged(
                                language = SystemLanguage.EN.code
                            )
                        )
                        true
                    }

                    R.id.action_language_zh_tw -> {
                        viewModel.sendIntention(
                            TimeDashBoardIntention.LanguageChanged(
                                language = SystemLanguage.ZH_TW.code
                            )
                        )
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

            is TimeDashBoardViewState.GetPreferenceCompleted -> {
                refreshRate = state.rate
                systemLanguage = state.language
                activity?.invalidateOptionsMenu()
                startTimerTask()
            }

            is TimeDashBoardViewState.LanguageUpdateCompleted -> {
                systemLanguage = state.language
                activity?.invalidateOptionsMenu()

                context?.let {
                    val newLocale = LocaleUtil.getLocale(state.language)
                    changeLanguage(context = it, newLocale = newLocale)
                }
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

    private fun updateActionbarMenuCheckStatus() {
        //reset menu checked status
        actionbarMenu?.apply {
            //reset refresh rate
            findItem(R.id.action_refresh_1_min).isChecked = false
            findItem(R.id.action_refresh_5_min).isChecked = false
            findItem(R.id.action_refresh_10_min).isChecked = false
            //reset system language
            findItem(R.id.action_language_en).isChecked = false
            findItem(R.id.action_language_zh_tw).isChecked = false
        }

        when (refreshRate) {
            RefreshRate.ONE_MINUTE.rate -> {
                actionbarMenu?.apply {
                    findItem(R.id.action_refresh_1_min).isChecked = true
                }
            }

            RefreshRate.FIVE_MINUTES.rate -> {
                actionbarMenu?.apply {
                    findItem(R.id.action_refresh_5_min).isChecked = true
                }
            }

            RefreshRate.TEN_MINUTES.rate -> {
                actionbarMenu?.apply {
                    findItem(R.id.action_refresh_10_min).isChecked = true
                }
            }
        }

        when (systemLanguage) {
            SystemLanguage.EN.code -> {
                actionbarMenu?.apply {
                    findItem(R.id.action_language_en).isChecked = true
                }
            }

            SystemLanguage.ZH_TW.code -> {
                actionbarMenu?.apply {
                    findItem(R.id.action_language_zh_tw).isChecked = true
                }
            }
        }
    }

    private fun handleViewModelEvent(event: TimeDashBoardEvent) {
        if (event is TimeDashBoardEvent.ErrorOccur) {
            showErrorDialog(error = event.error)
        }
    }

    private fun showErrorDialog(error: SystemError) {
        context?.let {
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            builder
                .setMessage(error.errorMsg)
                .setTitle(it.resources.getString(R.string.error_occur))
                .setPositiveButton(it.resources.getString(R.string.retry)) { _, _ ->
                    getPreference()
                }
                .setNegativeButton(it.resources.getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun handleTimeZoneDataReady(data: List<TimeZoneInfo>) {
        Log.d(TAG, "handleTimeZoneDatabaseDataReady: $data")
        binding.loadingProgress.visibility = View.INVISIBLE

        if (data.isEmpty()) {
            binding.noDataHint.visibility = View.VISIBLE
            stopTimerTask()
        } else {
            binding.noDataHint.visibility = View.INVISIBLE
        }

        digitalClockAdapter = DigitalClockListAdapter().apply {
            data?.let {
                setData(it)
                setOnItemClickListener(object : DigitalClockListAdapter.OnItemClickListener {
                    override fun onItemClick(data: TimeZoneInfo) {
                        permissionCheck(data = data)
                    }
                })
            }
        }
        binding.recyclerView.apply {
            adapter = digitalClockAdapter
            digitalClockAdapter?.notifyDataSetChanged()
        }

        FloatingWindowUpdateUtil.updateData(data)
    }

    private fun permissionCheck(data: TimeZoneInfo) {
        Log.d(TAG, "startFloatingClock: $data")
        context?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(it)) {
                    // Redirect the user to the Settings page to grant permission
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivityForResult(intent, REQUEST_CODE)
                } else {
                    createFloatingWindow(data = data)
                }
            } else {
                // No need to request permission for versions lower than Android 6.0
                createFloatingWindow(data = data)
            }
        }
    }

    private fun createFloatingWindow(data: TimeZoneInfo) {
        val intent = Intent(context, FloatingWindowService::class.java).apply {
            putExtra(DATA_BUNDLE_KEY, Gson().toJson(data))
        }
        intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        activity?.startForegroundService(intent)
    }

    private fun setAppLanguage(context: Context, locale: Locale) {
        context?.let {
            val context = it
            val resource = it.resources
            val metrics = resource.displayMetrics
            val configuration = resource.configuration

            configuration.setLocale(locale)
            context.createConfigurationContext(configuration)
            resource.updateConfiguration(configuration, metrics)
        }
    }

    private fun changeLanguage(context: Context, newLocale: Locale = Locale.ROOT) {
        setAppLanguage(context = context, locale = newLocale)
        activity?.apply {
            ActivityCompat.recreate(this)
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
        private const val REQUEST_CODE = 1100
    }
}