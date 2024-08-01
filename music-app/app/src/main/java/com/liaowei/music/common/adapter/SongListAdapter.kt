package com.liaowei.music.common.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.liaowei.music.R
import com.liaowei.music.common.constant.MusicConstant.Companion.UPDATE_PLAYING_FLAG
import com.liaowei.music.common.constant.MusicConstant.Companion.UPDATE_PLAYING_TAB
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.model.domain.Song

class SongListAdapter(val context: Context?, private val songList: List<Song>, private val flag: Int) :
    RecyclerView.Adapter<SongListAdapter.SongListViewHolder>() {

    companion object {
        private val retriever = MediaMetadataRetriever()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): SongListAdapter.SongListViewHolder {
        return SongListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_song_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongListAdapter.SongListViewHolder, position: Int) {
        val song = songList[position]

        // 处理封面
        retriever.setDataSource(song.resourceId)
        val embeddedPicture = retriever.embeddedPicture // 获取音频内嵌图片
        if (embeddedPicture == null) {
            holder.songCoverImg.setImageResource(R.drawable.default_audio)
        }else{
            val bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size)
            holder.songCoverImg.setImageBitmap(bitmap)
        }

        holder.songName.text = song.name
        holder.songSinger.text = song.singerName
        holder.isLike.visibility = View.GONE
        holder.download.visibility = View.GONE
        /*when(flag) {
            PageFlag.HOME_FRAGMENT,
            PageFlag.MORE_SONG_LIST_ACTIVITY -> {
                holder.isLike.visibility = View.GONE
                holder.download.visibility = View.GONE
            }
            PageFlag.LIKE_SONG_LIST_ACTIVITY -> {
                holder.isLike.visibility = View.GONE
            }
            else -> {
                holder.isLike.setOnClickListener{
                    song.isLike = if (song.isLike == 0) 1 else 0
                    if (song.isLike == 0) holder.isLike.setImageResource(R.drawable.favorite_border)
                    else holder.isLike.setImageResource(R.drawable.favorite_normal)
                }
            }
        }*/

        holder.songLayout.setOnClickListener{
            val intent = Intent("com.liaowei.music.broadcast.MusicBroadcast")
            intent.putExtra(UPDATE_PLAYING_FLAG, UPDATE_PLAYING_TAB)
            intent.putExtra("song", song)
            val localBroadcastManager = LocalBroadcastManager.getInstance(context!!)
            localBroadcastManager.sendBroadcast(intent)
        }
    }

    override fun getItemCount(): Int = songList.size

    inner class SongListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songLayout: LinearLayout = itemView.findViewById(R.id.song_layout)
        val songCoverImg: ImageView = itemView.findViewById(R.id.common_song_cover_img)
        val songName: TextView = itemView.findViewById(R.id.common_song_name)
        val songSinger: TextView = itemView.findViewById(R.id.common_song_singer)
        val isLike: ImageView = itemView.findViewById(R.id.common_is_like)
        val download: ImageView = itemView.findViewById(R.id.common_download)
    }
}