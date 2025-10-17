package com.example.futurepast

import android.content.Context
import android.media.MediaPlayer
import android.text.BoringLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedPlayerViewModel : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private val _currentMusic = MutableLiveData<MusicData?>()
    val currentMusic: LiveData<MusicData?> get() = _currentMusic
    private val _musicList = MutableLiveData<List<MusicData>>(emptyList())
    val musicList: LiveData<List<MusicData>> get() = _musicList
    private val  _isRewindRightOrClose = MutableLiveData(true)
    val rewindRightOrClose: LiveData<Boolean> get() = _isRewindRightOrClose
    private val _isShuffled = MutableLiveData(false)
    val isShuffled: LiveData<Boolean> get() = _isShuffled
    private var playOrder: MutableList<Int> = mutableListOf()

    fun setMusicList(list: List<MusicData>) {
        _musicList.value = list
        playOrder = list.indices.toMutableList()
    }

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
                _isRewindRightOrClose.value = false
                nextMusic(context)
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

    fun toggleShuffle() {
        val list = _musicList.value ?: return
        val newState = !(_isShuffled.value ?: false)
        _isShuffled.value = newState

        if (newState) {
            playOrder.shuffle()
        } else {
            playOrder = list.indices.toMutableList()
        }
    }


    fun nextMusic(context: Context) {
        val currentMusic = _currentMusic.value
        val list = _musicList.value ?: emptyList()

        if (currentMusic != null && list.isNotEmpty() && playOrder.isNotEmpty()) {
            val currentIndex = list.indexOfFirst { it.id == currentMusic.id }
            val logicalIndex = playOrder.indexOf(currentIndex)
            if (logicalIndex != -1) {
                val nextIndex = (logicalIndex + 1) % playOrder.size
                val nextMusic = playOrder[nextIndex]
                playMusic(context, list[nextMusic])
            }
        }
    }

    fun backMusic(context: Context) {
        val currentMusic = _currentMusic.value
        val list = _musicList.value ?: emptyList()

        if (currentMusic != null && list.isNotEmpty() && playOrder.isNotEmpty()) {
            val currentIndex = list.indexOfFirst { it.id == currentMusic.id }
            val logicalIndex = playOrder.indexOf(currentIndex)
            if (logicalIndex != -1) {
                val prevLogical = if (logicalIndex == 0) playOrder.size - 1 else logicalIndex - 1
                val prevIndex = playOrder[prevLogical]
                playMusic(context, list[prevIndex])
            }
        }
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

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun getPlayerInstance(): MediaPlayer? {
        return mediaPlayer
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
    }

}