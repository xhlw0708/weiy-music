package com.liaowei.music.common.fragment


import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.liaowei.music.R
import com.liaowei.music.common.adapter.SongListAdapter
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.databinding.FragmentSongBinding
import com.liaowei.music.main.model.Song

class SongFragment : Fragment() {

    private val binding: FragmentSongBinding by lazy { FragmentSongBinding.inflate(layoutInflater) }
    private val viewModel: SongViewModel by viewModels()

    companion object {
        fun newInstance() = SongFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songList: ArrayList<Song> = ArrayList()
        songList.add(Song(1, "爱的飞行日记", 1, R.drawable.jay1, 1, 0))
        songList.add(Song(2, "爱的飞行日记", 1, R.drawable.jay1, 1, 0))
        songList.add(Song(3, "爱的飞行日记", 1, R.drawable.jay1, 1, 0))
        songList.add(Song(4, "爱的飞行日记", 1, R.drawable.jay1, 1, 0))
        songList.add(Song(5, "只因你太美", 2, R.drawable.ikun1, 1, 0))
        songList.add(Song(6, "Hug Me", 2, R.drawable.ikun1, 1, 0))
        binding.commonSongListRv.adapter = SongListAdapter(this, songList, PageFlag.SONG_FRAGMENT)
        binding.commonSongListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

}