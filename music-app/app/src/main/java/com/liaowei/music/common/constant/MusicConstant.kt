package com.liaowei.music.common.constant

interface MusicConstant {
    companion object {
        const val PLAYING_FLAG = "PLAYING" // 播放标志
        const val UPDATE_PLAYING_FLAG = "UPDATE_PLAYING" // 更新播放栏标志
        const val DEFAULT_MUSIC_TYPE = 0 // 音乐标志
        const val NEXT_SONG = 1 // 下一首
        const val PREVIOUS_SONG = 2 // 上一首
        const val GET_PROGRESS = 3 // 获取进度
        const val ADD_SONG = 4 // 添加歌曲
        const val REMOVE_SONG = 5 // 删除歌曲
        const val IS_PLAYING = 6 // 播放
        const val IS_PAUSE = 7 // 暂停
        const val UPDATE_PLAYING_TAB = 8 // 更新播放栏UI
    }
}