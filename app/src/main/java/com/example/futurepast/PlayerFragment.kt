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
import com.airbnb.lottie.LottieDrawable
import android.animation.ValueAnimator

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
        setupPlayPauseListener()
        observePlaybackState()
    }

    private fun setupPlayPauseListener() {
        binding.PlayPauseSwitcher.setOnClickListener {
            sharedPlayerViewModel.togglePlayPause(requireContext())
        }
    }

    private fun observePlaybackState() {
        sharedPlayerViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.PlayPauseSwitcher.setImageResource(sharedPlayerViewModel.getCurrentIconRes())
            controlLottieAnimation(isPlaying)
        }
    }

    private fun controlLottieAnimation(shouldPlay: Boolean) {
        if (ThemeManager.isCoverAnimation()) {
            if (shouldPlay) {
                startAnimationWithAcceleration()
            } else {
                stopAnimationWithDeceleration()
            }
        }
    }

    private fun startAnimationWithAcceleration() {
        binding.lottieView.speed = 0.1f
        binding.lottieView.resumeAnimation()

        val animator = ValueAnimator.ofFloat(0.1f, 1.0f)
        animator.duration = 800
        animator.addUpdateListener { animation ->
            val speed = animation.animatedValue as Float
            binding.lottieView.speed = speed
        }
        animator.start()
    }

    private fun stopAnimationWithDeceleration() {
        val currentSpeed = binding.lottieView.speed

        val animator = ValueAnimator.ofFloat(currentSpeed, 0f)
        animator.duration = 800
        animator.addUpdateListener { animation ->
            val speed = animation.animatedValue as Float
            binding.lottieView.speed = speed

            if (speed <= 0.1f) {
                binding.lottieView.pauseAnimation()
            }
        }
        animator.start()
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


            binding.lottieView.repeatCount = LottieDrawable.INFINITE
            binding.lottieView.repeatMode = LottieDrawable.RESTART
            binding.lottieView.setMinAndMaxFrame(1, 89)


            binding.lottieView.addAnimatorUpdateListener { animation ->
                if (animation.animatedFraction > 0.99f) {
                    binding.lottieView.post {
                        binding.lottieView.progress = 0f
                    }
                }
            }

            val isPlaying = sharedPlayerViewModel.isPlaying.value ?: true
            if (isPlaying) {
                binding.lottieView.playAnimation()
            } else {
                binding.lottieView.pauseAnimation()
            }

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