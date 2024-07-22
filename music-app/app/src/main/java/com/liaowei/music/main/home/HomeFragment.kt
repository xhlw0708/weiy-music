package com.liaowei.music.main.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.fragment.app.replace
import com.google.android.material.tabs.TabLayout
import com.liaowei.music.R
import com.liaowei.music.databinding.FragmentHomeBinding
import com.liaowei.music.main.hall.HallFragment

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val viewModel: HomeViewModel by viewModels()


    companion object {
        fun newInstance() = HomeFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeSpecialColumnRanking.setOnClickListener{
            val mainTabLayout = activity?.findViewById<TabLayout>(R.id.main_tab_layout)
            mainTabLayout?.getTabAt(1)?.select() // TODO("改为常量") 跳转到乐馆
        }

    }
}