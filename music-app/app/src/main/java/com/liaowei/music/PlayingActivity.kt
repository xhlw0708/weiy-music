package com.liaowei.music

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.liaowei.music.common.constant.MusicConstant.Companion.DEFAULT_MUSIC_TYPE
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_FLAG
import com.liaowei.music.databinding.ActivityPlayingBinding
import com.liaowei.music.main.model.Song
import com.liaowei.music.service.MusicService
import com.liaowei.music.service.MusicService.MusicBinder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class PlayingActivity : AppCompatActivity() {


    companion object {
        var currentPosition: Int = 0
    }

    private val binding: ActivityPlayingBinding by lazy {
        ActivityPlayingBinding.inflate(
            layoutInflater
        )
    }
    private lateinit var musicBinder: MusicService.MusicBinder
    private lateinit var musicService: MusicService
    private val handler = Handler(Looper.getMainLooper())
    private val mConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            musicBinder = service as MusicBinder
            musicService = musicBinder!!.getService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }
    private val executorService: ScheduledExecutorService = Executors.newScheduledThreadPool(1)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.post(positionTask)
    }

    private var positionTask: Runnable = object : Runnable {
        override fun run() {
            currentPosition = MusicService.mediaPlayer.currentPosition
            binding.position.text = formatTime(currentPosition)
            binding.progressBar.progress = currentPosition
            handler.postDelayed(this, 1000)
        }
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.playing)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // 判断是否需要换歌曲播放，列表点过来的需要添加PLAYING_MUSIC
        val isChangeSong = intent.getIntExtra(PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
        // 获取跳转过来的歌曲
        val song = intent.getParcelableExtra("song", Song::class.java)
        val bindServiceIntent = Intent(this, MusicService::class.java).apply {
            // 将歌曲传递给service进行播放
            putExtra(PLAYING_FLAG, isChangeSong)
            putExtra("song", song)
        }
        bindService(bindServiceIntent, mConn, BIND_AUTO_CREATE)

        initView()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun initView() {
        handler.postDelayed({
            // 绑定返回按钮
            binding.playingBack.setOnClickListener { finish() }
            // 改变播放按钮图片
            changePlayingBtnImg()
            // 获取歌曲时长
            val duration = musicService.getDuration()
            binding.progressBar.max = duration
            binding.duration.text = formatTime(binding.progressBar.max)
            // 绑定播放按钮
            binding.playingBtn.setOnClickListener {
                MusicService.startOrPause(!MusicService.getMediaPlayerStatus())
                changePlayingBtnImg()
            }
            // 绑定播放进度条
            binding.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    musicService.getMediaPlayer().seekTo(seekBar!!.progress)
                    musicService.startOrPause(true)
                    changePlayingBtnImg()
                }
            })
            // 绑定下一曲按钮
            binding.playingNextSongBtn.setOnClickListener {
                // 播放下一首
                musicService.nextSong()
                if (musicService.getPlayListSize() - 1 == musicService.getIndex()) {
                    // 设置下一首按钮不可点
                    setNextSongBtnState(R.drawable.skip_next_gray, false) // 切换下一首状态为不可点
                }
                // 设置上一首按钮可点
                setPrevSongBtnState(R.drawable.skip_previous, true)
            }
            // 绑定上一首按钮
            binding.playingPrevSongBtn.setOnClickListener {
                // 播放上一首
                musicService.preSong()
                if (musicService.getIndex() == 0) {
                    setPrevSongBtnState(R.drawable.skip_previous_gray, false)
                }
                // 切换下一首按钮可点
                setNextSongBtnState(R.drawable.skip_next, true)
            }

        } , 200)
    }


    // 设置上一首按钮状态
    private fun setPrevSongBtnState(resId: Int, clickable: Boolean) {
        binding.playingPrevSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, resId, 0)
        binding.playingPrevSongBtn.isClickable = clickable
    }

    // 设置下一首按钮状态
    private fun setNextSongBtnState(resId: Int, clickable: Boolean) {
        binding.playingNextSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0)
        binding.playingNextSongBtn.isClickable = clickable
    }

    // 改变播放图片
    private fun changePlayingBtnImg() {
        if (MusicService.getMediaPlayerStatus()) {
            binding.playingBtn.setImageResource(R.drawable.pause_circle_80)
        } else {
            binding.playingBtn.setImageResource(R.drawable.play_circle_80)
        }
    }

    // 格式化时间
    private fun formatTime(time: Int): String {
        val minute = time / 1000 / 60
        val second = time / 1000 % 60
        return String.format("%02d:%02d", minute, second)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.playing_from_top_to_bottom)
        // overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, R.anim.playing_from_top_to_bottom)
    }
}