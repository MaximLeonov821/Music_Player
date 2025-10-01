package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
            replaceFragment(PlayerFragment(), "PLAYER")
            selectButton(binding.MusicBtn)
        }

        binding.HurtOrangeBtn.setOnClickListener {
            replaceFragment(FavouritesFragment(), "FAVOURITES")
            selectButton(binding.HurtOrangeBtn)
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