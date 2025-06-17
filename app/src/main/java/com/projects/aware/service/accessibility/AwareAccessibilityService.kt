package com.projects.aware.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import com.projects.aware.R
import com.projects.aware.data.db.ObjectBoxManager
import com.projects.aware.data.repo.App
import com.projects.aware.data.repo.AppsRepo
import com.projects.aware.data.repo.formatDuration
import com.projects.aware.data.repo.isLauncherApp
import com.projects.aware.main.AwareApp
import com.projects.aware.service.foreground.LimitDialogActivity
import com.projects.aware.ui.screens.overlay.OverlaySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

class AwareAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var app: AwareApp
    private lateinit var settings: OverlaySettings
    private lateinit var appsRepo: AppsRepo
    private lateinit var objectBoxManager: ObjectBoxManager

    // State variables
    private var currentApp: String? = null
    private var sessionTimeMillis: Long = 0
    private var totalTimeMillis: Long = 0
    private var currentAppInfo: App? = null
    private var isOverlayHidden = false
    private var visibilitySetting = false


    // broadcast receiver
    private val settingsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.aware.actions.OverlayVisibility") {
                val isVisible = intent.getBooleanExtra("is_visible", false)
                visibilitySetting = isVisible
                if (visibilitySetting) {
                    showOverlay()
                } else {
                    hideOverlay()
                }
            } else if (intent?.action == "com.aware.overlay.settings.update") {
                setupOverlay()
            }
        }
    }

    private fun registerVisibilityReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction("com.aware.actions.OverlayVisibility")
            addAction("com.aware.overlay.settings.update")
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RECEIVER_EXPORTED
        } else {
            0
        }
        registerReceiver(settingsReceiver, intentFilter, flags)
    }


    // Initialization
    override fun onCreate() {
        app = application as AwareApp
        appsRepo = app.container.appsRepo
        objectBoxManager = ObjectBoxManager(app.boxStore)

        initView()

        setupOverlay()

        startTracking()

        registerVisibilityReceiver()
        super.onCreate()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED
        ) {
            val newPackage = event.packageName.toString()
            if (isFloatingWindow(event)) return
            if (!isLauncherApp(packageManager, newPackage)) {
                currentApp = null
                hideOverlay()
                return
            }
            if (newPackage != currentApp) {
                val appInfo =
                    getAppInfo(applicationContext, newPackage.toString(), appsRepo = appsRepo)
                totalTimeMillis = appInfo?.usageTime ?: 0L
                currentApp = newPackage

                // changing app
                serviceScope.launch(Dispatchers.IO) {
                    val excludedApps = objectBoxManager.excludedAppBox.all
                    currentAppInfo = appInfo
                    val shouldShow =
                        !excludedApps.any { it?.packageName == newPackage } && visibilitySetting

                    withContext(Dispatchers.Main) {
                        if (shouldShow) {
                            val name = currentAppInfo?.name ?: "refresh"
                            showOverlay()
                            isOverlayHidden = false
                            updateBubble(
                                timeFormatted = formatDuration(totalTimeMillis),
                                icon = currentAppInfo?.icon,
                                name = if (name.length > 10) name.substring(
                                    0,
                                    8
                                ) + ".." else name
                            )
                        }
                    }
                }
            }
        }
    }

    // overlay setup methods
    private fun initView() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_layout, null)


        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

        setDragListener()

        if (overlayView.windowToken == null && Settings.canDrawOverlays(this)) {
            windowManager.addView(overlayView, layoutParams)
        }

    }

    private fun setDragListener() {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        // Constants
        val DOUBLE_CLICK_TIME_DELTA: Long = 300 // Milliseconds between clicks
        var lastClickTime: Long = 0

        overlayView.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                // Double-click detected!
                hideOverlay()
                it.performHapticFeedback(10)
                lastClickTime = 0 // Reset to avoid triple-click false positives
            } else {
                // Single click (handle separately if needed)
                lastClickTime = clickTime
            }
            true
        }

        overlayView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Check if it was a click (i.e., the user didn't drag much)
                    val deltaX = (event.rawX - initialTouchX).absoluteValue
                    val deltaY = (event.rawY - initialTouchY).absoluteValue
                    if (deltaX < 10 && deltaY < 10) {
                        v.performClick()
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(overlayView, layoutParams)
                    true
                }

                else -> false
            }
        }
    }

    private fun setupOverlay() {
        val appIcon = overlayView.findViewById<ImageView>(R.id.app_icon)
        val appName = overlayView.findViewById<TextView>(R.id.app_name)
        val appTime = overlayView.findViewById<TextView>(R.id.app_time)
        val bubbleContainer = overlayView.findViewById<View>(R.id.bubble_container)

        settings = app.container.preferencesManager.getOverlaySettings()

        visibilitySetting = settings.isBubbleVisible
        overlayView.visibility = if (visibilitySetting) View.VISIBLE else View.INVISIBLE

        appIcon.visibility = if (settings.showAppIcon) View.VISIBLE else View.GONE
        appName.visibility = if (settings.showAppName) View.VISIBLE else View.GONE
        appTime.visibility = if (settings.showAppUsage) View.VISIBLE else View.GONE

        // adjusting props
        appName.setTextColor(settings.textColor.toArgb())
        appTime.setTextColor(settings.textColor.toArgb())
        bubbleContainer.setBackgroundColor(settings.background.toArgb())


        // adding background props
        val gradientBackground = GradientDrawable().apply {
            setColor(settings.background.toArgb())
            cornerRadius = settings.cornerRadius.toFloat()
            shape = GradientDrawable.RECTANGLE
        }
        bubbleContainer.background = gradientBackground
    }


    // visibility setup
    private fun hideOverlay() {
        // Add fade-out animation for better UX

        overlayView.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                overlayView.visibility = View.INVISIBLE
            }
            .start()


    }

    private fun showOverlay() {
        overlayView.animate()
            .alpha(1f)
            .setDuration(300)
            .withStartAction {
                overlayView.visibility = View.VISIBLE
            }
            .start()


    }

    suspend fun updateBubble(name: String?, timeFormatted: String, icon: Drawable?) {
        val timeView = overlayView.findViewById<TextView>(R.id.app_time)
        val iconView = overlayView.findViewById<ImageView>(R.id.app_icon)
        val nameView = overlayView.findViewById<TextView>(R.id.app_name)
        withContext(Dispatchers.Main) {
            nameView.text = name?.capitalize()
            timeView.text = timeFormatted
            iconView.setImageDrawable(icon)
        }
    }


    // tracking setup
    private fun startTracking() {
        serviceScope.launch {

            launch {
                // checking app limits
                while (isActive) {
                    currentApp?.let {
                        val app = withContext(Dispatchers.IO) {
                            getAppInfo(applicationContext, appsRepo = appsRepo, pkg = it)
                        }
                        val limits = objectBoxManager.limits.all
                        val appLimit = limits.find { it?.packageName == app?.packageName }
                        val shouldBlock = appLimit != null && totalTimeMillis >= appLimit.dailyLimit

                        if (shouldBlock) {
                            withContext(Dispatchers.Main) {
                                blockApp(appLimit.dailyLimit)
                            }
                        }
                    }
                    delay(1500L)
                }
            }

            launch {
                // session change
                while (isActive) {
                    sessionTimeMillis += 1000L
                    totalTimeMillis += 1000L

                    if (!visibilitySetting) {
                        delay(1000L)
                        continue
                    }

                    val name = currentAppInfo?.name ?: "refresh"
                    updateBubble(
                        timeFormatted = formatDuration(totalTimeMillis),
                        icon = currentAppInfo?.icon,
                        name = if (name.length > 10) name.substring(0, 8) + ".." else name
                    )
                    delay(1000L)
                }
            }
        }
    }

    // helper functions
    fun getAppInfo(context: Context, pkg: String, appsRepo: AppsRepo): App? {
        val pm = context.packageManager
        val stats = appsRepo.getAppsStats().values
        val time = stats.firstOrNull { it.packageName == pkg }

        return try {
            val app = pm.getApplicationInfo(pkg, 0)
            App(
                name = app.loadLabel(pm).toString(),
                packageName = app.packageName,
                icon = app.loadIcon(pm),
                usageTime = time?.totalForegroundTime ?: 0,
                unlockTimes = 0
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    fun blockApp(limit: Long) {
        val intent = Intent(applicationContext, LimitDialogActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("packageName", currentAppInfo?.packageName)
            putExtra("limit", limit)
        }
        applicationContext.startActivity(intent)
    }

    fun getForegroundApp(context: Context): String? {
        val usageStatsManager =
            context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 10  // Check the last 10 seconds
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)

        var lastApp: String? = null
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastApp = event.packageName
            }
        }
        return lastApp
    }

    fun isFloatingWindow(event: AccessibilityEvent): Boolean {
        val window = event.source ?: return true  // fallback: assume floating if no source

        try {
            val bounds = Rect()
            window.getBoundsInScreen(bounds)

            // Heuristic: ignore windows smaller than a reasonable screen fraction
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val isSmallWindow =
                bounds.width() < screenWidth / 2 || bounds.height() < screenHeight / 2
            return isSmallWindow
        } catch (e: Exception) {
            return true
        }
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
    }
}