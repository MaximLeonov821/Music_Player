package com.example.futurepast

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedPlayerViewModel : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private val _currentMusic = MutableLiveData<MusicData?>()
    val currentMusic: LiveData<MusicData?> get() = _currentMusic
    private val  _isRewindRightOrClose = MutableLiveData(true)
    val rewindRightOrClose: LiveData<Boolean> get() = _isRewindRightOrClose

    fun playMusic(context: Context, music: MusicData) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            }

            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(context, music.contentUri)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            _isPlaying.value = true
            _currentMusic.value = music
            _isRewindRightOrClose.value = true

            mediaPlayer!!.setOnCompletionListener {
                _isPlaying.value = false
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun pauseMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
                _isRewindRightOrClose.value = false
            }
        }
    }

    fun resumeMusic() {
        mediaPlayer?.let {
            if (!_isPlaying.value!!) {
                it.start()
                _isPlaying.value = true
                _isRewindRightOrClose.value = true
            }
        }
    }

    fun stopMusic() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _isRewindRightOrClose.value = false
    }

    fun togglePlayPause(context: Context) {
        if (_isPlaying.value == true) {
            pauseMusic()
        } else {
            _currentMusic.value?.let {
                if (mediaPlayer == null) {
                    playMusic(context, it)
                } else {
                    resumeMusic()
                }
            }
        }
    }
    fun getCurrentRewindRightIconRes() : Int {
        return if (_isRewindRightOrClose.value == false) {
            ThemeManager.getCloseIconRes()

        } else{
            ThemeManager.getRewindRightIconRes()
        }
    }
    fun getCurrentPlayPauseIconRes(): Int {
        return if (_isPlaying.value == false) {
            ThemeManager.getPlayIconRes()
        } else {
            ThemeManager.getPauseIconRes()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopMusic()
    }
}