package com.example.futurepast

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class GeniusSearchResponse(val response: GeniusResponse? = null)

@Serializable
data class GeniusResponse(val hits: List<GeniusHit>? = emptyList())

@Serializable
data class GeniusHit(val result: GeniusResult? = null)

@Serializable
data class GeniusResult(val id: Int? = null, val url: String? = null)

class GeniusApiService(private val apiKey: String) {
    suspend fun getLyrics(artist: String, title: String): String? = withContext(Dispatchers.IO) {
        println("🎵 Поиск текста для: '$artist' - '$title'")

        try {
            val query = "${artist.trim()} ${title.trim()}"
            println("🔍 Поисковый запрос: '$query'")

            val searchUrl = "https://api.genius.com/search?q=${query.encodeURL()}"
            println("🌐 Search URL: $searchUrl")

            val searchResponse = apiRequest(searchUrl)
            println("📡 Search Response: ${searchResponse != null}")

            val url = searchResponse?.response?.hits?.firstOrNull()?.result?.url
            println("🔗 Найден URL: $url")

            return@withContext url?.let {
                println("📝 Парсим текст с URL...")
                parseLyrics(it)
            }
        } catch (e: Exception) {
            println("❌ Ошибка в getLyrics: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    private fun apiRequest(urlString: String): GeniusSearchResponse? {
        return try {
            println("🔄 API Request to: $urlString")
            val url = URL(urlString)
            val c = url.openConnection() as HttpURLConnection
            c.requestMethod = "GET"
            c.setRequestProperty("Authorization", "Bearer $apiKey")
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            c.setRequestProperty("Accept", "application/json")

            c.connectTimeout = 15000
            c.readTimeout = 15000

            println("📞 Код ответа: ${c.responseCode}")

            if (c.responseCode == 200) {
                val json = c.inputStream.bufferedReader().readText()
                println("✅ Успешный ответ, длина JSON: ${json.length}")

                val result = Json { ignoreUnknownKeys = true }.decodeFromString<GeniusSearchResponse>(json)
                println("📡 Search Response: ${result != null}")
                println("🎯 Hits count: ${result?.response?.hits?.size ?: 0}")

                return result
            } else {
                println("❌ Ошибка HTTP: ${c.responseCode} - ${c.responseMessage}")
                null
            }
        } catch (e: Exception) {
            println("❌ Ошибка в apiRequest: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    private fun parseLyrics(url: String): String? {
        return try {
            println("🎯 Парсим lyrics с: $url")
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(20000)
                .get()

            val structuredText = tryGetStructuredLyrics(doc)
            if (structuredText != null) {
                println("✅ СТРУКТУРИРОВАННЫЙ ТЕКСТ: ${structuredText.length} символов")
                return structuredText
            }

            println("⚠️ Используем fallback метод")
            return parseLyricsFallback(doc)

        } catch (e: Exception) {
            println("❌ Ошибка в parseLyrics: ${e.message}")
            null
        }
    }

    private fun tryGetStructuredLyrics(doc: org.jsoup.nodes.Document): String? {
        return try {
            val lyricsContainers = doc.select("div[data-lyrics-container=true]")
            if (lyricsContainers.isEmpty()) return null

            println("✅ Найдено контейнеров: ${lyricsContainers.size}")

            val lyricsBuilder = StringBuilder()

            for (container in lyricsContainers) {
                val text = extractTextWithLineBreaks(container)
                lyricsBuilder.append(text).append("\n\n")
            }

            var result = lyricsBuilder.toString().trim()

            result = cleanWithOriginalLogic(result)

            result = enhanceStructure(result)

            println("📄 ПРЕВЬЮ ТЕКСТА:\n${result.take(500)}...")
            result

        } catch (e: Exception) {
            println("❌ Ошибка в tryGetStructuredLyrics: ${e.message}")
            null
        }
    }

    private fun extractTextWithLineBreaks(element: org.jsoup.nodes.Element): String {
        val html = element.html()

        var text = html.replace(Regex("""<br\s*/?>"""), "\n")

        text = text.replace(Regex("""<[^>]+>"""), "")

        text = text.replace(Regex(" +"), " ")
        text = text.replace(Regex(" *\n *"), "\n")

        return text.trim()
    }

    private fun parseLyricsFallback(doc: org.jsoup.nodes.Document): String? {
        val allText = doc.text()
        println("📄 Вся страница: ${allText.length} символов")

        val startIndices = listOf(
            allText.indexOf("[Verse"), allText.indexOf("[Chorus"),
            allText.indexOf("[Intro"), allText.indexOf("[Hook"),
            allText.indexOf("[Outro"), allText.indexOf("[Bridge")
        ).filter { it != -1 }

        if (startIndices.isEmpty()) return null

        val songStart = startIndices.min()
        println("🎵 Начало песни найдено на позиции: $songStart")

        var songText = allText.substring(songStart)

        if (songText.contains("You might also like")) {
            val parts = songText.split("You might also like")
            if (parts.size > 1) {
                val beforeAd = parts[0]
                val afterAd = parts[1]

                val nextTagIndex = afterAd.indexOf("[")
                if (nextTagIndex != -1) {
                    songText = beforeAd + afterAd.substring(nextTagIndex)
                    println("🔪 Вырезана реклама 'You might also like'")
                } else {
                    songText = beforeAd
                }
            }
        }

        val endMarkers = listOf(
            "Contributors",
            "283Embed",
            "How to Format Lyrics",
            "About Song Bio",
            "Expand",
            "Genius Answer",
            "Ask a question"
        )

        var endPosition = songText.length
        for (marker in endMarkers) {
            val markerIndex = songText.indexOf(marker)
            if (markerIndex != -1) {
                endPosition = minOf(endPosition, markerIndex)
                println("🔪 Обрезаем КОНЕЦ по маркеру: '$marker'")
                break
            }
        }

        songText = songText.substring(0, endPosition).trim()

        songText = enhanceStructure(songText)

        println("✅ ЧИСТЫЙ ТЕКСТ ПЕСНИ: ${songText.length} символов")
        println("📄 ТЕКСТ ПЕСНИ:\n$songText")
        return songText
    }

    private fun cleanWithOriginalLogic(text: String): String {
        var cleaned = text

        val startIndices = listOf(
            cleaned.indexOf("[Verse"), cleaned.indexOf("[Chorus"),
            cleaned.indexOf("[Intro"), cleaned.indexOf("[Hook"),
            cleaned.indexOf("[Outro"), cleaned.indexOf("[Bridge")
        ).filter { it != -1 }

        if (startIndices.isNotEmpty()) {
            val songStart = startIndices.min()
            cleaned = cleaned.substring(songStart)
            println("🔪 Обрезано всё до начала песни")
        }

        if (cleaned.contains("You might also like")) {
            val parts = cleaned.split("You might also like")
            if (parts.size > 1) {
                val beforeAd = parts[0]
                val afterAd = parts[1]

                val nextTagIndex = afterAd.indexOf("[")
                if (nextTagIndex != -1) {
                    cleaned = beforeAd + afterAd.substring(nextTagIndex)
                    println("🔪 Вырезана реклама 'You might also like'")
                } else {
                    cleaned = beforeAd
                }
            }
        }

        val endMarkers = listOf(
            "Contributors",
            "283Embed",
            "How to Format Lyrics",
            "About Song Bio",
            "Expand",
            "Genius Answer",
            "Ask a question"
        )

        var endPosition = cleaned.length
        for (marker in endMarkers) {
            val markerIndex = cleaned.indexOf(marker)
            if (markerIndex != -1) {
                endPosition = minOf(endPosition, markerIndex)
                println("🔪 Обрезаем КОНЕЦ по маркеру: '$marker'")
                break
            }
        }

        cleaned = cleaned.substring(0, endPosition).trim()
        return cleaned
    }

    private fun enhanceStructure(text: String): String {
        var result = text

        result = result.replace(Regex("\\[Intro\\]"), "━━━━━━━━━━━━━━━━━━━━\n🎤 INTRO\n━━━━━━━━━━━━━━━━━━━━")
        result = result.replace(Regex("\\[Verse\\s*\\d*\\]"), "────────────────────\n🎵 VERSE\n────────────────────")
        result = result.replace(Regex("\\[Chorus\\]"), "════════════════════\n🎶 CHORUS\n════════════════════")
        result = result.replace(Regex("\\[Hook\\]"), "────────────────────\n🪝 HOOK\n────────────────────")
        result = result.replace(Regex("\\[Bridge\\]"), "────────────────────\n🌉 BRIDGE\n────────────────────")
        result = result.replace(Regex("\\[Outro\\]"), "━━━━━━━━━━━━━━━━━━━━\n👋 OUTRO\n━━━━━━━━━━━━━━━━━━━━")

        result = result.trim()
        result = result.replace(Regex("\n{3,}"), "\n\n")

        return result
    }

    private fun String.encodeURL(): String =
        java.net.URLEncoder.encode(this, "UTF-8").replace("+", "%20")
}