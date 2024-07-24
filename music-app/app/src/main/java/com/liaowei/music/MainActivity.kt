package com.liaowei.music

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.liaowei.music.common.adapter.ActivityViewPagerAdapter
import com.liaowei.music.databinding.ActivityMainBinding
import com.liaowei.music.main.hall.HallFragment
import com.liaowei.music.main.home.HomeFragment
import com.liaowei.music.main.mine.MineFragment


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val tabTitles = listOf("首页", "乐馆", "我的")
    private val topTitles = listOf("推荐", "音乐馆", "我的")


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {
        // 添加fragment
        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(HomeFragment.newInstance())
        fragmentList.add(HallFragment.newInstance())
        fragmentList.add(MineFragment.newInstance())

        // 设置viewPager
        val activityViewPagerAdapter = ActivityViewPagerAdapter(this, fragmentList)
        binding.mainViewPager.adapter = activityViewPagerAdapter
        binding.mainViewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT // 禁用预加载

        // 动态自定义Tab
        bindTab()

        // 预先设置播放图案的标签为“播放”
        binding.mainPlayingPlay.tag = R.drawable.play_circle
        // 绑定播放按钮
        binding.mainPlayingPlay.setOnClickListener {
            if (binding.mainPlayingPlay.tag == R.drawable.play_circle) {
                binding.mainPlayingPlay.setImageResource(R.drawable.pause_circle)
                binding.mainPlayingPlay.tag = R.drawable.pause_circle
            } else {
                binding.mainPlayingPlay.setImageResource(R.drawable.play_circle)
                binding.mainPlayingPlay.tag = R.drawable.play_circle
            }
        }

        // 播放栏绑定单击事件
        binding.mainPlayingLayout.setOnClickListener{
            val intent = Intent(this, PlayingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.playing_from_bottom_to_top, 0)
            // overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.playing_from_bottom_to_top, 0)
        }
    }

    private fun bindTab() {
        TabLayoutMediator(
            binding.mainTabLayout,
            binding.mainViewPager
        ) { tab: TabLayout.Tab, position: Int ->
            // 自定义TabView
            val tabView = TextView(this)
            tabView.text = tabTitles[position]
            tabView.setGravity(Gravity.CENTER)
            tabView.textSize = 20f
            binding.topTitle.text = (if (position == 0) topTitles[position] else "首页")

            // 将tabItem绑定到tab
            tab.setCustomView(tabView)
        }.attach()

        // 绑定tab选择事件
        binding.mainViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.topTitle.text = topTitles[position] // 更换顶部名称
            }
        })
    }
}

