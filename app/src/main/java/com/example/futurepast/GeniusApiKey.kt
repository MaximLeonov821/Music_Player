package com.example.futurepast

import io.github.cdimascio.dotenv.dotenv

object GeniusApiKey {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    val key: String = dotenv["GENIUS_API"].also { token ->
        println("🔑 Genius API Token: ${if (token.isNullOrEmpty()) "NULL или ПУСТОЙ" else "ЗАГРУЖЕН (${token.length} символов)"}")
        if (!token.isNullOrEmpty()) {
            println("🔑 Первые 10 символов: ${token.take(10)}...")
        }
    } ?: "fallback_key"
}