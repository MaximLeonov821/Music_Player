package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.futurepast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.MainBtn.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (currentFragment != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }

        binding.MusicBtn.setOnClickListener {
            replaceFragment(PlayerFragment(), "PLAYER")
        }

        binding.HurtOrangeBtn.setOnClickListener {
            replaceFragment(FavouritesFragment(), "FAVOURITES")
        }
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment != null && currentFragment.tag == tag) return

        val (enterAnim, exitAnim, popEnterAnim, popExitAnim) = when {
            currentFragment == null || currentFragment.tag == "PLAYER" && tag == "FAVOURITES" -> {
                Quad(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            }

            currentFragment?.tag == "FAVOURITES" && tag == "PLAYER" -> {
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