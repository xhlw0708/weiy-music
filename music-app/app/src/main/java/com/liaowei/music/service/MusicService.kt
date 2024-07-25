package com.liaowei.music.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaDataSource
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Build.VERSION_CODES.P
import android.os.IBinder
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Toast
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

    companion object {
        private val mediaPlayer: MediaPlayer = MediaPlayer()
        private var index = 0 // 记录播放的索引
    }

    init {
        mediaPlayer.setOnCompletionListener {
            // 检查播放列表是否为空，为空则停止播放，并释放资源
            if (playList?.isEmpty() == true) {
                mediaPlayer.stop()
                // todo： 让底部播放栏停止播放，播放页面设置暂停按钮
            } else {
                // 下一首
                nextSong()
            }
        }
    }

    // 维护一个播放队列
    private var playList: LinkedList<Song>? = LinkedList()
    // todo: 开一个线程一直去监听歌曲是否播放完毕和播放列表是否为空



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
    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onBind(intent: Intent): IBinder {
        val type = intent.getIntExtra(PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
        val song = intent.getParcelableExtra("song", Song::class.java)
        when (type) {
            ADD_SONG -> {
                // 添加歌曲
                playList?.offer(song)
                // todo: 还要继续判断当前是否有正在播放的歌曲
            }

            REMOVE_SONG -> {
                // todo: 优化，只需传递队列索引...
                playList?.remove(song)
            }

            IS_PLAYING -> {
                mediaPlayer.apply {
                    mediaPlayer.start()
                }
            }

            else -> {
                // 没有在播放，先将歌曲添加到playList中，再播放第一首歌
                if (!mediaPlayer.isPlaying) {
                    playList?.push(song)
                    val afd: AssetFileDescriptor = resources.openRawResourceFd(song!!.resourceId)
                    mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } else{
                    // 正在播放，只需要添加到队列即可
                    playList?.push(song)
                }
            }
        }
        return binder
    }


    override fun onUnbind(intent: Intent?): Boolean {
        playList?.clear()
        playList = null
        mediaPlayer.release()
        return super.onUnbind(intent)
    }

    // 返回播放状态
    fun getPlayStatus() = mediaPlayer.isPlaying

    // 添加歌曲
    fun addSong(song: Song) {
        playList?.push(song)
        index = 0
        // 播放歌曲
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            val afd: AssetFileDescriptor = resources.openRawResourceFd(song.resourceId)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
        }
        startOrPause(true)
    }

    // 获取歌曲信息
    fun getSong(): Song {
        TODO("return song's detail")
    }

    // 下一曲
    private fun nextSong() {
        mediaPlayer.reset()
        if (playList?.size!! > index + 1) {
            val afd: AssetFileDescriptor = resources.openRawResourceFd(playList?.get(index + 1)?.resourceId!!)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } else if (playList?.size!! == index + 1) {
            // 已经播放了最后一首，开始播放第一首
            index = 0
            val afd: AssetFileDescriptor = resources.openRawResourceFd(playList?.get(index)?.resourceId!!)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            Toast.makeText(baseContext, "已经是最后一首了", Toast.LENGTH_SHORT).show()
        }
        index++
    }


    // 上一曲
    private fun preSong() {
        mediaPlayer.reset()
        if (index == 0) {
            // 正在播放第一首
            Toast.makeText(baseContext, "已经是第一首了", Toast.LENGTH_SHORT).show()
        }
        if (index > 0) {
            index--
            val afd: AssetFileDescriptor = resources.openRawResourceFd(playList?.get(index)?.resourceId!!)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }


    // 播放或暂停
    private fun startOrPause(isPlay: Boolean) {
        if (isPlay) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
        }
    }


    inner class MusicBinder : Binder() {
        // 获取播放器的状态
        fun callGetPlayStatus(): Boolean = getPlayStatus()
        // 开关播放器
        fun callsStartOrPause(isPlay: Boolean){
            startOrPause(isPlay)
        }
        // 添加歌曲
        fun callAddSong(song: Song){
            addSong(song)
        }
        // 下一曲
        fun callNextSong(){
            nextSong()
        }
        // 上一曲
        fun callPreSong(){
            preSong()
        }
        // 是否最后一首
        fun callIsLastSong(): Boolean = index + 1 == playList?.size
        // 是否第一首
        fun callIsFirstSong(): Boolean = index == 0
    }
}