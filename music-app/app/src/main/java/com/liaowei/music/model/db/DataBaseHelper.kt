package com.liaowei.music.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.liaowei.music.common.constant.DBConstant.Companion.DB_NAME
import com.liaowei.music.common.constant.DBConstant.Companion.VERSION

class DataBaseHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, VERSION) {

    companion object {
        const val CREATE_TABLE_SQL = "create table song(id long primary key autoincrement, " +
                "name varchar(20), " +
                "singerId long, " +
                "singerName varchar(20), " +
                "img int, " +
                "resourceId int, " +
                "playNumber int, " +
                "isLike int)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }


}