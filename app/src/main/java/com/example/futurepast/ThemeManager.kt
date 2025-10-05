package com.example.futurepast

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


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
            THEME_DARK -> R.color.BackgroundDark
            THEME_LIGHT -> R.color.BackgroundLight
            else -> R.color.BackgroundRoot
        }
    }

    fun getTextsColorRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.color.TextsDark
            THEME_LIGHT -> R.color.TextsLight
            else -> R.color.white
        }
    }

    fun getBottomBarColorRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.color.BackgroundBottomDark
            THEME_LIGHT -> R.color.BackgroundBottomLight
            else -> R.color.BackgroundBottom
        }
    }

    fun getMainIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_main_dark
            THEME_LIGHT -> R.drawable.ic_main_light
            else -> R.drawable.ic_main
        }
    }

    fun getMusicIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_music_dark
            THEME_LIGHT -> R.drawable.ic_music_light
            else -> R.drawable.ic_music
        }
    }

    fun getHeartOrangeIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_heart_orange_dark
            THEME_LIGHT -> R.drawable.ic_heart_orange_light
            else -> R.drawable.ic_heart_orange
        }
    }

    fun getSeekBarProgressColorRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.drawable.music_seekbar_progress_dark
            THEME_LIGHT -> R.drawable.music_seekbar_progress_light
            else -> R.drawable.music_seekbar_progress
        }
    }

    fun getSeekBarThumbColorRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.drawable.music_seekbar_thumb_dark
            THEME_LIGHT -> R.drawable.music_seekbar_thumb_light
            else -> R.drawable.music_seekbar_thumb
        }
    }

    fun getPlayIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_play_dark
            THEME_LIGHT -> R.drawable.ic_play_light
            else -> R.drawable.ic_play
        }
    }

    fun getRefreshIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_refresh_dark
            THEME_LIGHT -> R.drawable.ic_refresh_light
            else -> R.drawable.ic_refresh
        }
    }

    fun getRewindBackIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_rewind_back_dark
            THEME_LIGHT -> R.drawable.ic_rewind_back_light
            else -> R.drawable.ic_rewind_back
        }
    }

    fun getPauseIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_pause_dark
            THEME_LIGHT -> R.drawable.ic_pause_light
            else -> R.drawable.ic_pause
        }
    }

    fun getRewindRightIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_rewind_right_dark
            THEME_LIGHT -> R.drawable.ic_rewind_right_light
            else -> R.drawable.ic_rewind_right
        }
    }

    fun getHeartIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_heart_dark
            THEME_LIGHT -> R.drawable.ic_heart_light
            else -> R.drawable.ic_heart
        }
    }

    fun getCoverBackgroundIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_cover_background_dark
            THEME_LIGHT -> R.drawable.ic_cover_background_light
            else -> R.drawable.ic_cover_background
        }
    }

     fun applyToAllTextViews(rootView: View, action: (TextView) -> Unit) {
        if (rootView is ViewGroup) {
            for (i in 0 until rootView.childCount) {
                val child = rootView.getChildAt(i)
                if (child is TextView) {
                    action(child)
                } else if (child is ViewGroup) {
                    applyToAllTextViews(child, action)
                }
            }
        } else if (rootView is TextView) {
            action(rootView)
        }
    }
}