package com.example.futurepast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurepast.databinding.ActivityPlayerBinding

class MainActivity : AppCompatActivity() {
    private var _playerActivity: ActivityPlayerBinding? = null
    private val playerActivity get() = _playerActivity!!

    private  var isPlaying = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _playerActivity = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(playerActivity.root)

        playerActivity.PlayPauseSwitcher.setOnClickListener {
            toggleSwitcher()
        }
    }
    private fun toggleSwitcher() {
        isPlaying = !isPlaying
        if (!isPlaying) {
            playerActivity.PlayPauseSwitcher.setImageResource(R.drawable.ic_pause)
        } else {
            playerActivity.PlayPauseSwitcher.setImageResource(R.drawable.ic_play)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _playerActivity = null
    }

}