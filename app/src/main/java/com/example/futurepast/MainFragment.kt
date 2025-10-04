package com.example.futurepast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    fun applyTheme() {
        binding.player.setBackgroundResource(ThemeManager.getBackgroundColorRes())
        binding.linearLayout3?.setBackgroundResource(ThemeManager.getBottomBarColorRes())

        binding.MainBtn?.setImageResource(ThemeManager.getMainIconRes())
        binding.MusicBtn?.setImageResource(ThemeManager.getMusicIconRes())
        binding.HurtOrangeBtn?.setImageResource(ThemeManager.getHurtOrangeIconRes())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}