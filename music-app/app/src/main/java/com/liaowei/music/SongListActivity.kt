package com.liaowei.music

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.liaowei.music.common.adapter.SongListAdapter
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.databinding.ActivitySongListBinding
import com.liaowei.music.main.mine.MineFragment
import com.liaowei.music.main.mine.MineFragment.Companion.songList
import com.liaowei.music.main.mine.MineViewModel
import com.liaowei.music.model.domain.Song

class SongListActivity : AppCompatActivity() {

    private val binding: ActivitySongListBinding by lazy { ActivitySongListBinding.inflate(layoutInflater) }
    private var fragment: Fragment? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.song_list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 渲染数据
        val songList = intent.getParcelableArrayListExtra("songList", Song::class.java) as ArrayList<Song>
        val flag = intent.getIntExtra("flag", PageFlag.DEFAULT_SONG_LIST_ACTIVITY)
        /*if (flag == PageFlag.LIKE_SONG_LIST_ACTIVITY) {
            binding.likeRv.adapter = SongListAdapter(this, songList, flag)
        }*/
        binding.likeRv.adapter = SongListAdapter(this, songList, flag)
        binding.likeRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.songTopTitle.text = intent.getStringExtra("songTopTitle")

        // 绑定返回单击事件
        binding.songTopTitle.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                val leftDrawable = binding.songTopTitle.compoundDrawables[0]
                if (leftDrawable != null) {
                    val drawableRect: Rect = leftDrawable.bounds
                    val x = event.x.toInt()
                    val y = event.y.toInt()
                    // 判断点击位置是否在图片的区域内
                    if (x in drawableRect.left until drawableRect.width() && y in drawableRect.top until drawableRect.height()) {
                        // 回到之前的页面
                        finish()
                    }
                }
            }
            true // 消耗事件
        }
    }
}