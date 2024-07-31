package com.liaowei.music.model.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.liaowei.music.common.constant.DBConstant.Companion.TABLE_NAME
import com.liaowei.music.model.db.DataBaseHelper


class MusicContentProvider : ContentProvider() {

    companion object {
        private const val SONG_DIR: Int = 0
        private const val SONG_ITEM: Int = 1
        private const val AUTHORITY: String = "com.liaowei.music.provider"
        private lateinit var uriMatcher: UriMatcher
        private lateinit var db: SQLiteDatabase
        val SONG_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    init {
        // uriMatcher 进⾏初始化，将期望匹配的集中uri加⼊
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(AUTHORITY, TABLE_NAME, SONG_DIR)
        uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", SONG_ITEM)
    }

    override fun onCreate(): Boolean {
        val helper = DataBaseHelper(context!!)
        db = helper.writableDatabase
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor {
        val cursor: Cursor = db.query(
            TABLE_NAME,
            projection, selection, selectionArgs,
            null, null, null,
            sortOrder
        )
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        var returnUri: Uri = uri
        when(uriMatcher.match(uri)) {
            SONG_DIR -> {
                val id = db.insert(TABLE_NAME, null, values)
                returnUri = ContentUris.withAppendedId(uri, id) // 将id添加到uri中

            }
        }
        return returnUri
    }

    override fun getType(uri: Uri): String? {
        when (uriMatcher.match(uri)) {
            SONG_DIR -> return "vnd.android.cursor.dir/vnd.com.liaowei.music.provider.song"
            SONG_ITEM -> return "vnd.android.cursor.item/vnd.com.liaowei.music.provider.song"
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }


    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}