package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.futurepast.databinding.FragmentPlayerBinding
import com.airbnb.lottie.LottieDrawable
import android.animation.ValueAnimator
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val sharedPlayerViewModel: SharedPlayerViewModel by activityViewModels()
    private var seekBarUpdateJob: Job? = null
    private var isUserSeeking = false

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
        musicPlayerPanel()
        setupSeekBar()
    }

    private fun musicPlayerPanel(){
        sharedPlayerViewModel.currentMusic.observe(viewLifecycleOwner){music ->
            if (music != null){
                binding.TextTitle.text = music.title
                binding.TextAuthor.text = music.author
            }
        }
    }


    private fun setupPlayPauseListener() {
        binding.PlayPauseSwitcher.setOnClickListener {
            sharedPlayerViewModel.togglePlayPause(requireContext())
            if (sharedPlayerViewModel.isPlaying.value == true){
                (activity as MainActivity).showPopUpMusicPanel()
            }
        }
    }

    private fun observePlaybackState() {
        sharedPlayerViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.PlayPauseSwitcher.setImageResource(sharedPlayerViewModel.getCurrentPlayPauseIconRes())
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

    private fun setupSeekBar() {
        binding.musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = sharedPlayerViewModel.getDuration()
                    if (duration > 0) {
                        updateSeekBarProgress(progress * duration / 100)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
                seekBarUpdateJob?.cancel()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    val duration = sharedPlayerViewModel.getDuration()
                    if (duration > 0) {
                        val newPos = seekBar.progress * duration / 100
                        sharedPlayerViewModel.seekTo(newPos)
                        updateSeekBarProgress(newPos)
                    }
                }
                isUserSeeking = false
                startUpdatingSeekBar()
            }
        })

        sharedPlayerViewModel.currentMusic.observe(viewLifecycleOwner) { music ->
            if (music != null) {
                updateSeekBarProgress(0)
                startUpdatingSeekBar()
            } else {
                stopUpdatingSeekBar()
            }
        }
    }

    private fun startUpdatingSeekBar() {
        seekBarUpdateJob?.cancel()
        seekBarUpdateJob = lifecycleScope.launch {
            while (isActive) {
                if (!isUserSeeking) {
                    val player = sharedPlayerViewModel.getPlayerInstance()
                    if (player != null && player.duration > 0) {
                        updateSeekBarProgress(player.currentPosition)
                    }
                }
                delay(300)
            }
        }
    }

    private fun stopUpdatingSeekBar() {
        seekBarUpdateJob?.cancel()
        seekBarUpdateJob = null
    }

    private fun updateSeekBarProgress(position: Int) {
        val duration = sharedPlayerViewModel.getDuration()
        if (duration <= 0) return
        binding.musicSeekBar.progress = position * 100 / duration
        binding.totalTime.text = formatTime(position)
    }

    private fun formatTime(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun applyTheme() {
        binding.PlayerContainer.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.RefreshBtn.setImageResource(ThemeManager.getRefreshIconRes())
        binding.RewindBackBtn.setImageResource(ThemeManager.getRewindBackIconRes())
        binding.RewindRightBtn.setImageResource(ThemeManager.getRewindRightIconRes())
        binding.HurtBtn.setImageResource(ThemeManager.getHeartIconRes())
        binding.TextTitle.setTextColor(ContextCompat.getColor(binding.root.context, ThemeManager.getTextsColorRes()))
        binding.TextAuthor.setTextColor(ContextCompat.getColor(binding.root.context, ThemeManager.getTextsColorRes()))
        binding.totalTime.setTextColor(ContextCompat.getColor(binding.root.context, ThemeManager.getTextsColorRes()))
        sharedPlayerViewModel.isPlaying.observe(viewLifecycleOwner) {
            binding.PlayPauseSwitcher.setImageResource(sharedPlayerViewModel.getCurrentPlayPauseIconRes())
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopUpdatingSeekBar()
        _binding = null
    }
}