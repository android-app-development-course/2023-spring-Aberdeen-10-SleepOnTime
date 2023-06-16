package com.example.sleepontime.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.sleepontime.R


class CountdownService : Service() {
    override fun onCreate() {
        super.onCreate()
        // 在这里创建并设置前台服务通知
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 处理在时间到达时将应用程序带到前台并跳转到相应页面的逻辑
        val targetTime = intent?.getLongExtra(TARGET_TIME_EXTRA, 0L) ?: 0L
        val intent = Intent(this, CountdownStart::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        // 在这里执行其他操作，如开始倒计时等

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun buildNotification(): Notification {
        // 创建前台服务通知
        val notificationIntent = Intent(this, CountdownStart::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown Service")
            .setContentText("Countdown in progress...")
            .setContentIntent(pendingIntent)
            .build()

        return notification
    }

    companion object {
        private const val CHANNEL_ID = "CountdownServiceChannel"
        private const val NOTIFICATION_ID = 123456
        const val TARGET_TIME_EXTRA = "target_time_extra"
    }
}