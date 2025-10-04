package com.example.futurepast

import android.content.Context
import android.content.SharedPreferences

object ThemeManager {
    const val THEME_DEFAULT = "default"
    const val THEME_DARK = "dark"
    const val THEME_BLUE = "blue"

    private var currentTheme = THEME_DEFAULT

    fun setTheme(theme: String, context: Context) {
        currentTheme = theme
        val prefs = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
        prefs.edit().putString("current_theme", theme).apply()
    }

    fun loadTheme(context: Context) {
        val prefs = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
        currentTheme = prefs.getString("current_theme", THEME_DEFAULT) ?: THEME_DEFAULT
    }

    fun getCurrentTheme(): String {
        return currentTheme
    }

    fun getBackgroundColorRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.color.background_dark
            THEME_BLUE -> R.color.background_blue
            else -> R.color.BackgroundRoot
        }
    }

    fun getBottomBarColorRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.color.background_bottom_dark
            THEME_BLUE -> R.color.background_bottom_blue
            else -> R.color.BackgroundBottom
        }
    }

    fun getBrushIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_brush
            THEME_BLUE -> R.drawable.ic_brush
            else -> R.drawable.ic_brush
        }
    }

    fun getMainIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_main
            THEME_BLUE -> R.drawable.ic_main
            else -> R.drawable.ic_main
        }
    }

    fun getMusicIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_music
            THEME_BLUE -> R.drawable.ic_music
            else -> R.drawable.ic_music
        }
    }

    fun getHurtOrangeIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_hurt_orange
            THEME_BLUE -> R.drawable.ic_hurt_orange
            else -> R.drawable.ic_hurt_orange
        }
    }

    fun getPlayIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_play
            THEME_BLUE -> R.drawable.ic_play
            else -> R.drawable.ic_play
        }
    }

    fun getPauseIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_pause
            THEME_BLUE -> R.drawable.ic_pause
            else -> R.drawable.ic_pause
        }
    }
}