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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futurepast.databinding.FragmentMainBinding
import kotlinx.coroutines.launch
import android.Manifest

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val sharedPlayerViewModel: SharedPlayerViewModel by activityViewModels()
    private lateinit var musicLoader: MusicLoader
    private val musicList = mutableListOf<MusicData>()
    private lateinit var musicAdapter: MusicAdapter
    private val deleteSongsLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            loadMusicFromDevice()
        }
    }


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
        setupRecyclerView()
        ensurePermissionAndLoad()
        applyTheme()

        binding.ThemesBtn.setOnClickListener {
            (activity as MainActivity).showThemeSelectionDialog()
        }

    }

    private fun setupRecyclerView() {
        musicAdapter = MusicAdapter(
            musicList,
            onItemClick = { music ->
                if (sharedPlayerViewModel.isPlaying.value == true) {
                    sharedPlayerViewModel.pauseMusic()
                } else {
                    sharedPlayerViewModel.playMusic(requireContext(), music, false)
                    (activity as MainActivity).showPopUpMusicPanel()
                }
            },
            onTrashClick = { music -> removeSong(music) }
        )
        binding.MusicRecyclerView.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.InstructionMusicText.visibility = View.GONE
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
                val oldSize = musicList.size
                musicList.clear()
                musicList.addAll(loadedMusic)

                sharedPlayerViewModel.setMusicList(musicList.toList())

                if (oldSize > 0) {
                    musicAdapter.notifyItemRangeRemoved(0, oldSize)
                }
                if (musicList.isNotEmpty()) {
                    musicAdapter.notifyItemRangeInserted(0, musicList.size)
                } else {
                    showNoMusicMessage()
                }
            } catch (e: Exception) {
                showNoMusicMessage()
            }
        }
    }

    private fun removeSong(music: MusicData) {
        val uri = music.contentUri

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val pendingIntent = android.provider.MediaStore.createDeleteRequest(
                    requireContext().contentResolver,
                    listOf(uri)
                )
                deleteSongsLauncher.launch(
                    androidx.activity.result.IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                requireContext().contentResolver.delete(uri, null, null)
                removeFromList(music)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun removeFromList(music: MusicData) {

        val position = musicList.indexOf(music)
        if (position != -1) {
            musicList.removeAt(position)
            musicAdapter.notifyItemRemoved(position)
            sharedPlayerViewModel.removeFromMusicListAndUpdate(requireContext(), music)

            if (position < musicList.size) {
                musicAdapter.notifyItemRangeChanged(position, musicList.size - position)
            }
        }

        if (musicList.isEmpty()) {
            showNoMusicMessage()
        }
    }

    private fun showNoMusicMessage() {
        binding.MusicRecyclerView.visibility = View.GONE
        binding.MusicTopLineView.visibility = View.GONE
        binding.InstructionMusicText.visibility = View.VISIBLE
    }

    fun applyTheme() {
        binding.MainContainer.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.ThemesBtn.setImageResource(ThemeManager.getBrushIconRes())
        binding.MusicRecyclerView.setBackgroundResource(ThemeManager.getBackgroundMusicBoxColorRes())
        binding.textViewTitle.setTextColor(ContextCompat.getColor(binding.root.context, ThemeManager.getTextsColorRes()))
        binding.InstructionMusicText.setTextColor(ContextCompat.getColor(binding.root.context, ThemeManager.getTextsColorRes()))
        musicAdapter.updateTheme()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}