package com.liaowei.music.main.hall

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.liaowei.music.common.adapter.FragmentViewPagerAdapter
import com.liaowei.music.common.fragment.SongFragment
import com.liaowei.music.databinding.FragmentHallBinding

class HallFragment : Fragment() {

    private val binding: FragmentHallBinding by lazy { FragmentHallBinding.inflate(layoutInflater) }

    companion object {
        fun newInstance() = HallFragment()
        val classifyList: List<String> = listOf("华语", "流行", "摇滚", "民谣", "电子", "网络流行", "日语", "英语",)
    }

    private val viewModel: HallViewModel by viewModels()

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
        // 添加fragment
        val fragmentList = mutableListOf<Fragment>()
        for (i in classifyList.indices) {
            fragmentList.add(SongFragment.newInstance())
        }
        // 设置viewPager
        val fragmentViewPagerAdapter = FragmentViewPagerAdapter(this, fragmentList)
        binding.hallViewPager.adapter = fragmentViewPagerAdapter

        // 动态自定义Tab
        TabLayoutMediator(
            binding.hallTabLayout,
            binding.hallViewPager
        ) { tab: TabLayout.Tab, position: Int ->
            // 自定义TabView
            val tabView = TextView(context)
            tabView.text = classifyList[position]
            tabView.setGravity(Gravity.CENTER)
            tabView.textSize = 20f

            // 将tabItem绑定到tab
            tab.setCustomView(tabView)
        }.attach()

    }
}