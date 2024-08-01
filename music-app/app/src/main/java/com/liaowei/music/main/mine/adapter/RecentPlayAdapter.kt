package com.liaowei.music.main.mine.adapter

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liaowei.music.R
import com.liaowei.music.model.domain.Song

class RecentPlayAdapter(private val songList: List<Song>): RecyclerView.Adapter<RecentPlayAdapter.RecentPlayViewHolder>() {

    companion object {
        private val retriever = MediaMetadataRetriever()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentPlayAdapter.RecentPlayViewHolder {
        return RecentPlayViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_recent_play_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecentPlayAdapter.RecentPlayViewHolder, position: Int) {
        val song = songList[position]
        retriever.setDataSource(song.resourceId)
        val embeddedPicture = retriever.embeddedPicture
        if (embeddedPicture != null) {
            val bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size)
            holder.coverImg.setImageBitmap(bitmap)
        } else{
            holder.coverImg.setImageResource(R.drawable.playing_music)
        }

        holder.playName.text = song.name
        holder.playName.requestFocus() // 请求焦点
        holder.playSinger.text = song.singerName
    }

    override fun getItemCount(): Int = songList.size

    inner class RecentPlayViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val coverImg: ImageView = itemView.findViewById(R.id.mine_recent_play_img)
        var playName: TextView = itemView.findViewById(R.id.mine_recent_play_name)
        val playSinger: TextView = itemView.findViewById(R.id.mine_recent_play_singer)
    }
}