package com.liaowei.music.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.liaowei.music.broadcast.SeekBarReceiver
import com.liaowei.music.common.constant.MusicConstant.Companion.ADD_SONG
import com.liaowei.music.common.constant.MusicConstant.Companion.DEFAULT_MUSIC_TYPE
import com.liaowei.music.common.constant.MusicConstant.Companion.IS_PLAYING
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_FLAG
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_MUSIC
import com.liaowei.music.common.constant.MusicConstant.Companion.REMOVE_SONG
import com.liaowei.music.fragment.PlayingSongFragment
import com.liaowei.music.main.model.Song
import java.util.LinkedList
import java.util.Timer
import java.util.TimerTask

class MusicService : Service() {

    private val binder = MusicBinder(this)
    // 维护一个播放队列
    companion object {
        private var playList: LinkedList<Song>? = LinkedList()
        val mediaPlayer: MediaPlayer = MediaPlayer()
        private var index = 0 // 记录播放的索引
        const val GET_SONG_STATE_MSG = 1 // 获取歌曲时长和播放进度
        const val SEND_SONG_STATE_MSG = 2 // 发送给客户端消息
        // 开启或暂停
        fun startOrPause(isPlay: Boolean) {
            if (isPlay) mediaPlayer.start() else mediaPlayer.pause()
        }
        // 获取播放状态
        fun getMediaPlayerStatus(): Boolean = mediaPlayer.isPlaying
        // 获取歌曲列表长度
        fun getPlayListSize(): Int = playList?.size ?: 0
    }

    private val serviceHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                GET_SONG_STATE_MSG -> {
                    clientMessenger = msg.replyTo
                    val replyMsg = Message.obtain(null, SEND_SONG_STATE_MSG)
                    replyMsg.arg1 = mediaPlayer.duration // 总时长
                    // replyMsg.arg2 = mediaPlayer.currentPosition // 播放进度
                    clientMessenger.send(replyMsg)
                }

                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }
    private val serviceMessenger: Messenger = Messenger(serviceHandler)
    private lateinit var clientMessenger: Messenger


    init {
        mediaPlayer.setOnCompletionListener {
            // 检查播放列表是否为空，为空则停止播放
            if (playList?.size == index ++) {
                mediaPlayer.stop()
                Toast.makeText(baseContext, "歌曲播放完毕", Toast.LENGTH_SHORT).show()
            } else {
                // 下一首
                nextSong()
            }
        }
    }



    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onBind(intent: Intent): IBinder {
        val type = intent.getIntExtra(PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
        val song = intent.getParcelableExtra("song", Song::class.java)
        when (type) {
            PLAYING_MUSIC -> {
                playList?.push(song)
                mediaPlayer.reset()
                val afd: AssetFileDescriptor = resources.openRawResourceFd(song!!.resourceId)
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                mediaPlayer.prepare()
                startOrPause(true)
            }

            else -> {
                Log.d("tag", "onBind: ${playList?.size}")
                // 仅仅打开播放页
            }
        }
        return binder
    }


    override fun onUnbind(intent: Intent?): Boolean {
        /*playList?.clear()
        playList = null
        mediaPlayer.release()*/
        return super.onUnbind(intent)
    }

    // 返回播放状态
    fun getPlayStatus() = mediaPlayer.isPlaying

    // 获取mediaPlayer
    fun getMediaPlayer() = mediaPlayer

    // 下一曲
    fun nextSong() {
        if (playList?.size!! > index + 1) {
            mediaPlayer.reset()
            index++
            val afd: AssetFileDescriptor = resources.openRawResourceFd(playList?.get(index)?.resourceId!!)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
            startOrPause(true)
        } else if (playList?.size!! == index + 1) {
            Toast.makeText(baseContext, "已经是最后一首了", Toast.LENGTH_SHORT).show()
        }
    }


    // 上一曲
    fun preSong() {
        if (index == 0) {
            // 正在播放第一首
            Toast.makeText(baseContext, "已经是第一首了", Toast.LENGTH_SHORT).show()
        }
        if (index > 0) {
            mediaPlayer.reset()
            index--
            val afd: AssetFileDescriptor =
                resources.openRawResourceFd(playList?.get(index)?.resourceId!!)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
            startOrPause(true)
        }
    }

    // 播放或暂停
    fun startOrPause(isPlay: Boolean) {
        if (isPlay) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
        }
    }

    // 获取播放时长
    fun getDuration(): Int = mediaPlayer.duration
    // 更新音乐进度
    fun updateProgress(position: Int) {
        mediaPlayer.seekTo(position)
        if (!mediaPlayer.isPlaying) {
            startOrPause(true)
        }
    }
    // 获取当前索引
    fun getIndex(): Int = index

    // 获取播放列表长度
    fun getPlayListSize(): Int = playList?.size!!


    inner class MusicBinder(private val service: MusicService) : Binder() {
        fun getService(): MusicService = service
    }
}