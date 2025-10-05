package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.futurepast.databinding.FragmentPlayerBinding
import com.airbnb.lottie.LottieAnimationView

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
        binding.PlayerContainer.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.RefreshBtn?.setImageResource(ThemeManager.getRefreshIconRes())
        binding.RewindBackBtn?.setImageResource(ThemeManager.getRewindBackIconRes())
        binding.RewindRightBtn?.setImageResource(ThemeManager.getRewindRightIconRes())
        binding.HurtBtn?.setImageResource(ThemeManager.getHeartIconRes())
        binding.imageView?.setImageResource(ThemeManager.getCoverBackgroundIconRes())

        binding.musicSeekBar.progressDrawable = ContextCompat.getDrawable(binding.root.context,
            ThemeManager.getSeekBarProgressColorRes())
        binding.musicSeekBar.thumb = ContextCompat.getDrawable(binding.root.context,
            ThemeManager.getSeekBarThumbColorRes())

        ThemeManager.applyToAllTextViews(binding.root) { textView ->
            textView.setTextColor(ContextCompat.getColor(textView.context, ThemeManager.getTextsColorRes()))
        }

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