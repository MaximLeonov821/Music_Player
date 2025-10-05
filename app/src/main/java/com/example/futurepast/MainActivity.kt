package com.example.futurepast

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.futurepast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var selectedButton: View? = null
    private lateinit var scaleUp: Animation
    private lateinit var scaleDown: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ThemeManager.loadTheme(this)
        applyCurrentTheme()

        if (savedInstanceState == null) {
            replaceFragment(MainFragment(), "MAIN")
            selectedButton = binding.MainBtn
            binding.MainBtn.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.scale_up)
            )
        }

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.MainBtn.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (currentFragment != null && currentFragment.tag != "MAIN") {
                replaceFragment(MainFragment(), "MAIN")
            }
            selectButton(binding.MainBtn)
        }

        binding.MusicBtn.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (currentFragment != null && currentFragment.tag != "PLAYER") {
                replaceFragment(PlayerFragment(), "PLAYER")
            }
            selectButton(binding.MusicBtn)
        }

        binding.HurtOrangeBtn.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (currentFragment != null && currentFragment.tag != "FAVOURITES") {
                replaceFragment(FavouritesFragment(), "FAVOURITES")
            }
            selectButton(binding.HurtOrangeBtn)
        }
    }

//    private fun showPopUpMusicPanel(){
//        binding.PopUpMusicPanel.visibility = View.VISIBLE
//        binding.LineView.visibility = View.VISIBLE
//    }
//
//    private fun hidePopUpMusicPanel(){
//        binding.PopUpMusicPanel.visibility = View.GONE
//        binding.LineView.visibility = View.GONE
//    }

    fun showThemeSelectionDialog() {
        val themes = arrayOf("Стандартная", "Темная", "Светлая")
        val currentTheme = ThemeManager.getCurrentTheme()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите тему")
            .setSingleChoiceItems(themes, getThemeIndex(currentTheme)) { dialog, which ->
                when (which) {
                    0 -> ThemeManager.setTheme(ThemeManager.THEME_DEFAULT, this)
                    1 -> ThemeManager.setTheme(ThemeManager.THEME_DARK, this)
                    2 -> ThemeManager.setTheme(ThemeManager.THEME_LIGHT, this)
                }
                applyCurrentTheme()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun getThemeIndex(theme: String): Int {
        return when (theme) {
            ThemeManager.THEME_DEFAULT -> 0
            ThemeManager.THEME_DARK -> 1
            ThemeManager.THEME_LIGHT -> 2
            else -> 0
        }
    }

    fun applyCurrentTheme() {
        binding.linearLayout3.setBackgroundResource(ThemeManager.getBottomBarColorRes())
        binding.linearLayout3.alpha = ThemeManager.getBackgroundAlpha()
        binding.PopUpMusicPanel.setBackgroundResource(ThemeManager.getBackgroundPopUpPanelColorRes())
        binding.PopUpMusicPanel.alpha = ThemeManager.getBackgroundAlpha()

        updateMainActivityIcons()

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        when (currentFragment) {
            is PlayerFragment -> currentFragment.applyTheme()
            is MainFragment -> currentFragment.applyTheme()
            is FavouritesFragment -> currentFragment.applyTheme()
        }
    }

    private fun updateMainActivityIcons() {
        binding.MainBtn.setImageResource(ThemeManager.getMainIconRes())
        binding.MusicBtn.setImageResource(ThemeManager.getMusicIconRes())
        binding.HurtOrangeBtn.setImageResource(ThemeManager.getHeartOrangeIconRes())
        binding.HurtBtn.setImageResource(ThemeManager.getHeartIconRes())
        binding.RewindRightBtn.setImageResource(ThemeManager.getRewindRightIconRes())
        binding.LineView.setBackgroundResource(ThemeManager.getBackgroundLineViewColorRes())

        ThemeManager.applyToAllTextViews(binding.root) { textView ->
            textView.setTextColor(ContextCompat.getColor(textView.context, ThemeManager.getTextsColorRes()))
        }
    }

    private fun selectButton(button: View) {
        if (selectedButton == button) return
        binding.MainBtn.clearAnimation()
        binding.MusicBtn.clearAnimation()
        binding.HurtOrangeBtn.clearAnimation()
        selectedButton?.startAnimation(scaleDown)
        button.startAnimation(scaleUp)
        selectedButton = button
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val (enterAnim, exitAnim, popEnterAnim, popExitAnim) = when {
            currentFragment == null -> {
                Quad(0, 0, 0, 0)
            }
            currentFragment.tag == "PLAYER" && tag == "FAVOURITES" -> {
                Quad(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            }
            currentFragment.tag == "FAVOURITES" && tag == "PLAYER" -> {
                Quad(R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_left)
            }
            currentFragment.tag == "MAIN" && tag == "PLAYER" -> {
                Quad(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            }
            currentFragment.tag == "PLAYER" && tag == "MAIN" -> {
                Quad(R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_left)
            }
            currentFragment.tag == "MAIN" && tag == "FAVOURITES" -> {
                Quad(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            }
            currentFragment.tag == "FAVOURITES" && tag == "MAIN" -> {
                Quad(R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_left)
            }
            else -> {
                Quad(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
            .replace(R.id.fragmentContainer, fragment, tag)
            .addToBackStack(null)
            .commit()
    }
}

data class Quad(val enter: Int, val exit: Int, val popEnter: Int, val popExit: Int)