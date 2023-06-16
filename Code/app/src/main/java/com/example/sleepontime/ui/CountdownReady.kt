package com.example.sleepontime.ui

import android.content.Intent
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.sleepontime.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*


class CountdownReady : AppCompatActivity() {
    private lateinit var bottomSheetDialog: BottomSheetDialog
    val timeZero: Long = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countdown_ready)

        val userId = intent.getIntExtra("userId", -1)
        val musicButton = findViewById<ImageButton>(R.id.music)
        val startButton = findViewById<Button>(R.id.start)

        if (intent.hasExtra("hour")) {

            //
            println("现在睡觉")

            val hour = intent.getIntExtra("hour",0)
            val minute = intent.getIntExtra("minute",0)
            var timeDifferenceMillis: Long = 0


            //点击开始，跳转页面
            startButton.setOnClickListener {

                //获取当前时间
                val timeZone = TimeZone.getTimeZone("GMT+8")
                val currentTime = Calendar.getInstance()
//                currentTime.timeZone = timeZone

                // 设置获取到的hour 和 minute
                val targetTime = Calendar.getInstance()
//                targetTime.timeZone = timeZone
                targetTime.set(Calendar.HOUR_OF_DAY, hour)
                targetTime.set(Calendar.MINUTE, minute)

                //调试用
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                dateFormat.timeZone = TimeZone.getTimeZone("GMT+8")

                val targetStr = dateFormat.format(targetTime.time)
                val currentStr = dateFormat.format(currentTime.time)
                println("========================================="+targetStr+"=========================================")
                println("========================================="+currentStr+"=========================================")

                //计算时间差
                var timeDifferenceMillis = targetTime.timeInMillis - currentTime.timeInMillis

                if (timeDifferenceMillis < 0) {
                    // 如果时间差小于0，表示目标时间是明天的时间，需要加上24小时
                    val tomorrowTargetTime = targetTime.clone() as Calendar
                    tomorrowTargetTime.add(Calendar.DAY_OF_YEAR, 1)
                    val tomorrowTimeDifferenceMillis = tomorrowTargetTime.timeInMillis - currentTime.timeInMillis
                    println(tomorrowTimeDifferenceMillis)

                    val intent = Intent(this, CountdownStart::class.java)
                    intent.putExtra("timeDifferenceMillis", tomorrowTimeDifferenceMillis)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                } else if (timeDifferenceMillis > 0) {
                    println(timeDifferenceMillis)
                    val intent = Intent(this, CountdownStart::class.java)
                    intent.putExtra("timeDifferenceMillis", timeDifferenceMillis)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                } else {
                    println("时间差为0的时候")
                    Toast.makeText(this@CountdownReady, "You have selected the present time", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CountdownReady, SleepNow::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }
            }
        } else {

            //
            println("过会儿睡觉")

            val hour1 = intent.getIntExtra("hour1", 0)
            val minute1 = intent.getIntExtra("minute1", 0)
            val hour2 = intent.getIntExtra("hour2", 0)
            val minute2 = intent.getIntExtra("minute2", 0)
            var timeDifferenceMillis: Long = 0

            //点击开始，跳转页面
            startButton.setOnClickListener {

                val timeZone = TimeZone.getTimeZone("GMT+8")
                //计划睡觉时间
                val sleepTime = Calendar.getInstance()
                sleepTime.set(Calendar.HOUR_OF_DAY, hour1)
                sleepTime.set(Calendar.MINUTE, minute1)

                //计划起床时间
                val targetTime = Calendar.getInstance()
                targetTime.set(Calendar.HOUR_OF_DAY, hour2)
                targetTime.set(Calendar.MINUTE, minute2)

                //调试用
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                val targetStr = dateFormat.format(targetTime.time)
                val currentStr = dateFormat.format(sleepTime.time)
                println("========================================="+targetStr+"=========================================")
                println("========================================="+currentStr+"=========================================")

                //这里好像没有生效。。
                //计算时间差
                timeDifferenceMillis = Math.abs(targetTime.timeInMillis - sleepTime.timeInMillis)
                println("timeDifferenceMillis: $timeDifferenceMillis")

                //当时间差为0的时候
                if (timeDifferenceMillis == timeZero ) {
                    println("时间差为0的时候")
                    Toast.makeText(this@CountdownReady, "You have selected the present time", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CountdownReady, SleepNow::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                } else {
                    //当时间差不为0的时候
                    println("时间差不为0的时候")
                    val intent = Intent(this, CountdownStart::class.java)
                    intent.putExtra("timeDifferenceMillis", timeDifferenceMillis)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }
            }
        }

        //点击音乐
        musicButton.setOnClickListener {
            val intent = Intent(this, ExoPlayerMusic::class.java)
            startActivity(intent)
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

//    private fun showBottomSheetMenu() {
//        val view = LayoutInflater.from(this).inflate(R.layout.activity_exo_player_music,null)
//        bottomSheetDialog = BottomSheetDialog(this)
//        bottomSheetDialog.setContentView(view)
//        bottomSheetDialog.show()
//    }
}