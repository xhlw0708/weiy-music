package com.liaowei.music.main.model

data class Song(
    val id: Long,
    val name: String, // 歌曲名称
    val singerId: Long, // 歌手id
    val img: Int, // 歌曲封面图
    val resourceId: Int, // 资源id
    var isLike: Int = 0 // 是否喜欢，默认0-不喜欢，1-喜欢
)
