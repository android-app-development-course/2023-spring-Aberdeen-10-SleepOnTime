package com.example.sleepontime.ui

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.*
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.sleepontime.R
import com.example.sleepontime.data.DatabaseHelper
import com.example.sleepontime.data.DatabaseHelper.Companion.COLUMN2_AVAI
import com.example.sleepontime.data.DatabaseHelper.Companion.TABLE2_NAME
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.XXPermissions.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray


class CountdownStart : AppCompatActivity() {

    private var remainingHours: Long = 0
    private var remainingMinutes: Long = 0
    private var secondsRemaining: Long = 0
    private val handler = Handler()
    private var isCountdownFinished = false
    private var isKillProcessServiceRunning = false
    private var isExitButtonClicked = false
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var bootBroadcastReceiver: BootBroadcastReceiver
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var serviceIntent: Intent


    // 定义一个类来表示 weekhour 列表结构
    data class WeekHourList(val weekHour: List<Int>)


    override fun onCreate(savedInstanceState: Bundle?) {


        val musicButton = findViewById<ImageButton>(R.id.music)
        val userId = intent.getIntExtra("userId", -1)
        super.onCreate(savedInstanceState)

        with(this)
            .permission(Permission.PACKAGE_USAGE_STATS)
            .permission(Permission.SYSTEM_ALERT_WINDOW)
            .permission(Permission.SCHEDULE_EXACT_ALARM)
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        Toast.makeText(this@CountdownStart, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Toast.makeText(this@CountdownStart, "获取权限成功", Toast.LENGTH_SHORT).show()

                    dbHelper = DatabaseHelper(this@CountdownStart)

                    // 启动后台进程监控服务
                    serviceIntent = Intent(this@CountdownStart, KillProcessService::class.java)
                    startForegroundService(serviceIntent)

                    // 锁定Home键和返回键
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    )

                    setContentView(R.layout.activity_countdown_start)

                    val timeDifferenceMillis = intent.getLongExtra("timeDifferenceMillis", 0)
                    val tvCountDown = findViewById<TextView>(R.id.tvCountDown)
                    val exitButton = findViewById<Button>(R.id.exit)

                    if (timeDifferenceMillis < 0) {
                        println("负数")
                        println(timeDifferenceMillis)
                    }

                    // 开始倒计时
                    countdownTimer = object : CountDownTimer(timeDifferenceMillis, 1000) {
                        override fun onTick(millisUntilFinished: Long) {

                            // 显示倒计时时间
                            val timeRemainingText = formatTimeRemaining(millisUntilFinished)
                            tvCountDown.text = timeRemainingText
                        }

                        // 倒计时结束
                        override fun onFinish() {
                            isCountdownFinished = true
                            println("这里倒计时结束了")
                            handler.post {
                                val intent = Intent(this@CountdownStart, Choose::class.java)
                                startActivity(intent)
                                //关停后台服务
                                stopService(serviceIntent)
                                println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%-OnFinish 关停了服务-%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
                                // 设置isKillProcessServiceRunning为true
                                isKillProcessServiceRunning = true
                                // 调用onDestroy方法
                                onDestroy()

                                if (!isExitButtonClicked) {
                                    println("将会存到数据库")
                                    println(timeDifferenceMillis/ (1000 * 60 * 60))
                                    println((timeDifferenceMillis/ 1000) % 60)
                                    println((timeDifferenceMillis / (1000 * 60)) % 60)

                                    //获取今天星期几
                                    val calendar = Calendar.getInstance()
                                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                                    //只存入小时和分钟
                                    val minutesToHour = (timeDifferenceMillis / (1000 * 60)) % 60 / 60
                                    val hours = timeDifferenceMillis/ (1000 * 60 * 60)
                                    val sleepTime = minutesToHour + hours

                                    var db = dbHelper.writableDatabase
                                    var weekHour = dbHelper.getWeekHour(userId)
                                    // 将 weekhour 字符串转换为列表
                                    val weekHourList = weekHour!!.toMutableList()
                                    val updateValue = weekHourList[dayOfWeek-1] + sleepTime.toInt()
                                    weekHourList[dayOfWeek-1] = updateValue

                                    // 将 weekHourList 转换为 JSONArray
                                    val jsonArray = JSONArray(weekHourList)
                                    // 将 JSONArray 转换为字符串
                                    val weekHourStr = jsonArray.toString()
                                    dbHelper.updateWeekHour(userId, weekHourStr)
                                    db.close()
                                } else {
                                    println("不会存到数据库")
                                }
                            }
                        }
                    }.start()


//                    //点击音乐
//                    musicButton.setOnClickListener {
//                        val intent = Intent(this@CountdownStart, ExoPlayerMusic::class.java)
//                        startActivity(intent)
//                    }

                    // 注册返回键回调函数
                    onBackPressedCallback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            Toast.makeText(this@CountdownStart, "The device is locked", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 将返回键回调函数添加到当前活动
                    onBackPressedDispatcher.addCallback(this@CountdownStart, onBackPressedCallback)

                    // 注册开机广播接收器
                    bootBroadcastReceiver = BootBroadcastReceiver()
                    val intentFilter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
                    registerReceiver(bootBroadcastReceiver, intentFilter)

                    // 点击退出
                    exitButton.setOnClickListener {
                        // 显示对话框
                        MaterialAlertDialogBuilder(this@CountdownStart)
                            .setTitle(resources.getString(R.string.title))
                            .setMessage(resources.getString(R.string.supporting_text))
                            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                                // Respond to neutral button press
                                dialog.dismiss()
                            }
                            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->

                                var db = dbHelper.writableDatabase

                                val isAvailable = dbHelper.isAvailable(userId)  // 替换为正确的活动类名

                                if (isAvailable) {

                                    if (!db.isOpen) {
                                        db = dbHelper.writableDatabase
                                    }
                                    val query = "UPDATE $TABLE2_NAME SET $COLUMN2_AVAI = $COLUMN2_AVAI - 1"
                                    db.execSQL(query)

                                    dialog.dismiss()
                                    val intent = Intent(this@CountdownStart, Choose::class.java)
                                    startActivity(intent)
                                    //通过exit button 退出
                                    isExitButtonClicked = true
                                    finish()
                                    // 设置isKillProcessServiceRunning为true
                                    isKillProcessServiceRunning = true
                                    // 调用onDestroy方法
                                    onDestroy()
                                } else {
                                    Toast.makeText(this@CountdownStart, "You don't have enough unlock times", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                                db.close()
                            }
                            .show()
                    }
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        Toast.makeText(this@CountdownStart, "被永久拒绝授权，请手动授予录音和日历权限", Toast.LENGTH_SHORT).show()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        startPermissionActivity(this@CountdownStart, permissions)
                    } else {
                        Toast.makeText(this@CountdownStart, "获取录音和日历权限失败", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }


    fun parseWeekHour(weekHourStr: String): List<Int> {
        return try {
            val weekHourList = Json.decodeFromString<WeekHourList>(weekHourStr)
            weekHourList.weekHour
        } catch (e: Exception) {
            // 处理解析异常
            emptyList() // 返回一个空列表或其他默认值
        }
    }

    private fun formatTimeRemaining(millisUntilFinished: Long): String {
        secondsRemaining = (millisUntilFinished / 1000) % 60
        remainingHours = (millisUntilFinished / (1000 * 60 * 60))
        remainingMinutes = (millisUntilFinished / (1000 * 60)) % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", remainingHours, remainingMinutes, secondsRemaining)
    }

    override fun onDestroy() {
        // 移除返回键回调函数
        onBackPressedCallback.remove()
        // 注销开机广播接收器
        unregisterReceiver(bootBroadcastReceiver)
        // 如果服务正在运行，停止和关闭服务
        if (isKillProcessServiceRunning) {
            stopService(serviceIntent)
            println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%-OnDEstory 关停了服务-%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
            isKillProcessServiceRunning = false
        }
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    class BootBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                val bootStartIntent = Intent(context, CountdownStart::class.java)
                bootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(bootStartIntent)
            }
        }
    }
}

