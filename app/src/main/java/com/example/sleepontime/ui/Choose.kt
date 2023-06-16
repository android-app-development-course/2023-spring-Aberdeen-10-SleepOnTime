package com.example.sleepontime.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.sleepontime.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Choose : AppCompatActivity() {

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        val userId = intent.getIntExtra("userId", -1)

        var sleepNowButton = findViewById<Button>(R.id.sleepNowButton)
        var sleepLaterButton = findViewById<Button>(R.id.sleepLaterButton)

        //点击SleepNow,跳转
        sleepNowButton.setOnClickListener {
            val intent = Intent(this, SleepNow::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        //点击SleepLater
        sleepLaterButton.setOnClickListener {
            val intent = Intent(this, SleepLater1::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

//        //musictest
//        val musicTest = findViewById<Button>(R.id.musicTest)
//        musicTest.setOnClickListener {
//            val intent = Intent(this, ExoPlayerMusic::class.java)
//            startActivity(intent)
//        }

        // 注册返回键回调函数
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //
            }
        }

        // 将返回键回调函数添加到当前活动
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

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