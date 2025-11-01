package com.example.futurepast

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class LyricsResponse(
    val lyrics: String? = null,
    val error: String? = null
)

class LyricsApiService {

    suspend fun getLyrics(artist: String, title: String): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val encodedArtist = artist.encodeURL()
            val encodedTitle = title.encodeURL()
            val url = URL("https://api.lyrics.ovh/v1/$encodedArtist/$encodedTitle")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val lyricsResponse = Json.decodeFromString<LyricsResponse>(response)
                lyricsResponse.lyrics
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun String.encodeURL(): String {
        return java.net.URLEncoder.encode(this, "UTF-8")
            .replace("+", "%20")
    }
}