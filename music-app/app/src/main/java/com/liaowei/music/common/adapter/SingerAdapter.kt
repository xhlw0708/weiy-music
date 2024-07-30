package com.liaowei.music.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liaowei.music.R
import com.liaowei.music.model.domain.Singer


class SingerAdapter(private val singerList: List<Singer>) :
    RecyclerView.Adapter<SingerAdapter.SingerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingerViewHolder {
        return SingerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_singer_item, parent, false)
        )
    }

    override fun getItemCount(): Int = singerList.size

    override fun onBindViewHolder(holder: SingerViewHolder, position: Int) {
        val singer = singerList[position]
        holder.singerImg.setImageResource(singer.img)
        /*Glide.with(holder.itemView)
            .load(singer.img)
            .circleCrop()
            .into(holder.singerImg)*/

        holder.singerName.text = singer.name
    }


    inner class SingerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val singerImg = itemView.findViewById<ImageView>(R.id.singer_img)
        val singerName = itemView.findViewById<TextView>(R.id.singer_name)
    }
}