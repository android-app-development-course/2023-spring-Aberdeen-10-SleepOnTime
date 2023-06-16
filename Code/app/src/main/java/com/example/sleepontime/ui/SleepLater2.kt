package com.example.sleepontime.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.sleepontime.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.text.SimpleDateFormat
import java.util.*

class SleepLater2 : AppCompatActivity() {

    private var isPickerVisible by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", -1)
        setContentView(R.layout.activity_sleep_now)

        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.PACKAGE_USAGE_STATS)
            .permission(Permission.SCHEDULE_EXACT_ALARM)
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        Toast.makeText(this@SleepLater2, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Toast.makeText(this@SleepLater2, "获取录音和日历权限成功", Toast.LENGTH_SHORT).show()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        Toast.makeText(this@SleepLater2, "被永久拒绝授权，请手动授予录音和日历权限", Toast.LENGTH_SHORT).show()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@SleepLater2, permissions)
                    } else {
                        Toast.makeText(this@SleepLater2, "获取录音和日历权限失败", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        val composeContainer = findViewById<ComposeView>(R.id.composeContainer)

        //接收Sleep Later1 的睡觉时间
        val LaterHour1 = intent.getIntExtra("hour", 0)
        val LaterMinute1 = intent.getIntExtra("minute", 0)

        composeContainer.setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {

                        val showTimePicker = findViewById<Button>(R.id.timepicker_button2)
                        showTimePicker.setOnClickListener {
                            isPickerVisible = true
                        }
//                        Button(onClick = { isPickerVisible = true }) {
//                            Text(text = "Show time picker")
//                        }

//                        fun startCountdownService(targetTime: Long) {
//                            val intent = Intent(this@SleepLater2, CountdownService::class.java)
//                            intent.putExtra(CountdownService.TARGET_TIME_EXTRA, targetTime)
//                            ContextCompat.startForegroundService(this@SleepLater2, intent)
//
//                        }

                        if (isPickerVisible) {

                            val picker = MaterialTimePicker.Builder()
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(12)
                                .setMinute(10)
                                .setTitleText("Select Appointment time")
                                .build()
                            picker.show(supportFragmentManager, "tag")
                            picker.addOnPositiveButtonClickListener {
                                val hour = picker.hour
                                val minute = picker.minute
//                                val intent = Intent(this@SleepLater2, CountdownReady::class.java)
//                                intent.putExtra("hour2", hour)
//                                intent.putExtra("minute2", minute)
//                                intent.putExtra("hour1", LaterHour1)
//                                intent.putExtra("minute1", LaterMinute1)
//                                startActivity(intent)

                                //获取当前时间
                                val currentTime = Calendar.getInstance()
                                //设置目标睡觉时间
                                val targetTime = Calendar.getInstance()
                                targetTime.set(Calendar.HOUR_OF_DAY, LaterHour1)
                                targetTime.set(Calendar.MINUTE, LaterMinute1)
                                //调试用
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                val targetStr = dateFormat.format(targetTime.time)
                                val currentStr = dateFormat.format(currentTime.time)
                                println("========================================="+targetStr+"=========================================")
                                println("========================================="+currentStr+"=========================================")
                                //计算时间差
                                var timeDifferenceMillis = targetTime.timeInMillis - currentTime.timeInMillis

                                var secondsRemaining: Long = 0
                                var remainingHours: Long = 0
                                var remainingMinutes: Long = 0
                                if (timeDifferenceMillis < 0) {
                                    // 如果时间差小于0，表示目标时间是明天的时间，需要加上24小时
                                    val tomorrowTargetTime = targetTime.clone() as Calendar
                                    tomorrowTargetTime.add(Calendar.DAY_OF_YEAR, 1)
                                    val tomorrowTimeDifferenceMillis = tomorrowTargetTime.timeInMillis - currentTime.timeInMillis
                                    println(tomorrowTimeDifferenceMillis)
                                    timeDifferenceMillis = tomorrowTimeDifferenceMillis

                                    secondsRemaining = (tomorrowTimeDifferenceMillis / 1000) % 60
                                    remainingHours = (tomorrowTimeDifferenceMillis / (1000 * 60 * 60))
                                    remainingMinutes = (tomorrowTimeDifferenceMillis / (1000 * 60)) % 60

                                    // 显示倒计时时间
                                    val timeRemainingText = String.format("%02d:%02d:%02d", remainingHours, remainingMinutes, secondsRemaining)

                                    Toast.makeText(this@SleepLater2, "You will sleep after"+timeRemainingText, Toast.LENGTH_SHORT).show()

//                                    // 启动前台服务，并将目标时间传递给服务
//                                    startCountdownService(targetTime.timeInMillis)

                                } else {
                                    println(timeDifferenceMillis)
                                    secondsRemaining = (timeDifferenceMillis / 1000) % 60
                                    remainingHours = (timeDifferenceMillis / (1000 * 60 * 60))
                                    remainingMinutes = (timeDifferenceMillis / (1000 * 60)) % 60

                                    // 显示倒计时时间
                                    val timeRemainingText = String.format("%02d:%02d:%02d", remainingHours, remainingMinutes, secondsRemaining)

                                    Toast.makeText(this@SleepLater2, "You will sleep after"+timeRemainingText, Toast.LENGTH_SHORT).show()
                                    timeDifferenceMillis = timeDifferenceMillis
                                }

                                // 创建一个 AlarmManager 实例
                                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                                // 创建一个 PendingIntent，用于启动 CountdownStart 活动
                                val intent = Intent(this@SleepLater2, CountdownStart::class.java)
                                intent.putExtra("timeDifferenceMillis", timeDifferenceMillis)
                                intent.putExtra("userId", userId)
                                val pendingIntent = PendingIntent.getActivity(
                                    this@SleepLater2,
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )

                                // 设置闹钟时间为目标时间
                                alarmManager?.set(
                                    AlarmManager.RTC_WAKEUP,
                                    targetTime.timeInMillis,
                                    pendingIntent
                                )
//                                    }
//                                }
                            }
                        }
                    }
                }
            }
        }

        val navBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navBar.setOnItemSelectedListener {
            when (navBar.selectedItemId) {
                R.id.clock -> {
                    val intent = Intent(this, Choose::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    true
                }
                R.id.home -> {
                    val intent = Intent(this, HomePage::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
