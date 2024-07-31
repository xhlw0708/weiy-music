package com.liaowei.music

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.liaowei.music.broadcast.MusicReceiver
import com.liaowei.music.common.adapter.ActivityViewPagerAdapter
import com.liaowei.music.common.constant.DBConstant.Companion.IS_UPDATE_LOCAL_MUSIC
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_FLAG
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_MUSIC
import com.liaowei.music.common.constant.PermissionConstant.Companion.EXTERNAL_PERMISSION
import com.liaowei.music.common.utils.PermissionUtil
import com.liaowei.music.databinding.ActivityMainBinding
import com.liaowei.music.main.hall.HallFragment
import com.liaowei.music.main.home.HomeFragment
import com.liaowei.music.main.mine.MineFragment
import com.liaowei.music.model.domain.Song
import com.liaowei.music.model.provider.MusicContentProvider.Companion.SONG_CONTENT_URI
import com.liaowei.music.service.MusicService
import java.util.Random


class MainActivity : AppCompatActivity() {

    companion object {
        val permission: Array<String> = arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
        )
        private val retriever = MediaMetadataRetriever()
    }
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val tabTitles = listOf("首页", "乐馆", "我的")
    private val topTitles = listOf("推荐", "音乐馆", "我的")
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private val random: Random = Random()

    private val handler = @SuppressLint("HandlerLeak") object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // 更改播放栏UI
            val name = msg.data.getString("name")
            val singerName = msg.data.getString("singerName")
            binding.mainPlayingTitle.text = "$name-$singerName"
            binding.mainPlayingPlay.setImageResource(R.drawable.pause_circle)
            // 处理封面图
            val path = msg.data.getString("path")
            retriever.setDataSource(path)
            val embeddedPicture = retriever.embeddedPicture
            if (embeddedPicture == null) {
                binding.mainPlayingImg.setImageResource(R.drawable.music_video)
            }else{
                val bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size)
                binding.mainPlayingImg.setImageBitmap(bitmap)
            }
            val song = Song(name?:"", singerName?:"", path?:"")
            // 播放音乐
            val intent = Intent(this@MainActivity, PlayingActivity::class.java).apply {
                putExtra(PLAYING_FLAG, PLAYING_MUSIC)
                putExtra("song", song)
            }
            // 跳转activity
            startActivity(intent)
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
        checkMusicPermission()
        // TODO: 提供主动入库的功能

        // 判断是否已经更新过数据库
        val sp = getPreferences(MODE_PRIVATE)
        val isUpdate = sp.getBoolean(IS_UPDATE_LOCAL_MUSIC, false)
        if (!isUpdate) {
            // 获取本地音乐并存入数据库
            getLocalMusicListIntoDB()
        }
        initView()
        registerBroadcast()
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
                MusicService.startOrPause(true)
            } else {
                binding.mainPlayingPlay.setImageResource(R.drawable.play_circle)
                binding.mainPlayingPlay.tag = R.drawable.play_circle
                MusicService.startOrPause(false)
            }
        }

        // 播放栏绑定单击事件
        binding.mainPlayingLayout.setOnClickListener {
            if (MusicService.getPlayListSize() != 0) {
                val intent = Intent(this, PlayingActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.playing_from_bottom_to_top, 0)
                // overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.playing_from_bottom_to_top, 0)
            } else {
                Toast.makeText(this, R.string.main_playing_tab_content, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 获取本地音乐并存入数据库
    private fun getLocalMusicListIntoDB() {
        val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)) // 文件名
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)) // 歌曲名称
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)) // 文件路径
                val singerName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) // 歌手名称
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) // 歌曲时长
                val split = fileName.substring(fileName.lastIndexOf(".") + 1)
                if (split == "mp3" && duration > 1000 || split == "flac" || split == "ncm") {
                    // 插入数据库
                    val values = ContentValues()
                    values.put("name", name)
                    values.put("singerId", cursor.position)
                    values.put("singerName", singerName)
                    values.put("img", R.drawable.jay1)
                    values.put("resourceId", path)
                    values.put("playNumber", 1)
                    values.put("isLike", 0)
                    values.put("category", HallFragment.classifyList[random.nextInt(HallFragment.classifyList.size)])
                    contentResolver.insert(SONG_CONTENT_URI, values)
                }
            }
            cursor.close()
        }else{
            Toast.makeText(this, getString(R.string.main_no_local_music), Toast.LENGTH_SHORT).show()
        }
        // 记录已经将本地音乐存入数据库了
        val sp = getPreferences(MODE_PRIVATE)
        sp.edit().putBoolean(IS_UPDATE_LOCAL_MUSIC, true).commit()
    }


    // 检查权限
    private fun checkMusicPermission(){
        PermissionUtil.checkPermission(this, permission, EXTERNAL_PERMISSION)
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

    // 注册广播
    private fun registerBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter("com.liaowei.music.broadcast.MusicBroadcast")
        localBroadcastManager.registerReceiver(MusicReceiver(handler), intentFilter)
    }

    override fun onResume() {
        super.onResume()
        if (MusicService.getMediaPlayerStatus()) {
            binding.mainPlayingPlay.setImageResource(R.drawable.pause_circle)
            binding.mainPlayingPlay.tag = R.drawable.pause_circle
        } else {
            binding.mainPlayingPlay.setImageResource(R.drawable.play_circle)
            binding.mainPlayingPlay.tag = R.drawable.play_circle
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            EXTERNAL_PERMISSION -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限被授予了", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "需要获得权限才能听音乐", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

