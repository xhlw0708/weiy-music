package com.liaowei.music.model.domain

import android.os.Parcel
import android.os.Parcelable

data class Singer(
    val id: Long, // id
    val name: String, // 名字
    val img: Int // 图片
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeInt(img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Singer> {
        override fun createFromParcel(parcel: Parcel): Singer {
            return Singer(parcel)
        }

        override fun newArray(size: Int): Array<Singer?> {
            return arrayOfNulls(size)
        }
    }
}
