package com.liaowei.music.fragment

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore.Audio.Media
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liaowei.music.R
import com.liaowei.music.common.constant.MusicConstant.Companion.IS_PLAYING
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_FLAG
import com.liaowei.music.databinding.FragmentPlayingSongBinding
import com.liaowei.music.service.MusicService

class PlayingSongFragment : Fragment() {

    private val binding: FragmentPlayingSongBinding by lazy { FragmentPlayingSongBinding.inflate(layoutInflater) }
    private lateinit var musicBinder: MusicService.MusicBinder
    private var playStatus = false
    companion object {
        fun newInstance() = PlayingSongFragment()
    }
    private val mConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            musicBinder = service as MusicService.MusicBinder
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    }

    private val viewModel: PlayingSongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 获取播放状态
        val playStatus = savedInstanceState?.getBoolean("playStatus")

        // 判断音乐是否在播放，更改图标
        if (playStatus != null && playStatus) {
            binding.playingBtn.setImageResource(R.drawable.pause_circle_80)
        } else{
            binding.playingBtn.setImageResource(R.drawable.play_circle_80)
        }
        binding.playingBtn.setOnClickListener {
            if (musicBinder.callGetPlayStatus()) {
                // musicBinder.callPlay()
                binding.playingBtn.setImageResource(R.drawable.play_circle_80)
            } else {
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80)
            }
        }

        // todo: 该页面不启动服务，在songList点击音乐启动服务
        val intent = Intent(context, MusicService::class.java).apply {
            putExtra(PLAYING_FLAG, IS_PLAYING)
        }
        requireActivity().bindService(intent, mConn, BIND_AUTO_CREATE)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("playStatus", musicBinder.callGetPlayStatus())
    }
}