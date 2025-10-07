package com.example.futurepast

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.futurepast.databinding.FragmentMainBinding
import kotlinx.coroutines.launch
import android.Manifest

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val sharedPlayerViewModel: SharedPlayerViewModel by activityViewModels()
    private lateinit var musicLoader: MusicLoader
    private val musicList = mutableListOf<MusicData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        musicLoader = MusicLoader(requireContext())
        ensurePermissionAndLoad()
        applyTheme()

        binding.ThemesBtn.setOnClickListener {
            (activity as MainActivity).showThemeSelectionDialog()
        }

        binding.PlayMusicBtn.setOnClickListener {
            if (musicList.isNotEmpty()) {
                onMusicItemClicked(musicList[0])
            }
        }
    }
    private fun ensurePermissionAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission), 100)
        } else {
            loadMusicFromDevice()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMusicFromDevice()
        } else {
            showNoMusicMessage()
        }
    }

    private fun loadMusicFromDevice() {
        lifecycleScope.launch {
            try {
                val loadedMusic = musicLoader.loadMusicFromDevice()
                musicList.clear()
                musicList.addAll(loadedMusic)

                if (musicList.isNotEmpty()) {
                    updateMusicBoxWithSong(musicList[0])
                } else {
                    showNoMusicMessage()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showNoMusicMessage()
            }
        }
    }
    private fun updateMusicBoxWithSong(music: MusicData) {
        binding.MusicTitle.text = music.title
        binding.MusicAuthor.text = music.author

        binding.TrashDelete.setOnClickListener {
            removeCurrentSong(music)
        }
    }

    private fun onMusicItemClicked(music: MusicData) {
        sharedPlayerViewModel.playMusic(music)
    }

    private fun removeCurrentSong(music: MusicData) {
        musicList.remove(music)
        if (musicList.isNotEmpty()) {
            updateMusicBoxWithSong(musicList[0])
        } else {
            showNoMusicMessage()
        }
    }

    private fun showNoMusicMessage() {
        binding.MusicTitle.text = "Музыка не скачана"
        binding.MusicAuthor.visibility = View.GONE
        binding.TrashDelete.visibility = View.GONE
    }

    fun applyTheme() {
        binding.MainContainer?.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.ThemesBtn?.setImageResource(ThemeManager.getBrushIconRes())
        binding.MusicBox?.setBackgroundResource(ThemeManager.getBackgroundMusicBoxColorRes())
        binding.CoverMusicBox?.setImageResource(ThemeManager.getCoverBackgroundMusicBoxIconRes())

        ThemeManager.applyToAllTextViews(binding.root) { textView ->
            textView.setTextColor(ContextCompat.getColor(textView.context, ThemeManager.getTextsColorRes()))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}