package com.liaowei.music.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.liaowei.music.R
import com.liaowei.music.common.constant.MusicConstant.Companion.DEFAULT_MUSIC_TYPE
import com.liaowei.music.common.constant.MusicConstant.Companion.UPDATE_PLAYING_FLAG
import com.liaowei.music.common.constant.MusicConstant.Companion.UPDATE_PLAYING_TAB
import com.liaowei.music.main.model.Song

class MusicReceiver(private val handler: Handler) : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getIntExtra(UPDATE_PLAYING_FLAG, DEFAULT_MUSIC_TYPE)
        when(type) {
            UPDATE_PLAYING_TAB -> {
                val song = intent.getParcelableExtra("song", Song::class.java)
                val message = Message.obtain()
                message.data.putInt("img", song?.img ?: 0)
                message.data.putString("name", song?.name ?: "网络歌手")
                message.data.putLong("singer", song?.singerId ?: 1)
                message.data.putInt("playSong", R.raw.test3) // todo: 添加歌曲
                handler.sendMessage(message)
            }
        }
    }
}