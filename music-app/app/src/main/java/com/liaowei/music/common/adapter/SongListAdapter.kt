package com.liaowei.music.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liaowei.music.R
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.main.model.Song

class SongListAdapter(private val songList: List<Song>, private val flag: Int) :
    RecyclerView.Adapter<SongListAdapter.SongListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): SongListAdapter.SongListViewHolder {
        return SongListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_song_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongListAdapter.SongListViewHolder, position: Int) {
        val song = songList[position]
        holder.songCoverImg.setImageResource(song.img)
        holder.songName.text = song.name
        holder.songSinger.text = if (song.singerId == 1L) "周杰伦" else "蔡徐坤"

        when(flag) {
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
        }
    }

    override fun getItemCount(): Int = songList.size

    inner class SongListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songCoverImg = itemView.findViewById<ImageView>(R.id.common_song_cover_img)
        val songName = itemView.findViewById<TextView>(R.id.common_song_name)
        val songSinger = itemView.findViewById<TextView>(R.id.common_song_singer)
        val isLike = itemView.findViewById<ImageView>(R.id.common_is_like)
        val download = itemView.findViewById<ImageView>(R.id.common_download)
    }
}