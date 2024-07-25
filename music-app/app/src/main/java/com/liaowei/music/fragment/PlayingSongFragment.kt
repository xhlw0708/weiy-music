package com.liaowei.music.fragment

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore.Audio.Media
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.liaowei.music.R
import com.liaowei.music.common.constant.MusicConstant.Companion.DEFAULT_MUSIC_TYPE
import com.liaowei.music.common.constant.MusicConstant.Companion.IS_PLAYING
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_FLAG
import com.liaowei.music.databinding.FragmentPlayingSongBinding
import com.liaowei.music.main.model.Song
import com.liaowei.music.service.MusicService

class PlayingSongFragment : Fragment() {

    private val binding: FragmentPlayingSongBinding by lazy { FragmentPlayingSongBinding.inflate(layoutInflater) }
    private lateinit var musicBinder: MusicService.MusicBinder
    private val handler: Handler = Handler(Looper.getMainLooper())
    companion object {
        fun newInstance() = PlayingSongFragment()
        val playSongViewModel: PlayingSongViewModel = PlayingSongViewModel(MutableLiveData(false))
        var bound = false
    }
    private val mConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            musicBinder = service as MusicService.MusicBinder
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }
    private val viewModel: PlayingSongViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 该页面在视图创建之后，只需要绑定到musicService即可，无需任何播放操作
        if (!bound) {
            val intent = Intent(context, MusicService::class.java).apply {
                putExtra(PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
                putExtra("song", Song(1,"周杰伦", 1L, R.drawable.jay1, R.raw.test3, 1))
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
        bindPlayBtn()
        handler.postDelayed({
            if (musicBinder.callGetPlayStatus()) {
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80)
            } else{
                binding.playingBtn.setImageResource(R.drawable.play_circle_80)
            }
        }, 500)
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
}