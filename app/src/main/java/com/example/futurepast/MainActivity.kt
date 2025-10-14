package com.example.futurepast

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.futurepast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val sharedPlayerViewModel: SharedPlayerViewModel by viewModels()
    private lateinit var mainFragment: MainFragment
    private lateinit var playerFragment: PlayerFragment
    private lateinit var favouritesFragment: FavouritesFragment
    private var activeFragment: Fragment? = null
    private var selectedButton: View? = null
    private lateinit var scaleUp: Animation
    private lateinit var scaleDown: Animation
    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hidePopUpMusicPanel()
        hideNavigationBar()
        ThemeManager.loadTheme(this)
        applyCurrentTheme()

        if (savedInstanceState == null) {
            initFragments()
        } else {
            mainFragment = supportFragmentManager.findFragmentByTag("MAIN") as MainFragment
            playerFragment = supportFragmentManager.findFragmentByTag("PLAYER") as PlayerFragment
            favouritesFragment = supportFragmentManager.findFragmentByTag("FAVOURITES") as FavouritesFragment
            activeFragment = when {
                mainFragment.isVisible -> mainFragment
                playerFragment.isVisible -> playerFragment
                else -> favouritesFragment
            }
        }

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.MainBtn.setOnClickListener {
            onButtonClick {
                if (activeFragment != mainFragment) {
                    switchFragment(mainFragment, "MAIN")
                }
                selectButton(binding.MainBtn)
            }
        }

        binding.MusicBtn.setOnClickListener {
            onButtonClick {
                if (activeFragment != playerFragment) {
                    switchFragment(playerFragment, "PLAYER")
                }
                selectButton(binding.MusicBtn)
            }
        }

        binding.HurtOrangeBtn.setOnClickListener {
            onButtonClick {
                if (activeFragment != favouritesFragment) {
                    switchFragment(favouritesFragment, "FAVOURITES")
                }
                selectButton(binding.HurtOrangeBtn)
            }
        }

        binding.PlayPauseSwitcher.setOnClickListener {
            sharedPlayerViewModel.togglePlayPause(this)
        }

        binding.RewindRightBtn.setOnClickListener {
            if (sharedPlayerViewModel.rewindRightOrClose.value == true){
                // Тут будет перемотка вперед
            }else{
                hidePopUpMusicPanel()
            }
        }
    }

    private fun onButtonClick(action: () -> Unit) {
        val now = System.currentTimeMillis()
        if (now - lastClickTime < 500) return
        lastClickTime = now
        action()
    }

    private fun hideNavigationBar() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
    }

    fun showPopUpMusicPanel() {
        binding.PopUpMusicPanel.visibility = View.VISIBLE
        binding.PanelTopLineView.visibility = View.VISIBLE
        binding.PanelBottomLineView.visibility = View.VISIBLE
    }

    fun hidePopUpMusicPanel(){
        binding.PopUpMusicPanel.visibility = View.GONE
        binding.PanelTopLineView.visibility = View.GONE
        binding.PanelBottomLineView.visibility = View.GONE
    }

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
        binding.ActivityContainer.setBackgroundResource(ThemeManager.getBackgroundColorRes())

        updateMainActivityIcons()

        if (::mainFragment.isInitialized) mainFragment.applyTheme()
        if (::playerFragment.isInitialized) playerFragment.applyTheme()
        if (::favouritesFragment.isInitialized) favouritesFragment.applyTheme()
    }

    private fun updateMainActivityIcons() {
        binding.MainBtn.setImageResource(ThemeManager.getMainIconRes())
        binding.MusicBtn.setImageResource(ThemeManager.getMusicIconRes())
        binding.HurtOrangeBtn.setImageResource(ThemeManager.getHeartOrangeIconRes())
        binding.HurtBtn.setImageResource(ThemeManager.getHeartIconRes())
        binding.PanelBottomLineView.setBackgroundResource(ThemeManager.getBackgroundLineViewColorRes())
        binding.TextTitleMainActivity.setTextColor(ContextCompat.getColor(this, ThemeManager.getTextsColorRes()))
        binding.TextAuthorMainActivity.setTextColor(ContextCompat.getColor(this, ThemeManager.getTextsColorRes()))
        sharedPlayerViewModel.rewindRightOrClose.observe(this) {
            binding.RewindRightBtn.setImageResource(sharedPlayerViewModel.getCurrentRewindRightIconRes())
        }
        sharedPlayerViewModel.isPlaying.observe(this) {
            binding.PlayPauseSwitcher.setImageResource(sharedPlayerViewModel.getCurrentPlayPauseIconRes())
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

    private fun initFragments() {
        mainFragment = MainFragment()
        playerFragment = PlayerFragment()
        favouritesFragment = FavouritesFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, mainFragment, "MAIN")
            .add(R.id.fragmentContainer, playerFragment, "PLAYER").hide(playerFragment)
            .add(R.id.fragmentContainer, favouritesFragment, "FAVOURITES").hide(favouritesFragment)
            .commit()

        activeFragment = mainFragment
        selectedButton = binding.MainBtn
        binding.MainBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up))
    }

    private fun switchFragment(target: Fragment, tag: String) {
        if (target == activeFragment) return

        val (enterAnim, exitAnim, popEnterAnim, popExitAnim) = when {
            activeFragment?.tag == "PLAYER" && tag == "FAVOURITES" -> {
                Quad(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            }
            activeFragment?.tag == "FAVOURITES" && tag == "PLAYER" -> {
                Quad(R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_left)
            }
            activeFragment?.tag == "MAIN" && tag == "PLAYER" -> {
                Quad(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            }
            activeFragment?.tag == "PLAYER" && tag == "MAIN" -> {
                Quad(R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_left)
            }
            activeFragment?.tag == "MAIN" && tag == "FAVOURITES" -> {
                Quad(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            }
            activeFragment?.tag == "FAVOURITES" && tag == "MAIN" -> {
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
            .hide(activeFragment!!)
            .show(target)
            .commit()

        activeFragment = target
    }
}

data class Quad(val enter: Int, val exit: Int, val popEnter: Int, val popExit: Int)