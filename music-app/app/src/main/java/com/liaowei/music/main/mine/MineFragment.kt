package com.liaowei.music.main.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.liaowei.music.R
import com.liaowei.music.databinding.FragmentMineBinding
import com.liaowei.music.main.mine.adapter.RecentPlayAdapter
import com.liaowei.music.main.model.Song


class MineFragment : Fragment() {

    private val binding: FragmentMineBinding by lazy { FragmentMineBinding.inflate(layoutInflater) }

    companion object {
        fun newInstance() = MineFragment()
    }

    private val viewModel: MineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*val songList: List<Song> = listOf(Song(1, "爱的飞行日记", 1, R.drawable.jay1, 1),
            Song(2, "只因你太美", 2, R.drawable.ikun1, 2))*/
        // val songList: List<Song> = listOf(Song(1, "爱的飞行日记", 1, R.drawable.jay1, 1))
        val songList: ArrayList<Song> = ArrayList()
        songList.add(Song(1, "爱的飞行日记", 1, R.drawable.jay1, 1))
        songList.add(Song(2, "爱的飞行日记", 1, R.drawable.jay1, 1))
        songList.add(Song(3, "爱的飞行日记", 1, R.drawable.jay1, 1))
        songList.add(Song(4, "爱的飞行日记", 1, R.drawable.jay1, 1))
        songList.add(Song(5, "只因你太美", 2, R.drawable.ikun1, 1))
        songList.add(Song(6, "Hug Me", 2, R.drawable.ikun1, 1))

        binding.mineRecentPlayRv.adapter = RecentPlayAdapter(songList)
        // val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.mineRecentPlayRv.layoutManager = layoutManager
    }
}