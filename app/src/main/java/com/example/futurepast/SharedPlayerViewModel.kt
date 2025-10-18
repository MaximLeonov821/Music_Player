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
    private val _musicList = MutableLiveData<List<MusicData>>(emptyList())
    val musicList: LiveData<List<MusicData>> get() = _musicList
    private val  _isRewindRightOrClose = MutableLiveData(true)
    val rewindRightOrClose: LiveData<Boolean> get() = _isRewindRightOrClose
    private val _isShuffled = MutableLiveData(false)
    val isShuffled: LiveData<Boolean> get() = _isShuffled
    private var playOrder: MutableList<Int> = mutableListOf()
    private val _favouritesList = MutableLiveData<List<MusicData>>(emptyList())
    val favouritesList: LiveData<List<MusicData>> get() = _favouritesList
    private var isPlayingFromFavourites = false
    private val _isFavouritesShuffled = MutableLiveData(false)
    val isFavouritesShuffled: LiveData<Boolean> get() = _isFavouritesShuffled
    private var favouritesPlayOrder: MutableList<Int> = mutableListOf()
    private var currentIndexInOrder = 0

    fun setMusicList(list: List<MusicData>) {
        _musicList.value = list
        playOrder = list.indices.toMutableList()
        currentIndexInOrder = 0
    }

    fun playMusic(context: Context, music: MusicData, fromFavourites: Boolean = false) {
        isPlayingFromFavourites = fromFavourites
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()

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

            val list = if (fromFavourites) _favouritesList.value ?: emptyList()
            else _musicList.value ?: emptyList()
            val order = if (fromFavourites) favouritesPlayOrder else playOrder

            currentIndexInOrder = order.indexOfFirst { list[it].id == music.id }
            if (currentIndexInOrder == -1) currentIndexInOrder = 0

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
        val list = if (isPlayingFromFavourites) _favouritesList.value ?: return
        else _musicList.value ?: return

        val newState = !(if (isPlayingFromFavourites) _isFavouritesShuffled.value ?: false
        else _isShuffled.value ?: false)

        if (isPlayingFromFavourites) _isFavouritesShuffled.value = newState
        else _isShuffled.value = newState

        val order = list.indices.toMutableList()
        if (newState) order.shuffle()

        if (isPlayingFromFavourites) favouritesPlayOrder = order
        else playOrder = order

        val currentId = _currentMusic.value?.id
        currentIndexInOrder = order.indexOfFirst { list[it].id == currentId }
        if (currentIndexInOrder == -1) currentIndexInOrder = 0
    }

    fun nextMusic(context: Context) {
        if (isPlayingFromFavourites) nextFavouritesMusic(context)
        else nextMusicInList(context)
    }

    fun backMusic(context: Context) {
        if (isPlayingFromFavourites) backFavouritesMusic(context)
        else backMusicInList(context)
    }

    private fun nextMusicInList(context: Context) {
        val list = _musicList.value ?: return
        if (list.isEmpty() || playOrder.isEmpty()) return

        currentIndexInOrder = (currentIndexInOrder + 1) % playOrder.size
        val nextTrack = list[playOrder[currentIndexInOrder]]
        playMusic(context, nextTrack, false)
    }

    private fun backMusicInList(context: Context) {
        val list = _musicList.value ?: return
        if (list.isEmpty() || playOrder.isEmpty()) return

        currentIndexInOrder =
            if (currentIndexInOrder <= 0) playOrder.size - 1 else currentIndexInOrder - 1
        val prevTrack = list[playOrder[currentIndexInOrder]]
        playMusic(context, prevTrack, false)
    }

    private fun nextFavouritesMusic(context: Context) {
        val list = _favouritesList.value ?: return
        if (list.isEmpty() || favouritesPlayOrder.isEmpty()) return

        currentIndexInOrder = (currentIndexInOrder + 1) % favouritesPlayOrder.size
        val nextTrack = list[favouritesPlayOrder[currentIndexInOrder]]
        playMusic(context, nextTrack, true)
    }

    private fun backFavouritesMusic(context: Context) {
        val list = _favouritesList.value ?: return
        if (list.isEmpty() || favouritesPlayOrder.isEmpty()) return

        currentIndexInOrder =
            if (currentIndexInOrder <= 0) favouritesPlayOrder.size - 1 else currentIndexInOrder - 1
        val prevTrack = list[favouritesPlayOrder[currentIndexInOrder]]
        playMusic(context, prevTrack, true)
    }

    fun addToFavourites(context: Context, music: MusicData) {
        val current = _favouritesList.value?.toMutableList() ?: mutableListOf()
        if (!current.any { it.id == music.id }) {
            current.add(music)
            _favouritesList.value = current
            saveFavouritesToPrefs(context)
        }
    }

    fun removeFromFavourites(context: Context, music: MusicData) {
        val current = _favouritesList.value?.toMutableList() ?: mutableListOf()
        current.removeAll { it.id == music.id }
        _favouritesList.value = current
        saveFavouritesToPrefs(context)
        favouritesPlayOrder = current.indices.toMutableList()

        if (_currentMusic.value?.id == music.id) {
            stopMusic()
            _currentMusic.value = null
        }
    }

    fun removeFromMusicListAndUpdate(context: Context, music: MusicData) {
        val current = _musicList.value?.toMutableList() ?: mutableListOf()
        current.removeAll { it.id == music.id }
        _musicList.value = current

        if (_isShuffled.value == true) playOrder =
            current.indices.toMutableList().also { it.shuffle() }
        else playOrder = current.indices.toMutableList()

        if (_currentMusic.value?.id == music.id) {
            stopMusic()
            _currentMusic.value = null
        }

        val favourites = _favouritesList.value?.toMutableList() ?: mutableListOf()
        if (favourites.any { it.id == music.id }) {
            favourites.removeAll { it.id == music.id }
            _favouritesList.value = favourites
            saveFavouritesToPrefs(context)
        }
    }

    private fun saveFavouritesToPrefs(context: Context) {
        val prefs = context.getSharedPreferences("favourites", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val ids = _favouritesList.value?.map { it.id }?.joinToString(",") ?: ""
        editor.putString("favourites_ids", ids)
        editor.apply()
    }

    fun loadFavouritesFromPrefs(context: Context) {
        val prefs = context.getSharedPreferences("favourites", Context.MODE_PRIVATE)
        val savedIds =
            prefs.getString("favourites_ids", "")?.split(",")?.mapNotNull { it.toLongOrNull() }
                ?: emptyList()
        val music = _musicList.value ?: emptyList()
        val favourites = music.filter { savedIds.contains(it.id) }
        _favouritesList.value = favourites

        favouritesPlayOrder = favourites.indices.toMutableList()
        if (_isFavouritesShuffled.value == true) favouritesPlayOrder.shuffle()
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