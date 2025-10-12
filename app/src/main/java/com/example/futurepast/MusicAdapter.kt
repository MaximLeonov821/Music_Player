package com.example.futurepast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.futurepast.databinding.ItemMusicBoxBinding

class MusicAdapter(
    private val musicList: List<MusicData>,
    private val onItemClick: (MusicData) -> Unit,
    private val onTrashClick: (MusicData) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    private var currentThemeRes: Int = ThemeManager.getCoverBackgroundMusicBoxIconRes()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ItemMusicBoxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)
    }
    fun updateTheme() {
        currentThemeRes = ThemeManager.getCoverBackgroundMusicBoxIconRes()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(musicList[position])
        holder.applyTheme()
    }

    override fun getItemCount() = musicList.size

    inner class MusicViewHolder(private val binding: ItemMusicBoxBinding) :
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
}