package com.liaowei.music

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.liaowei.music.broadcast.MusicReceiver
import com.liaowei.music.common.adapter.ActivityViewPagerAdapter
import com.liaowei.music.common.constant.MusicConstant.Companion.DEFAULT_MUSIC_TYPE
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_FLAG
import com.liaowei.music.common.constant.MusicConstant.Companion.UPDATE_PLAYING_FLAG
import com.liaowei.music.common.constant.MusicConstant.Companion.UPDATE_PLAYING_TAB
import com.liaowei.music.databinding.ActivityMainBinding
import com.liaowei.music.main.hall.HallFragment
import com.liaowei.music.main.home.HomeFragment
import com.liaowei.music.main.mine.MineFragment
import com.liaowei.music.main.model.Song
import com.liaowei.music.service.MusicService


class MainActivity : AppCompatActivity() {

    companion object {
        var bound = false
    }
    private lateinit var musicBinder: MusicService.MusicBinder
    private val mConn = MyConn()
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val tabTitles = listOf("首页", "乐馆", "我的")
    private val topTitles = listOf("推荐", "音乐馆", "我的")
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private val handler = @SuppressLint("HandlerLeak") object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // 更改播放栏UI
            val img = msg.data.getInt("img")
            val name = msg.data.getString("name")
            val singer = if (msg.data.getLong("singer") == 1L) "周杰伦" else "蔡徐坤"
            binding.mainPlayingImg.setImageResource(img)
            binding.mainPlayingTitle.text = "$name-$singer"
            binding.mainPlayingPlay.setImageResource(R.drawable.pause_circle)

            // 播放音乐
            val playSong = msg.data.getInt("playSong")
            if (!bound) {
                val intent = Intent(this@MainActivity, MusicService::class.java).apply {
                    putExtra(PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
                    putExtra("song", Song(1, name!!, msg.data.getLong("singer"), img, R.raw.test3, 1))
                }
                bindService(intent, mConn, BIND_AUTO_CREATE)
            } else{
                // 绑定过了就添加歌曲
                musicBinder.callAddSong(Song(1, name!!, msg.data.getLong("singer"), img, R.raw.test3, 1))
            }
        }
    }

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
        registerBroadcast()
    }

    // 注册广播
    private fun registerBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter("com.liaowei.music.broadcast.MusicBroadcast")
        localBroadcastManager.registerReceiver(MusicReceiver(handler), intentFilter)
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


    inner class MyConn: ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBinder = service as MusicService.MusicBinder
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }
}

