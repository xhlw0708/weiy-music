package com.liaowei.music.model.domain

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Song(
    var id: Int?,
    var name: String, // 歌曲名称
    var singerId: Int, // 歌手id
    var singerName: String, // 歌手名称
    var img: Int, // 歌曲封面图
    var resourceId: String, // 资源
    var playNumber: Int = 0, // 播放次数
    var isLike: Int = 0, // 是否喜欢，默认0-不喜欢，1-喜欢
    var category: String // 分类
) : Serializable {


    constructor(name: String, singerName: String, resourceId: String) :
            this(null, name, 0, singerName, 0, resourceId, 0, 0, "")


}
