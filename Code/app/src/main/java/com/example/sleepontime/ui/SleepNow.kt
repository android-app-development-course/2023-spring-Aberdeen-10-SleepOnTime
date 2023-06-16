package com.example.sleepontime.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.compose.setContent
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
import com.example.sleepontime.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class SleepNow : AppCompatActivity() {

    private var isPickerVisible by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_now)

        val composeContainer = findViewById<ComposeView>(R.id.composeContainer)
        val userId = intent.getIntExtra("userId", -1)

        composeContainer.setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
//                        Button(onClick = { isPickerVisible = true }) {
//                            Text(text = "Show time picker")
//                        }
                        val showTimePicker = findViewById<Button>(R.id.timepicker_button2)
                        showTimePicker.setOnClickListener {
                            isPickerVisible = true
                        }

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
                                val intent = Intent(this@SleepNow, CountdownReady::class.java)
                                intent.putExtra("hour", hour)
                                intent.putExtra("minute", minute)
                                intent.putExtra("userId", userId)
                                startActivity(intent)
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