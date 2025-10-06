package com.example.futurepast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedPlayerViewModel : ViewModel() {
    private val _isPlaying = MutableLiveData(true)

    val isPlaying: LiveData<Boolean> get() = _isPlaying

    fun togglePlayPause() {
        _isPlaying.value = !(_isPlaying.value ?: true)
    }

    fun getCurrentIconRes(): Int {
        return if (_isPlaying.value == true) ThemeManager.getPlayIconRes()
        else ThemeManager.getPauseIconRes()
    }
}