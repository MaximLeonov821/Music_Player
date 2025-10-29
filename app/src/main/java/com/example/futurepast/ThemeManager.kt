package com.example.futurepast

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


object ThemeManager {
    const val THEME_DEFAULT = "default"
    const val THEME_DARK = "dark"
    const val THEME_LIGHT = "light"
    const val THEME_BLACK_AND_GREEN = "blackAndGreen"
    const val THEME_BROWN_AND_PINK = "brownAndPink"
    const val THEME_DARK_INDIGO = "darkIndigo"

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
            THEME_BLACK_AND_GREEN -> R.color.BackgroundBlackAndGreen
            THEME_BROWN_AND_PINK -> R.color.BackgroundBrownAndPink
            THEME_DARK_INDIGO -> R.color.BackgroundDarkIndigo
            else -> R.color.BackgroundRoot
        }
    }

    fun getBackgroundMusicBoxColorRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.color.BackgroundMusicDark
            THEME_LIGHT -> R.color.BackgroundMusicLight
            THEME_BLACK_AND_GREEN -> R.color.BackgroundMusicBlackAndGreen
            THEME_BROWN_AND_PINK -> R.color.BackgroundMusicBrownAndPink
            THEME_DARK_INDIGO -> R.color.BackgroundMusicDarkIndigo
            else -> R.color.BackgroundMusic
        }
    }
    fun getBackgroundPopUpPanelColorRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.color.BackgroundPopUpPanelDark
            THEME_LIGHT -> R.color.BackgroundPopUpPanelLight
            THEME_BLACK_AND_GREEN -> R.color.BackgroundPopUpPanelAndBottomBlackAndGreen
            THEME_BROWN_AND_PINK -> R.color.BackgroundPopUpPanelAndBottomBrownAndPink
            THEME_DARK_INDIGO -> R.color.BackgroundPopUpPanelDarkIndigo
            else -> R.color.BackgroundBottom
        }
    }
    fun getBackgroundAlpha(): Float{
        return when (currentTheme){
            THEME_DARK -> 1f
            THEME_LIGHT -> 1f
            THEME_BLACK_AND_GREEN -> 1f
            THEME_BROWN_AND_PINK -> 1f
            THEME_DARK_INDIGO -> 1f
            else -> 0.74f
        }
    }

    fun getBackgroundLineViewColorRes(): Int{
        return when(currentTheme){
            THEME_DARK -> R.color.white
            THEME_LIGHT -> R.color.BackgroundPanelLineViewLight
            THEME_BLACK_AND_GREEN -> R.color.white
            THEME_BROWN_AND_PINK -> R.color.white
            THEME_DARK_INDIGO -> R.color.white
            else -> R.color.white
        }
    }

    fun getTextsColorRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.color.TextsDark
            THEME_LIGHT -> R.color.TextsLight
            THEME_BLACK_AND_GREEN -> R.color.TextsBlackAndGreen
            THEME_BROWN_AND_PINK -> R.color.TextsBrownAndPink
            THEME_DARK_INDIGO -> R.color.TextsDarkIndigo
            else -> R.color.white
        }
    }

    fun getMenuIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_menu_player
            THEME_LIGHT -> R.drawable.ic_menu_player_dark
            THEME_BLACK_AND_GREEN -> R.drawable.ic_menu_player
            THEME_BROWN_AND_PINK -> R.drawable.ic_menu_player
            THEME_DARK_INDIGO -> R.drawable.ic_menu_player
            else -> R.drawable.ic_menu_player
        }
    }

    fun getBrushIconRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.drawable.ic_brush_dark
            THEME_LIGHT -> R.drawable.ic_brush_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_brush_black_and_green
            THEME_BROWN_AND_PINK -> R.drawable.ic_brush_brown_and_pink
            THEME_DARK_INDIGO -> R.drawable.ic_brush_dark_indigo
            else -> R.drawable.ic_brush
        }
    }

    fun getBottomBarColorRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.color.BackgroundBottomDark
            THEME_LIGHT -> R.color.BackgroundBottomLight
            THEME_BLACK_AND_GREEN -> R.color.BackgroundPopUpPanelAndBottomBlackAndGreen
            THEME_BROWN_AND_PINK -> R.color.BackgroundPopUpPanelAndBottomBrownAndPink
            THEME_DARK_INDIGO -> R.color.BackgroundBottomDarkIndigo
            else -> R.color.BackgroundBottom
        }
    }

    fun getMainIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_main_dark
            THEME_LIGHT -> R.drawable.ic_main_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_main_black_and_green
            THEME_BROWN_AND_PINK -> R.drawable.ic_main_brown_and_pink
            THEME_DARK_INDIGO -> R.drawable.ic_main_dark_indigo
            else -> R.drawable.ic_main
        }
    }

    fun getMusicIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_music_dark
            THEME_LIGHT -> R.drawable.ic_music_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_music_black_and_green
            THEME_BROWN_AND_PINK -> R.drawable.ic_music_brown_and_pink
            THEME_DARK_INDIGO -> R.drawable.ic_music_dark_indigo
            else -> R.drawable.ic_music
        }
    }

    fun getHeartOrangeIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_heart_orange_dark
            THEME_LIGHT -> R.drawable.ic_heart_orange_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_heart_orange_black_and_green
            THEME_BROWN_AND_PINK -> R.drawable.ic_heart_orange_brown_and_pink
            THEME_DARK_INDIGO -> R.drawable.ic_heart_orange_dark_indigo
            else -> R.drawable.ic_heart_orange
        }
    }

    fun getSeekBarProgressColorRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.drawable.music_seekbar_progress_dark
            THEME_LIGHT -> R.drawable.music_seekbar_progress_light
            THEME_BLACK_AND_GREEN -> R.drawable.music_seekbar_progress_black_and_green
            THEME_BROWN_AND_PINK -> R.drawable.music_seekbar_progress_brown_and_pink
            THEME_DARK_INDIGO -> R.drawable.music_seekbar_progress_dark_indigo
            else -> R.drawable.music_seekbar_progress
        }
    }

    fun getSeekBarThumbColorRes(): Int{
        return when (currentTheme){
            THEME_DARK -> R.drawable.music_seekbar_thumb_dark
            THEME_LIGHT -> R.drawable.music_seekbar_thumb_light
            THEME_BLACK_AND_GREEN -> R.drawable.music_seekbar_thumb_black_and_green
            THEME_BROWN_AND_PINK -> R.drawable.music_seekbar_thumb_brown_and_pink
            THEME_DARK_INDIGO -> R.drawable.music_seekbar_thumb_dark_indigo
            else -> R.drawable.music_seekbar_thumb
        }
    }

    fun getPlayIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_play_dark
            THEME_LIGHT -> R.drawable.ic_play_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_play
            THEME_BROWN_AND_PINK -> R.drawable.ic_play
            THEME_DARK_INDIGO -> R.drawable.ic_play
            else -> R.drawable.ic_play
        }
    }

    fun getRefreshIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_refresh_dark
            THEME_LIGHT -> R.drawable.ic_refresh_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_refresh
            THEME_BROWN_AND_PINK -> R.drawable.ic_refresh
            THEME_DARK_INDIGO -> R.drawable.ic_refresh
            else -> R.drawable.ic_refresh
        }
    }

    fun getRewindBackIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_rewind_back_dark
            THEME_LIGHT -> R.drawable.ic_rewind_back_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_rewind_back
            THEME_BROWN_AND_PINK -> R.drawable.ic_rewind_back
            THEME_DARK_INDIGO -> R.drawable.ic_rewind_back
            else -> R.drawable.ic_rewind_back
        }
    }

    fun getPauseIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_pause_dark
            THEME_LIGHT -> R.drawable.ic_pause_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_pause
            THEME_BROWN_AND_PINK -> R.drawable.ic_pause
            THEME_DARK_INDIGO -> R.drawable.ic_pause
            else -> R.drawable.ic_pause
        }
    }

    fun getRewindRightIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_rewind_right_dark
            THEME_LIGHT -> R.drawable.ic_rewind_right_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_rewind_right
            THEME_BROWN_AND_PINK -> R.drawable.ic_rewind_right
            THEME_DARK_INDIGO -> R.drawable.ic_rewind_right
            else -> R.drawable.ic_rewind_right
        }
    }

    fun getCloseIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_close_panel_dark
            THEME_LIGHT -> R.drawable.ic_close_panel_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_close_panel
            THEME_BROWN_AND_PINK -> R.drawable.ic_close_panel
            THEME_DARK_INDIGO -> R.drawable.ic_close_panel
            else -> R.drawable.ic_close_panel
        }
    }

    fun getHeartIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_heart_dark
            THEME_LIGHT -> R.drawable.ic_heart_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_heart
            THEME_BROWN_AND_PINK -> R.drawable.ic_heart
            THEME_DARK_INDIGO -> R.drawable.ic_heart
            else -> R.drawable.ic_heart
        }
    }

    fun getHeartRedIconRes(): Int{
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_heart_red
            THEME_LIGHT -> R.drawable.ic_heart_red
            THEME_BLACK_AND_GREEN -> R.drawable.ic_heart_red
            THEME_BROWN_AND_PINK -> R.drawable.ic_heart_red
            THEME_DARK_INDIGO -> R.drawable.ic_heart_red
            else -> R.drawable.ic_heart_red
        }
    }

    fun getCoverBackgroundMusicBoxIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.drawable.ic_cover_background_small_dark
            THEME_LIGHT -> R.drawable.ic_cover_background_small_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_cover_background_small_black_and_green
            THEME_BROWN_AND_PINK -> R.drawable.ic_cover_background_small_brown_and_pink
            THEME_DARK_INDIGO -> R.drawable.ic_cover_background_small_dark_indigo
            else -> R.drawable.ic_cover_background_small
        }
    }

    fun getCoverBackgroundPlayerIconRes(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.raw.cover_background_animation_dark
            THEME_LIGHT -> R.drawable.ic_cover_background_large_light
            THEME_BLACK_AND_GREEN -> R.drawable.ic_cover_background_small_black_and_green
            THEME_BROWN_AND_PINK -> R.drawable.ic_cover_background_large_brown_and_pink
            THEME_DARK_INDIGO -> R.drawable.ic_cover_background_large_dark_indigo
            else -> R.raw.cover_background_animation
        }
    }

    fun isCoverAnimation(): Boolean {
        return when (currentTheme) {
            THEME_DARK, THEME_DEFAULT -> true
            THEME_LIGHT, THEME_BLACK_AND_GREEN, THEME_BROWN_AND_PINK, THEME_DARK_INDIGO -> false
            else -> true
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