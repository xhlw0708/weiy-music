package com.liaowei.music.main.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.liaowei.music.R
import com.liaowei.music.common.adapter.SongListAdapter
import com.liaowei.music.common.constant.FragmentFlag
import com.liaowei.music.databinding.FragmentHomeBinding
import com.liaowei.music.main.model.Song

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val viewModel: HomeViewModel by viewModels()


    companion object {
        fun newInstance() = HomeFragment()
    }


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
        // 给音乐榜绑定单击事件
        binding.homeSpecialColumnRanking.setOnClickListener{
            val mainTabLayout = activity?.findViewById<TabLayout>(R.id.main_tab_layout)
            mainTabLayout?.getTabAt(1)?.select() // TODO("改为常量") 跳转到乐馆
        }

        val songList: ArrayList<Song> = ArrayList()
        songList.add(Song(1, "爱的飞行日记", 1, R.drawable.jay1, 1))
        songList.add(Song(2, "爱的飞行日记", 1, R.drawable.jay1, 1))
        songList.add(Song(3, "爱的飞行日记", 1, R.drawable.jay1, 1))
        songList.add(Song(4, "爱的飞行日记", 1, R.drawable.jay1, 1))
        songList.add(Song(5, "只因你太美", 2, R.drawable.ikun1, 1))
        songList.add(Song(6, "Hug Me", 2, R.drawable.ikun1, 1))
        // 加载“大家都在听”数据
        binding.homeListenRv.adapter = SongListAdapter(songList, FragmentFlag.HOME_FRAGMENT)
        binding.homeListenRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
}