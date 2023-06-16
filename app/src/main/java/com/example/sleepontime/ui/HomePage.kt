package com.example.sleepontime.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sleepontime.R
import com.example.sleepontime.data.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)


        val userId = intent.getIntExtra("userId", -1)
        dbHelper = DatabaseHelper(this@HomePage)

        val (username, available) = dbHelper.getUsernameAndAvailableById(userId)
        println("Available: $available")
        println("Username: $username")


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