package com.example.futurepast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.futurepast.databinding.ItemFavouritesBoxBinding

class FavouritesAdapter(
    private var musicList: List<MusicData>,
    private val onItemClick: (MusicData) -> Unit,
    private val onTrashClick: (MusicData) -> Unit
) : RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {
    private var currentThemeRes: Int = ThemeManager.getCoverBackgroundMusicBoxIconRes()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val binding = ItemFavouritesBoxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouritesViewHolder(binding)
    }

    fun updateTheme() {
        currentThemeRes = ThemeManager.getCoverBackgroundMusicBoxIconRes()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        holder.bind(musicList[position])
        holder.applyTheme()
    }

    override fun getItemCount() = musicList.size

    inner class FavouritesViewHolder(private val binding: ItemFavouritesBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(music: MusicData) {
            binding.MusicTitle.text = music.title

            if (music.author == "Неизвестный автор" || music.author == "unknown") {
                binding.MusicAuthor.visibility = View.GONE
            } else {
                binding.MusicAuthor.visibility = View.VISIBLE
                binding.MusicAuthor.text = music.author
            }

            binding.PlayMusicBtn.setOnClickListener { onItemClick(music) }
            binding.TrashDelete.setOnClickListener { onTrashClick(music) }
        }

        fun applyTheme() {
            binding.CoverMusicBox.setImageResource(currentThemeRes)
            ThemeManager.applyToAllTextViews(binding.root) { textView ->
                textView.setTextColor(ContextCompat.getColor(textView.context, ThemeManager.getTextsColorRes()))
            }

        }
    }

    fun updateData(newList: List<MusicData>) {
        musicList = newList
        notifyDataSetChanged()
    }
}
