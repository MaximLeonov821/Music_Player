package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.futurepast.databinding.FragmentPlayerBinding
import com.airbnb.lottie.LottieAnimationView

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val sharedPlayerViewModel: SharedPlayerViewModel by activityViewModels()

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
            sharedPlayerViewModel.togglePlayPause()
        }
    }

    fun applyTheme() {
        binding.PlayerContainer.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.RefreshBtn?.setImageResource(ThemeManager.getRefreshIconRes())
        binding.RewindBackBtn?.setImageResource(ThemeManager.getRewindBackIconRes())
        binding.RewindRightBtn?.setImageResource(ThemeManager.getRewindRightIconRes())
        binding.HurtBtn?.setImageResource(ThemeManager.getHeartIconRes())
        sharedPlayerViewModel.isPlaying.observe(viewLifecycleOwner) {
            binding.PlayPauseSwitcher.setImageResource(sharedPlayerViewModel.getCurrentIconRes())
        }

        val coverRes = ThemeManager.getCoverBackgroundPlayerIconRes()

        if (ThemeManager.isCoverAnimation()) {
            binding.lottieView.setAnimation(coverRes)
            binding.lottieView.playAnimation()
            binding.lottieView.loop(true)

            binding.lottieView.visibility = View.VISIBLE
            binding.imageViewStatic.visibility = View.GONE
        } else {
            binding.imageViewStatic.setImageResource(coverRes)

            binding.imageViewStatic.visibility = View.VISIBLE
            binding.lottieView.visibility = View.GONE
        }

        binding.musicSeekBar.progressDrawable = ContextCompat.getDrawable(binding.root.context,
            ThemeManager.getSeekBarProgressColorRes())
        binding.musicSeekBar.thumb = ContextCompat.getDrawable(binding.root.context,
            ThemeManager.getSeekBarThumbColorRes())

        ThemeManager.applyToAllTextViews(binding.root) { textView ->
            textView.setTextColor(ContextCompat.getColor(textView.context, ThemeManager.getTextsColorRes()))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}