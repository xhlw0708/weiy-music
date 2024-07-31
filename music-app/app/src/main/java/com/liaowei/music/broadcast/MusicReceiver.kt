package com.liaowei.music.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import androidx.annotation.RequiresApi
import com.liaowei.music.R
import com.liaowei.music.common.constant.MusicConstant.Companion.DEFAULT_MUSIC_TYPE
import com.liaowei.music.common.constant.MusicConstant.Companion.UPDATE_PLAYING_FLAG
import com.liaowei.music.common.constant.MusicConstant.Companion.UPDATE_PLAYING_TAB
import com.liaowei.music.model.domain.Song

class MusicReceiver(private val handler: Handler) : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getIntExtra(UPDATE_PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
        when(type) {
            UPDATE_PLAYING_TAB -> {
                val song = intent.getParcelableExtra("song", Song::class.java)
                val message = Message.obtain()
                message.data.putString("name", song?.name ?: "网络热歌")
                message.data.putString("singerName", song?.singerName ?: "网络歌手")
                message.data.putString("path", song?.resourceId)
                handler.sendMessage(message)
            }
        }
    }
}