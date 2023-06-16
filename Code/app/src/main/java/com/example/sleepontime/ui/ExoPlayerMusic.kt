package com.example.sleepontime.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
import com.example.sleepontime.R

class ExoPlayerMusic : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player_music)

        val player = ExoPlayer.Builder(this).build()

//        val mediaItem = MediaItem.fromUri("https://m10.music.126.net/20230607224403/44fb8dfe26d35fe902df72a0afd97f55/yyaac/obj/wonDkMOGw6XDiTHCmMOi/2930742819/a8bb/1e3d/1b4b/b2df584920e7013ce852828d3a2690ed.m4a")

        val mediaItem1 = MediaItem.fromUri("https://m10.music.126.net/20230607224403/44fb8dfe26d35fe902df72a0afd97f55/yyaac/obj/wonDkMOGw6XDiTHCmMOi/2930742819/a8bb/1e3d/1b4b/b2df584920e7013ce852828d3a2690ed.m4a")
//        val mediaItem1 = MediaItem.fromUri("https://m704.music.126.net/20230608001802/54b6f77368e01c8dbf9cb974cf7be49e/jdyyaac/obj/w5rDlsOJwrLDjj7CmsOj/7302698175/1301/e93d/7b1a/0ea2fb6f3c722d9c370d407abbb5af83.m4a?authSecret=00000188968fbdf6128e0aaba0b00a75")
        player.setMediaItem(mediaItem1)
        player.prepare()

        val pianoSongButton = findViewById<Button>(R.id.play_pause_button)
        pianoSongButton.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                pianoSongButton.text = "Piano song 1"
            } else {
                player.play()
                pianoSongButton.text = "Pause"
            }
        }
        player.stop()

        val mediaItem2 = MediaItem.fromUri("https://m804.music.126.net/20230608003705/aac673f1b08965a850d578d4dc779c19/jdyyaac/obj/w5rDlsOJwrLDjj7CmsOj/9641557873/a783/7209/777a/ef7754d7cd1a14259163eb23d07f9432.m4a?authSecret=0000018896a12d9103fd0aaba12c171e")
        player.setMediaItem(mediaItem2)
        player.prepare()

        val secretSongButton = findViewById<Button>(R.id.play_pause_button1)
        secretSongButton.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                secretSongButton.text = "Piano song 2"
            } else {
                player.play()
                secretSongButton.text = "Pause"
            }
        }
    }
}