class KillProcessService : Service() {
    private lateinit var context: Context
    private lateinit var appPackageName: String
    private lateinit var countdownTimer: CountDownTimer

    private var notificationIdCounter = 1

    //生成唯一的通知ID
    private fun generateNotificationId(): Int {
        return notificationIdCounter++
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun getRecentAppPackageName(context: Context): String? {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        if (usageStatsManager != null) {
            // 获取当前时间戳
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 5000 // 设置查询的时间范围，例如最近5秒内的应用程序

            // 查询最近使用的应用程序
            val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime)
            if (usageStatsList != null && usageStatsList.isNotEmpty()) {
                // 找到最近使用的应用程序
                var recentUsageStats: UsageStats? = null
                for (usageStats in usageStatsList) {
                    if (recentUsageStats == null || usageStats.lastTimeUsed > recentUsageStats.lastTimeUsed) {
                        recentUsageStats = usageStats
                    }
                }

                // 获取最近使用应用程序的包名
                if (recentUsageStats != null) {
                    return recentUsageStats.packageName
                }
            }
        }
        return null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        appPackageName = context.packageName

        // 启动前台服务
        val channelId = "sleep_on_time_channel" // 通知渠道ID
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Notification Title") // 设置通知标题
            .setContentText("Notification Content") // 设置通知内容
            .build()

        startForeground(generateNotificationId(), notificationBuilder)

        //创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "sleep_on_time_channel"
            val channelName = "Your Channel Name"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val handler = Handler(Handler.Callback {
            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val runningTasks = am.getRunningTasks(1)
            val runningTaskInfo = runningTasks[0]
            val topActivity = runningTaskInfo.topActivity
            val packageName = topActivity?.packageName

            println("+++++++++++++++++++++++++++++++++++"+getRecentAppPackageName(this@KillProcessService)+"+++++++++++++++++++++++++++++++++++")

            if (getRecentAppPackageName(this@KillProcessService) == appPackageName || getRecentAppPackageName(this@KillProcessService) == null) { // 判断是否是您的应用包名
                // 是您的应用，不进行操作
            } else {
                // 不是您的应用，启动您的应用并关闭前台应用
                println(packageName)
                println(appPackageName)
                println("=========================================不是您的应用=========================================")

                //返回原来的应用
                val intent = Intent(applicationContext, CountdownStart::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                am.killBackgroundProcesses(packageName)
                stopSelf()
            }
            false
        })

        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                handler.obtainMessage().sendToTarget()
            }
        }
        timer.schedule(timerTask, 0, 150) // 0.15秒启动一次timerTask，无延迟
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在这里执行清理操作和停止服务的逻辑
        stopSelf()
    }
}