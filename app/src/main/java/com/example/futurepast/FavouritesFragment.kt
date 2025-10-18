package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futurepast.databinding.FragmentFavouritesBinding

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private val sharedPlayerViewModel: SharedPlayerViewModel by activityViewModels()
    private lateinit var favouritesAdapter: FavouritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTheme()
        setupRecyclerView()
        val musicList = sharedPlayerViewModel.musicList.value
        if (!musicList.isNullOrEmpty()) {
            sharedPlayerViewModel.loadFavouritesFromPrefs(requireContext())
        } else {
            sharedPlayerViewModel.musicList.observe(viewLifecycleOwner) { list ->
                if (list.isNotEmpty()) {
                    sharedPlayerViewModel.loadFavouritesFromPrefs(requireContext())
                }
            }
        }
    }

    private fun setupRecyclerView() {

        favouritesAdapter = FavouritesAdapter(
            sharedPlayerViewModel.favouritesList.value ?: emptyList(),
            onItemClick = { music ->
                if (sharedPlayerViewModel.isPlaying.value == true) {
                    sharedPlayerViewModel.pauseMusic()
                }else {
                    sharedPlayerViewModel.playMusic(requireContext(), music)
                    (activity as MainActivity).showPopUpMusicPanel()
                }
            },
            onTrashClick = { music -> removeSong(music)}
        )

        binding.FavouritesRecyclerView.apply{
            adapter = favouritesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        sharedPlayerViewModel.favouritesList.observe(viewLifecycleOwner) { list ->
            favouritesAdapter.updateData(list)

            if (list.isEmpty()) {
                showNoMusicMessage()
            } else {
                binding.FavouritesRecyclerView.visibility = View.VISIBLE
                binding.MusicTopLineView.visibility = View.VISIBLE
                binding.InstructionMusicText.visibility = View.GONE
            }
        }
    }

    private fun removeSong(music: MusicData) {
        sharedPlayerViewModel.removeFromFavourites(requireContext(), music)
    }

    private fun showNoMusicMessage(){
        binding.FavouritesRecyclerView.visibility = View.GONE
        binding.MusicTopLineView.visibility = View.GONE
        binding.InstructionMusicText.visibility = View.VISIBLE
    }

    fun applyTheme() {
        binding.FavouritesContainer.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.textViewTitle.setTextColor(ContextCompat.getColor(binding.root.context, ThemeManager.getTextsColorRes()))
        binding.FavouritesRecyclerView.setBackgroundResource(ThemeManager.getBackgroundMusicBoxColorRes())
        binding.InstructionMusicText.setTextColor(ContextCompat.getColor(binding.root.context, ThemeManager.getTextsColorRes()))
        if (::favouritesAdapter.isInitialized){
            favouritesAdapter.updateTheme()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}