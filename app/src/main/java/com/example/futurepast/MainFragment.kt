package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.futurepast.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTheme()

        binding.ThemesBtn.setOnClickListener {
            (activity as MainActivity).showThemeSelectionDialog()
        }
    }

    fun applyTheme() {
        binding.MainContainer?.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.ThemesBtn?.setImageResource(ThemeManager.getBrushIconRes())

        ThemeManager.applyToAllTextViews(binding.root) { textView ->
            textView.setTextColor(ContextCompat.getColor(textView.context, ThemeManager.getTextsColorRes()))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}