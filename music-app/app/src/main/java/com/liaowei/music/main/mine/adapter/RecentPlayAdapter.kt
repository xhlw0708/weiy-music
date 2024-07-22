package com.liaowei.music.main.mine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liaowei.music.R
import com.liaowei.music.main.model.Song

class RecentPlayAdapter(private val songList: List<Song>): RecyclerView.Adapter<RecentPlayAdapter.RecentPlayViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentPlayAdapter.RecentPlayViewHolder {
        return RecentPlayViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_recent_play_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecentPlayAdapter.RecentPlayViewHolder, position: Int) {
        val song = songList[position]
        holder.coverImg.setImageResource(song.img)
        holder.playName.text = song.name
        holder.playSinger.text = if (song.singerId == 1L) "周杰伦" else "蔡徐坤"
    }

    override fun getItemCount(): Int = songList.size

    inner class RecentPlayViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val coverImg: ImageView = itemView.findViewById(R.id.mine_recent_play_img)
        val playName: TextView = itemView.findViewById(R.id.mine_recent_play_name)
        val playSinger: TextView = itemView.findViewById(R.id.mine_recent_play_singer)
    }
}