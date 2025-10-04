package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.futurepast.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private var isPlaying = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyTheme()

        binding.PlayPauseSwitcher.setOnClickListener {
            isPlaying = !isPlaying
            updatePlayPauseButton()
        }
    }

    fun applyTheme() {
        binding.player.setBackgroundResource(ThemeManager.getBackgroundColorRes())

        binding.linearLayout3.setBackgroundResource(ThemeManager.getBottomBarColorRes())

        updateFragmentIcons()
    }

    private fun updateFragmentIcons() {

        binding.MainBtn?.setImageResource(ThemeManager.getMainIconRes())
        binding.MusicBtn?.setImageResource(ThemeManager.getMusicIconRes())
        binding.HurtOrangeBtn?.setImageResource(ThemeManager.getHurtOrangeIconRes())

        updatePlayPauseButton()
    }

    private fun updatePlayPauseButton() {
        val iconRes = if (isPlaying) {
            ThemeManager.getPlayIconRes()
        } else {
            ThemeManager.getPauseIconRes()
        }
        binding.PlayPauseSwitcher.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}