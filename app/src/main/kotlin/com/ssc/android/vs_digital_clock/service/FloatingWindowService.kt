package com.ssc.android.vs_digital_clock.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FloatingWindowService : Service() {

    private lateinit var windowMgr: WindowManager
    private lateinit var floatingView: View
    private var currentTimeZone: String? = null
    private val serviceJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Window Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create the foreground notification
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Floating Window Active")
            .setContentText("Click to stop the service.")
            .build()

        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val timeZoneInfo =
            Gson().fromJson(intent?.getStringExtra(DATA_BUNDLE_KEY), TimeZoneInfo::class.java)
        currentTimeZone = timeZoneInfo.timeZone
        createFloatingWindow(data = timeZoneInfo)

        coroutineScope.launch {
            FloatingWindowUpdateUtil.dataFlow.collect {
                if (it.isNotEmpty())
                    withContext(Dispatchers.Main) {
                        val data = fetchCurrentTimeZoneData(it)
                        data?.let { newTimeZoneInfo ->
                            updateData(data = newTimeZoneInfo)
                        }
                    }
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    private fun fetchCurrentTimeZoneData(data: List<TimeZoneInfo>): TimeZoneInfo? {
        currentTimeZone?.let { curTimeZone ->
            data.forEach { newTimeZone ->
                if (curTimeZone == newTimeZone.timeZone) {
                    return newTimeZone
                }
            }
        }

        return null
    }

    private fun updateData(data: TimeZoneInfo) {
        Log.d(TAG, "updateDataFromFragment : $data")
        floatingView.let {
            val time: TextView = it.findViewById(R.id.time)
            val timeZone: TextView = it.findViewById(R.id.timezone)
            time.text = data.time
            timeZone.text = data.timeZone
        }
    }

    private fun createFloatingWindow(data: TimeZoneInfo) {
        val layoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = layoutInflater.inflate(R.layout.widget_floating_clock, null)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        windowMgr = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams(
            applicationContext.resources.getDimension(R.dimen.digital_clock_item_width).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Set the initial position of the floating window
        layoutParams.gravity = Gravity.TOP or Gravity.LEFT
        layoutParams.x = 300
        layoutParams.y = 300

        // Add the floating view to the WindowManager
        windowMgr.addView(floatingView, layoutParams)

        // Make the floating window draggable
        val touchListener = object : View.OnTouchListener {
            private var xOffset = 0
            private var yOffset = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        xOffset = (event.rawX - layoutParams.x).toInt()
                        yOffset = (event.rawY - layoutParams.y).toInt()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = (event.rawX - xOffset).toInt()
                        layoutParams.y = (event.rawY - yOffset).toInt()
                        windowMgr.updateViewLayout(floatingView, layoutParams)
                    }
                }
                return true
            }
        }

        floatingView.let {
            it.setOnTouchListener(touchListener)
            val closeButton: ImageButton = it.findViewById(R.id.close)
            val time: TextView = it.findViewById(R.id.time)
            val timeZone: TextView = it.findViewById(R.id.timezone)
            time.text = data.time
            timeZone.text = data.timeZone
            closeButton.setOnClickListener {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::windowMgr.isInitialized && ::floatingView.isInitialized) {
            windowMgr.removeView(floatingView)
            currentTimeZone = null
        }
        serviceJob.cancel()
    }

    companion object {
        private const val CHANNEL_ID = "floating_service_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "FloatingWindowService"
        const val DATA_BUNDLE_KEY = "data_bundle"
    }
}