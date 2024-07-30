/*
package com.liaowei.music.fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore.Audio.Media
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.liaowei.music.MainActivity.Companion.bound
import com.liaowei.music.R
import com.liaowei.music.common.constant.MusicConstant.Companion.DEFAULT_MUSIC_TYPE
import com.liaowei.music.common.constant.MusicConstant.Companion.IS_PLAYING
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_FLAG
import com.liaowei.music.databinding.FragmentPlayingSongBinding
import com.liaowei.music.model.domain.Song
import com.liaowei.music.service.MusicService

class PlayingSongFragmentBak : Fragment() {

    private val binding: FragmentPlayingSongBinding by lazy {
        FragmentPlayingSongBinding.inflate(
            layoutInflater
        )
    }
    companion object {
        fun newInstance() = PlayingSongFragmentBak()
        // val playSongViewModel: PlayingSongViewModel = PlayingSongViewModel(MutableLiveData(false))
        var bound = false
    }
    private lateinit var musicBinder: MusicService.MusicBinder
    private val initHandler: Handler = Handler(Looper.getMainLooper())
    private val mConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            musicBinder = service as MusicService.MusicBinder
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }
    private val updateBarHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val duration = msg.arg1 // 总时长
            val position = msg.arg2 // 当前播放进度
            // 更新进度条
            // 更新进度条
            binding.progressBar.max = duration
            binding.progressBar.progress = position
        }
    }

    fun getUpdateBarHandler() = updateBarHandler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 该页面在视图创建之后，只需要绑定到musicService即可，无需任何播放操作
        if (!bound) {
            val intent = Intent(context, MusicService::class.java).apply {
                putExtra(PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
                putExtra("song", Song(1, "周杰伦", 1L, R.drawable.jay1, R.raw.test3, 1))
            }
            requireActivity().bindService(intent, mConn, BIND_AUTO_CREATE)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解绑service
        if (bound) {
            requireActivity().unbindService(mConn)
            bound = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHandler.postDelayed({
            // 绑定播放按钮单击事件
            bindPlayBtn()

            // 进入页面初始化播放按钮
            if (musicBinder.callGetPlayStatus()) {
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80)
            } else {
                binding.playingBtn.setImageResource(R.drawable.play_circle_80)
            }

            // 绑定下一首
            binding.playingNextSongBtn.setOnClickListener {
                musicBinder.callNextSong()
                if (musicBinder.callGetIndex() == musicBinder.callGetPlayListSize() - 1) run {
                    setNextSongBtnState(R.drawable.skip_next_gray, false) // 切换下一首状态为不可点
                }
                setPrevSongBtnState(R.drawable.skip_previous, true)
            }
            // 绑定上一首
            binding.playingPrevSongBtn.setOnClickListener {
                musicBinder.callPreSong()
                if (musicBinder.callGetIndex() == 0) run {
                    setPrevSongBtnState(R.drawable.skip_previous_gray, false)
                }
                setNextSongBtnState(R.drawable.skip_next, true) // 切换下一首状态为可点
            }
        }, 500)
    }

    // 设置上一首按钮状态
    private fun setPrevSongBtnState(resId: Int, clickable: Boolean) {
        binding.playingPrevSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            resId,
            0
        )
        binding.playingNextSongBtn.isClickable = clickable
    }

    // 设置下一首按钮状态
    private fun setNextSongBtnState(resId: Int, clickable: Boolean) {
        binding.playingNextSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
            resId,
            0,
            0,
            0
        )
        binding.playingNextSongBtn.isClickable = clickable
    }

    // 绑定播放按钮
    private fun bindPlayBtn() {
        binding.playingBtn.setOnClickListener {
            if (musicBinder.callGetPlayStatus()) { // 正在播放
                musicBinder.callsStartOrPause(false) // 暂停
                binding.playingBtn.setImageResource(R.drawable.play_circle_80)
            } else {
                musicBinder.callsStartOrPause(true) // 播放
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80)
            }
        }
    }
}*/
