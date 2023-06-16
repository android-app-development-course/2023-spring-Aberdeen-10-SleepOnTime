package com.example.sleepontime.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.sleepontime.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class MusicSheet : AppCompatActivity() {

    private var musicPlaying = false
    private var playingMusic = ""
    private lateinit var bottomSheetDialog: BottomSheetDialog   //声明变量并延迟初始化，使用lateinit时要确保在使用该属性之前对其进行初始化

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_sheet)

        //初始化bottomSheetDialog
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.activity_music_sheet)

        val music1 = findViewById<Button>(R.id.musicOne)
        val music2 = findViewById<Button>(R.id.musicTwo)
        val close = findViewById<Button>(R.id.close)

        //点击音乐1
        music1.setOnClickListener {
            //检查是否有音乐在播放
            if (musicPlaying) {
                //有音乐正在播放,暂停音乐
                pauseMusic()
                println("pause music1")
            }

            println("no music now")
            //音乐开始播放
            playMusic()
            playingMusic = "Music1"
        }

        //点击音乐2
        music2.setOnClickListener {
            //检查是否有音乐在播放
            if (musicPlaying) {
                //有音乐正在播放,暂停音乐
                pauseMusic()
                println("pause music2")
            }

            println("no music now")
            //音乐开始播放
            playMusic()
            playingMusic = "Music2"
        }

        //点击关闭
        close.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    //播放音乐的函数
    private fun playMusic() {
        println("play music")
        musicPlaying = true
        Toast.makeText(this, "playing music", Toast.LENGTH_SHORT).show()
        Log.d("MusicSheet", "playmusic")
    }

    //暂停音乐的函数
    private fun pauseMusic() {
        println("pause music")
        musicPlaying = false
        Toast.makeText(this, "stop music", Toast.LENGTH_SHORT).show()
        Log.d("MusicSheet", "pause music")
    }
}