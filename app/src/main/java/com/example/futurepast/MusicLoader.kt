package com.example.futurepast

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicLoader(private val context: Context) {

    suspend fun loadMusicFromDevice(): List<MusicData> = withContext(Dispatchers.IO) {
        val musicList = mutableListOf<MusicData>()
        val contentResolver: ContentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )
        println("Курсор: $cursor")
        println("Количество строк: ${cursor?.count}")

        cursor?.use { c ->
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val authorColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (c.moveToNext()) {
                val id = c.getLong(idColumn)
                val title = c.getString(titleColumn) ?: "Неизвестная музыка"
                val author = c.getString(authorColumn) ?: "Неизвестный автор"
                val duration = c.getLong(durationColumn)
                val path = c.getString(pathColumn)

                musicList.add(
                    MusicData(
                        id = id,
                        title = title,
                        author = author,
                        duration = duration,
                        path = path
                    )
                )
            }
        }
        println("=== ЗАГРУЖЕНО ПЕСЕН: ${musicList.size} ===")

        return@withContext musicList
    }
}