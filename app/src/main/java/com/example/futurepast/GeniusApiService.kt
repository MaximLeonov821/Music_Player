package com.example.futurepast

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class GeniusSearchResponse(
    val response: GeniusResponse? = null
)

@Serializable
data class GeniusResponse(
    val hits: List<GeniusHit>? = emptyList()
)

@Serializable
data class GeniusHit(
    val result: GeniusResult? = null
)

@Serializable
data class GeniusResult(
    val id: Int? = null,
    val title: String? = null,
    val url: String? = null,
    val primary_artist: GeniusArtist? = null
)

@Serializable
data class GeniusArtist(
    val name: String? = null
)

class GeniusApiService {

    suspend fun getLyrics(artist: String, title: String): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val searchUrl = "https://api.genius.com/search?q=${artist.encodeURL()} ${title.encodeURL()}"
            println("DEBUG: Searching Genius: $searchUrl")

            val searchResponse = makeGeniusRequest(searchUrl)
            val songId = searchResponse?.response?.hits?.firstOrNull()?.result?.id

            if (songId != null) {
                parseGeniusLyrics(songId)
            } else {
                null
            }
        } catch (e: Exception) {
            println("DEBUG: Genius API error: ${e.message}")
            null
        }
    }

    private fun makeGeniusRequest(urlString: String): GeniusSearchResponse? {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.setRequestProperty("Accept", "application/json")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Json.decodeFromString<GeniusSearchResponse>(response)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseGeniusLyrics(songId: Int): String? {
        return try {
            "Текст доступен по ссылке: https://genius.com/songs/$songId\n\nДля полной интеграции нужен парсинг HTML страницы"
        } catch (e: Exception) {
            null
        }
    }

    private fun String.encodeURL(): String {
        return java.net.URLEncoder.encode(this, "UTF-8")
            .replace("+", "%20")
    }
}