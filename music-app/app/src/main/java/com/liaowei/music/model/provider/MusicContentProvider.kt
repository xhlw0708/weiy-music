package com.liaowei.music.model.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.liaowei.music.model.db.DataBaseHelper


class MusicContentProvider : ContentProvider() {

    companion object {
        private const val SONG_DIR: Int = 0
        private const val SONG_ITEM: Int = 1
        private const val AUTHORITY: String = "com.liaowei.music.provider"
        private lateinit var uriMatcher: UriMatcher
        private lateinit var db: SQLiteDatabase
    }

    init {
        // uriMatcher 进⾏初始化，将期望匹配的集中uri加⼊
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(AUTHORITY, "song", SONG_DIR)
        uriMatcher.addURI(AUTHORITY, "song/#", SONG_ITEM)
    }

    override fun onCreate(): Boolean {
        val helper = DataBaseHelper(context!!)
        db = helper.writableDatabase
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        TODO("Implement this to handle query requests from clients.")
    }

    override fun getType(uri: Uri): String? {
        when(uriMatcher.match(uri)) {
            SONG_DIR -> return "vnd.android.cursor.dir/vnd.com.liaowei.music.provider.song"
            SONG_ITEM -> return "vnd.android.cursor.item/vnd.com.liaowei.music.provider.song"
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Implement this to handle requests to insert a new row.")
    }


    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}