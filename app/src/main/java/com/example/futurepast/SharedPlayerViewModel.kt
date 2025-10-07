package com.example.futurepast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedPlayerViewModel : ViewModel() {
    private val _isPlaying = MutableLiveData(true)
    private val _currentMusic = MutableLiveData<MusicData?>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    fun togglePlayPause() {
        _isPlaying.value = !(_isPlaying.value ?: true)
    }

    fun getCurrentIconRes(): Int {
        return if (_isPlaying.value == true) ThemeManager.getPlayIconRes()
        else ThemeManager.getPauseIconRes()
    }
    fun playMusic(music: MusicData) {
        _currentMusic.value = music
        _isPlaying.value = true
    }

}