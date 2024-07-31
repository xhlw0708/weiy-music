package com.liaowei.music.common.constant

interface DBConstant {
    companion object {
        const val DB_NAME: String = "music.db" // 库名字
        const val TABLE_NAME: String = "song" // 表名字
        const val VERSION: Int = 1 // 版本号
        const val IS_UPDATE_LOCAL_MUSIC: String = "localMusicToDB" // 更新本地音乐到数据库标识
    }
}