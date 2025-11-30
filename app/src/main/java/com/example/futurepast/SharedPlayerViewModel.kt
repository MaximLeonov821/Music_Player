package com.example.futurepast

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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

    private val _isFavouritesAdd = MutableLiveData(false)
    val isFavouritesAdd: LiveData<Boolean> get() = _isFavouritesAdd
    private var isPlayingFromFavourites = false
    private val _isFavouritesShuffled = MutableLiveData(false)
    val isFavouritesShuffled: LiveData<Boolean> get() = _isFavouritesShuffled
    private var favouritesPlayOrder: MutableList<Int> = mutableListOf()
    private var currentIndexInOrder = 0
    private val geniusApiService = GeniusApiService(GeniusApiKey.key)
    private val _currentLyrics = MutableLiveData<String?>()
    val currentLyrics: LiveData<String?> get() = _currentLyrics
    private var lyricsLoadingJob: kotlinx.coroutines.Job? = null
    private val _currentPlaySource = MutableLiveData<String>("Главная")
    val currentPlaySource: LiveData<String> get() = _currentPlaySource

    fun loadLyricsForCurrentTrack(context: Context) {
        val currentMusic = _currentMusic.value ?: return

        lyricsLoadingJob?.cancel()
        lyricsLoadingJob = viewModelScope.launch {
            println("🚀 Начинаем загрузку текста для текущего трека...")
            _currentLyrics.postValue("🔍 Ищем текст песни...")

            try {
                val mmr = android.media.MediaMetadataRetriever()
                try {
                    mmr.setDataSource(context, currentMusic.contentUri)
                    var artist = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
                    var title = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""

                    artist = cleanMetadata(artist)
                    title = cleanMetadata(title)

                    println("🎵 Очищенные метаданные:")
                    println("   👨‍🎤 Artist: '$artist'")
                    println("   🎵 Title: '$title'")
                    println("   🆔 Music ID: ${currentMusic.id}")

                    if (!isActive) {
                        println("⏹️ Коррутина неактивна, прерываем загрузку")
                        return@launch
                    }

                    println("🌐 Запрашиваем текст через Genius API...")
                    val lyrics = try {
                        geniusApiService.getLyrics(artist, title)
                    } catch (e: Exception) {
                        println("❌ Исключение при получении текста: ${e.message}")
                        null
                    }

                    if (!isActive) {
                        println("⏹️ Коррутина неактивна после получения текста")
                        return@launch
                    }

                    val finalLyrics = if (lyrics != null && lyrics.length > 10) {
                        lyrics
                    } else {
                        "😔 Текст песни не найден\n\nGenius лучше работает с английскими песнями. " +
                                "Для русских и других не-английских треков текст может быть недоступен."
                    }

                    println("🎯 Результат получения текста: ${if (lyrics != null) "УСПЕХ" else "НЕ НАЙДЕНО"}")
                    _currentLyrics.postValue(finalLyrics)

                } catch (e: Exception) {
                    if (e is kotlinx.coroutines.CancellationException) {
                        println("⏹️ Загрузка текста отменена для трека: ${currentMusic.title}")
                        return@launch
                    }
                    println("❌ Ошибка при извлечении метаданных: ${e.message}")
                    _currentLyrics.postValue("Не удалось извлечь метаданные")
                } finally {
                    mmr.release()
                    println("🔚 Завершена загрузка текста для трека: ${currentMusic.title}")
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                println("⏹️ Коррутина загрузки текста отменена")
            } catch (e: Exception) {
                println("❌ Неожиданная ошибка: ${e.message}")
                _currentLyrics.postValue("Ошибка загрузки текста")
            }
        }
    }

    private fun cleanMetadata(text: String): String {
        var cleaned = text.trim()
        cleaned = cleaned.replace(Regex("\\.(mp3|m4a|flac|wav)$", RegexOption.IGNORE_CASE), "")
        cleaned = cleaned.replace(Regex("\\s+"), " ")
        return cleaned
    }


    fun clearLyrics() {
        lyricsLoadingJob?.cancel()
    }
    fun setMusicList(list: List<MusicData>) {
        _musicList.value = list
        playOrder = list.indices.toMutableList()
        currentIndexInOrder = 0
    }

    fun playMusic(context: Context, music: MusicData, fromFavourites: Boolean = false) {
        isPlayingFromFavourites = fromFavourites
        _currentPlaySource.value = if (fromFavourites) "Избранное" else "Главная"
        try {
            clearLyrics()
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()

            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(context, music.contentUri)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            _isPlaying.value = true
            _currentMusic.value = music
            _isRewindRightOrClose.value = true
            val isInFavourites = _favouritesList.value?.any { it.id == music.id } ?: false
            _isFavouritesAdd.value = isInFavourites

            if (fromFavourites) {
                val favourites = _favouritesList.value ?: emptyList()
                val indexInFavourites = favourites.indexOfFirst { it.id == music.id }
                if (indexInFavourites != -1) {
                    currentIndexInOrder = favouritesPlayOrder.indexOf(indexInFavourites)
                    if (currentIndexInOrder == -1) currentIndexInOrder = 0
                }
            } else {
                val list = _musicList.value ?: emptyList()
                val indexInList = list.indexOfFirst { it.id == music.id }
                if (indexInList != -1) {
                    currentIndexInOrder = playOrder.indexOf(indexInList)
                    if (currentIndexInOrder == -1) currentIndexInOrder = 0
                }
            }

            loadLyricsForCurrentTrack(context)

            mediaPlayer!!.setOnCompletionListener {
                _isPlaying.value = false
                _isRewindRightOrClose.value = false
                nextMusic(context)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            clearLyrics()
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
        clearLyrics()
    }

    fun toggleShuffleForAll() {
        val newState = !(_isShuffled.value ?: false)
        _isShuffled.value = newState
        _isFavouritesShuffled.value = newState

        playOrder = (_musicList.value?.indices?.toMutableList() ?: mutableListOf()).apply { if (newState) shuffle() }
        updateFavouritesPlayOrder()
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

    fun updateFavouritesPlayOrder() {
        val favourites = _favouritesList.value ?: emptyList()
        favouritesPlayOrder = favourites.indices.toMutableList()
        if (_isFavouritesShuffled.value == true) {
            favouritesPlayOrder.shuffle()
        }

        _currentMusic.value?.let { currentMusic ->
            val indexInFavourites = favourites.indexOfFirst { it.id == currentMusic.id }
            if (indexInFavourites != -1) {
                currentIndexInOrder = favouritesPlayOrder.indexOf(indexInFavourites)
                if (currentIndexInOrder == -1) currentIndexInOrder = 0
            }
        }
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
            _isFavouritesAdd.value = true
            saveFavouritesToPrefs(context)
            updateFavouritesPlayOrder()
        }
    }

    fun removeFromFavourites(context: Context, music: MusicData) {
        val current = _favouritesList.value?.toMutableList() ?: mutableListOf()
        current.removeAll { it.id == music.id }
        _favouritesList.value = current
        _isFavouritesAdd.value = false
        saveFavouritesToPrefs(context)
        updateFavouritesPlayOrder()

        if (_currentMusic.value?.id == music.id && isPlayingFromFavourites) {
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
        val savedIds = prefs.getString("favourites_ids", "")?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
        val music = _musicList.value ?: emptyList()
        val favourites = music.filter { savedIds.contains(it.id) }
        _favouritesList.value = favourites

        updateFavouritesPlayOrder()

        _currentMusic.value?.let { currentMusic ->
            val isInFavourites = favourites.any { it.id == currentMusic.id }
            _isFavouritesAdd.value = isInFavourites
        }
    }

    fun getCurrentHeartIconRes(): Int {
        return if (_isFavouritesAdd.value == true) {
            ThemeManager.getHeartRedIconRes()
        } else {
            ThemeManager.getHeartIconRes()
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
        lyricsLoadingJob?.cancel()
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