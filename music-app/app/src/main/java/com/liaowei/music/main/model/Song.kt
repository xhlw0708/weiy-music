package com.liaowei.music.main.model

import android.os.Parcel
import android.os.Parcelable

data class Song(
    val id: Long,
    val name: String, // 歌曲名称
    val singerId: Long, // 歌手id
    val img: Int, // 歌曲封面图
    val resourceId: Int, // 资源id
    var playNumber: Int, // 播放次数
    var isLike: Int = 0 // 是否喜欢，默认0-不喜欢，1-喜欢
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeLong(singerId)
        parcel.writeInt(img)
        parcel.writeInt(resourceId)
        parcel.writeInt(isLike)
        parcel.writeInt(playNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}
