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
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
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
    private var isPanelMusicVisible = false
    private var isMetadataVisible = false
    private var isLyricsVisible = false

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
        musicMetadataPanel()
        setupSeekBar()
        observeRefreshState()
        setupMusicPanel()

        binding.PanelMusic.translationY = binding.PanelMusic.height.toFloat()

        binding.RewindRightBtn.setOnClickListener {
            sharedPlayerViewModel.nextMusic(requireContext())
        }

        binding.RewindBackBtn.setOnClickListener {
            sharedPlayerViewModel.backMusic(requireContext())
        }

        binding.RefreshBtn.setOnClickListener {
            sharedPlayerViewModel.toggleShuffleForAll()
        }

        binding.HurtBtn.setOnClickListener {
            val currentMusic = sharedPlayerViewModel.currentMusic.value
            if (currentMusic != null) {
                sharedPlayerViewModel.addToFavourites(requireContext(), currentMusic)
            }
        }

        binding.TextTrack.setOnClickListener {
            if (isPanelMusicVisible) {
                hideMusicPanel {
                    showMetadataPanel()
                }
            }
        }

        binding.TextLyrics.setOnClickListener {
            if (!isLyricsVisible) {
                hideMusicPanel {
                    showLyricsPanel()
                }
            }
        }
    }

    private fun showLyricsPanel() {
        if (isLyricsVisible) return

        val panel = binding.LyricsPanel
        panel.apply {
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.9f
            scaleY = 0.9f
        }

        tryApplyBlur(true)

        binding.overlayDim.apply {
            visibility = View.VISIBLE
            animate()
                .alpha(0.55f)
                .setDuration(200)
                .setInterpolator(FastOutSlowInInterpolator())
                .start()
        }

        panel.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setInterpolator(FastOutSlowInInterpolator())
            .start()

        isLyricsVisible = true
    }

    private fun hideLyricsPanel(onHidden: (() -> Unit)? = null) {
        if (!isLyricsVisible) {
            onHidden?.invoke()
            return
        }

        val panel = binding.LyricsPanel

        panel.animate()
            .alpha(0f)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(250)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction {
                panel.visibility = View.GONE
                panel.alpha = 1f
                panel.scaleX = 1f
                panel.scaleY = 1f
                isLyricsVisible = false

                binding.overlayDim.animate()
                    .alpha(0f)
                    .setDuration(180)
                    .withEndAction {
                        binding.overlayDim.visibility = View.GONE
                        tryApplyBlur(false)
                    }
                    .start()

                onHidden?.invoke()
            }
            .start()
    }

    private fun showMetadataPanel() {
        if (isMetadataVisible) return

        val panel = binding.MetadataMusicPanel
        panel.apply {
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.9f
            scaleY = 0.9f
        }

        tryApplyBlur(true)

        binding.overlayDim.apply {
            visibility = View.VISIBLE
            animate()
                .alpha(0.55f)
                .setDuration(200)
                .setInterpolator(FastOutSlowInInterpolator())
                .start()
        }

        panel.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setInterpolator(FastOutSlowInInterpolator())
            .start()

        isMetadataVisible = true
    }

    private fun hideMetadataPanel(onHidden: (() -> Unit)? = null) {
        if (!isMetadataVisible) {
            onHidden?.invoke()
            return
        }

        val panel = binding.MetadataMusicPanel

        panel.animate()
            .alpha(0f)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(250)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction {
                panel.visibility = View.GONE
                panel.alpha = 1f
                panel.scaleX = 1f
                panel.scaleY = 1f
                isMetadataVisible = false

                binding.overlayDim.animate()
                    .alpha(0f)
                    .setDuration(180)
                    .withEndAction {
                        binding.overlayDim.visibility = View.GONE
                        tryApplyBlur(false)
                    }
                    .start()

                onHidden?.invoke()
            }
            .start()
    }


    private fun setupMusicPanel() {
        binding.ToggleMusicPanel.setOnClickListener {
            toggleMusicPanel()
        }

        binding.closeLyricsPanel.setOnClickListener {
            hideLyricsPanel()
        }

        binding.overlayDim.setOnClickListener {
            if (isMetadataVisible){
                hideMetadataPanel()
            } else if (isLyricsVisible) {
                hideLyricsPanel()
            } else {
                hideMusicPanel()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        isMetadataVisible -> hideMetadataPanel()
                        isPanelMusicVisible -> hideMusicPanel()
                        isLyricsVisible -> hideLyricsPanel()
                        else -> {
                            isEnabled = false
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            }
        )
    }

    private fun toggleMusicPanel() {
        if (isPanelMusicVisible) hideMusicPanel() else showMusicPanel()
    }

    private fun showMusicPanel() {
        if (isPanelMusicVisible) return

        binding.overlayDim.visibility = View.VISIBLE
        binding.overlayDim.alpha = 0f
        binding.overlayDim.animate()
            .alpha(0.55f)
            .setDuration(200)
            .start()

        tryApplyBlur(true)

        binding.PanelMusic.visibility = View.VISIBLE

        val panelHeight = binding.PanelMusic.height
        if (panelHeight == 0) {
            binding.PanelMusic.doOnLayout { panel ->
                animatePanelUp(panel.height)
            }
        } else {
            animatePanelUp(panelHeight)
        }

        isPanelMusicVisible = true
    }

    private fun animatePanelUp(panelHeightPx: Int) {
        binding.PanelMusic.translationY = panelHeightPx.toFloat()
        binding.PanelMusic.animate()
            .translationY(0f)
            .setDuration(300)
            .setInterpolator(FastOutSlowInInterpolator())
            .start()
    }

    private fun hideMusicPanel(onHidden: (() -> Unit)? = null) {
        if (!isPanelMusicVisible) {
            onHidden?.invoke()
            return
        }

        val h = binding.PanelMusic.height
        val animateAction = {
            binding.PanelMusic.animate()
                .translationY(h.toFloat())
                .setDuration(260)
                .setInterpolator(FastOutSlowInInterpolator())
                .withEndAction {
                    binding.PanelMusic.visibility = View.GONE
                    binding.PanelMusic.translationY = 0f
                    isPanelMusicVisible = false

                    binding.overlayDim.animate()
                        .alpha(0f)
                        .setDuration(180)
                        .withEndAction {
                            binding.overlayDim.visibility = View.GONE
                            tryApplyBlur(false)
                        }
                        .start()

                    onHidden?.invoke()
                }
                .start()
        }

        if (h == 0) {
            binding.PanelMusic.doOnLayout { animateAction() }
        } else animateAction()
    }

    private fun tryApplyBlur(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (enable) {
                val radius = 20f
                val effect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
                binding.PlayerContainer.setRenderEffect(effect)
            } else {
                binding.PlayerContainer.setRenderEffect(null)
            }
        }
    }

    private fun musicMetadataPanel() {
        sharedPlayerViewModel.currentMusic.observe(viewLifecycleOwner){music ->
            if (music != null) {
                binding.TitleMetadata.text = "Название: " + music.title
                binding.AuthorMetadata.text = "Артисты: " + music.author
                binding.DurationMetadata.text = "Длительность: " + formatDuration(music.duration)
            }
        }
    }

    private fun musicPlayerPanel(){
        sharedPlayerViewModel.currentMusic.observe(viewLifecycleOwner){music ->
            if (music != null){
                binding.TextTitle.text = music.title
                binding.TextAuthor.text = music.author
            }
        }
    }

    private fun observeRefreshState() {
        sharedPlayerViewModel.isShuffled.observe(viewLifecycleOwner) {updateShuffleRotation(it)}
        sharedPlayerViewModel.isFavouritesShuffled.observe(viewLifecycleOwner) {updateShuffleRotation(it)}

    }

    private fun updateShuffleRotation(isOn: Boolean) {
        if (isOn) {
            binding.RefreshBtn.animate().rotationBy(360f).setDuration(300).start()
        } else {
            binding.RefreshBtn.animate().rotationBy(-360f).setDuration(300).start()
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

    private fun formatDuration(durationMillis: Long): String {
        val totalSeconds = durationMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun applyTheme() {
        binding.PlayerContainer.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.MetadataMusicPanel.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.PanelMusic.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.LyricsPanel.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.RefreshBtn.setImageResource(ThemeManager.getRefreshIconRes())
        binding.RewindBackBtn.setImageResource(ThemeManager.getRewindBackIconRes())
        binding.RewindRightBtn.setImageResource(ThemeManager.getRewindRightIconRes())
        binding.HurtBtn.setImageResource(ThemeManager.getHeartIconRes())
        binding.ToggleMusicPanel.setImageResource(ThemeManager.getMenuIconRes())
        binding.closeLyricsPanel.setImageResource(ThemeManager.getCloseIconRes())
        ThemeManager.applyToAllTextViews(binding.root) { textView ->
            textView.setTextColor(ContextCompat.getColor(textView.context, ThemeManager.getTextsColorRes()))
        }
        sharedPlayerViewModel.isFavouritesAdd.observe(viewLifecycleOwner) {
            binding.HurtBtn.setImageResource(sharedPlayerViewModel.getCurrentHeartIconRes())
        }
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