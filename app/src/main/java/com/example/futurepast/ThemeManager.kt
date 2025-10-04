package com.example.futurepast

import android.content.Context

object ThemeManager {
    const val THEME_DEFAULT = "default"
    const val THEME_DARK = "dark"
    const val THEME_LIGHT = "light"

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
            THEME_LIGHT -> R.color.BackgroundLight
            else -> R.color.BackgroundRoot
        }
    }

    fun getBottomBarColorRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.color.background_bottom_dark
            THEME_LIGHT -> R.color.BackgroundBottomLight
            else -> R.color.BackgroundBottom
        }
    }

    fun getMainIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_main
            THEME_LIGHT -> R.drawable.ic_main_light
            else -> R.drawable.ic_main
        }
    }

    fun getMusicIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_music
            THEME_LIGHT -> R.drawable.ic_music_light
            else -> R.drawable.ic_music
        }
    }

    fun getHurtOrangeIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_hurt_orange
            THEME_LIGHT -> R.drawable.ic_heart_orange_light
            else -> R.drawable.ic_hurt_orange
        }
    }

    fun getPlayIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_play
            THEME_LIGHT -> R.drawable.ic_play_light
            else -> R.drawable.ic_play
        }
    }

    fun getPauseIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_pause
            THEME_LIGHT -> R.drawable.ic_pause_light
            else -> R.drawable.ic_pause
        }
    }

    fun getRewindBackIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_rewind_back
            THEME_LIGHT -> R.drawable.ic_rewind_back_light
            else -> R.drawable.ic_rewind_back
        }
    }

    fun getRewindRightIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_rewind_right
            THEME_LIGHT -> R.drawable.ic_rewind_right_light
            else -> R.drawable.ic_rewind_right
        }
    }

    fun getRewindRefreshIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_refresh
            THEME_LIGHT -> R.drawable.ic_refresh_light
            else -> R.drawable.ic_refresh
        }
    }


    fun getHeartIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_hurt
            THEME_LIGHT -> R.drawable.ic_heart_light
            else -> R.drawable.ic_hurt
        }
    }

    fun getRewindRefreshIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_refresh
            THEME_LIGHT -> R.drawable.ic_refresh_light
            else -> R.drawable.ic_refresh
        }
    }

    fun getRewindRefreshIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_cover_background
            THEME_LIGHT -> R.drawable.ic_cover_background_light
            else -> R.drawable.ic_cover_background
        }
    }
}