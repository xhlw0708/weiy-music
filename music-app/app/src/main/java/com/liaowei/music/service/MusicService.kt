package com.liaowei.music.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.liaowei.music.R
import com.liaowei.music.common.constant.MusicConstant.Companion.ADD_SONG
import com.liaowei.music.common.constant.MusicConstant.Companion.DEFAULT_MUSIC_TYPE
import com.liaowei.music.common.constant.MusicConstant.Companion.IS_PLAYING
import com.liaowei.music.common.constant.MusicConstant.Companion.PLAYING_FLAG
import com.liaowei.music.common.constant.MusicConstant.Companion.REMOVE_SONG
import com.liaowei.music.main.model.Song
import java.util.LinkedList
import java.util.Queue

class MusicService : Service() {

    private val binder = MusicBinder()
    private lateinit var mediaPlayer: MediaPlayer

    // 维护一个播放队列
    private var playList: Queue<Song>? = LinkedList()
    // todo: 开一个线程一直去监听歌曲是否播放完毕和播放列表是否为空


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer(baseContext)
    }

    /**
     * 1.怎么绑定的
     *    1.songList单击过来的
     *    2.底部播放栏点击过来的
     *    3.播放页点击按钮过来的
     * 2.到这儿传的数据是什么
     *    1.歌曲信息，通知要播放的歌曲
     *    2.添加歌曲到队列
     *    3.要执行的动作：播放、暂停、下一首/上一首、指定播放位置
     *  3.其他地方需要用到歌曲实时的状态
     *    1.歌词、播放进度、是否正在播放......
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onBind(intent: Intent): IBinder {
        val type = intent.getIntExtra(PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
        when (type) {
            ADD_SONG -> {
                // 添加歌曲
                val song = intent.getParcelableExtra("song", Song::class.java)
                playList?.offer(song)
                // todo: 还要继续判断当前是否有正在播放的歌曲
            }

            REMOVE_SONG -> {
                // todo: 优化，只需传递队列索引...
                val song = intent.getParcelableExtra("song", Song::class.java)
                playList?.remove(song)
            }

            IS_PLAYING -> {
                mediaPlayer.apply {
                    reset() // 重置
                    mediaPlayer = MediaPlayer.create(baseContext, R.raw.test1)
                    mediaPlayer.start()
                    mediaPlayer.isLooping = true
                }
            }
        }


        return binder
    }


    override fun onUnbind(intent: Intent?): Boolean {
        playList?.clear()
        playList = null
        return super.onUnbind(intent)
    }

    // 返回播放状态
    fun getPlayStatus() = mediaPlayer.isPlaying

    // 添加歌曲
    fun addSong() {}

    // 获取歌曲信息
    fun getSong(): Song {
        TODO("return song's detail")
    }

    // 下一曲
    fun nextSong() {}

    // 上一曲
    fun preSong() {}

    // 播放或暂停 todo: 传递参数 判断是播放还是暂停
    fun startOrPause() {}


    inner class MusicBinder : Binder() {
        // 调用service中的方法
        fun callGetPlayStatus(): Boolean = getPlayStatus()
    }
}