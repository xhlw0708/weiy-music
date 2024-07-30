package com.liaowei.music.main.mine

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.liaowei.music.SongListActivity
import com.liaowei.music.R
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.databinding.FragmentMineBinding
import com.liaowei.music.main.mine.adapter.RecentPlayAdapter
import com.liaowei.music.model.domain.Song


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
        songList.add(Song(1, "爱的飞行日记", 1, R.drawable.jay1, 1, 0))
        songList.add(Song(2, "爱的飞行日记", 1, R.drawable.jay1, 1, 0))
        songList.add(Song(3, "爱的飞行日记", 1, R.drawable.jay1, 1, 0))
        songList.add(Song(4, "爱的飞行日记", 1, R.drawable.jay1, 1, 0))
        songList.add(Song(5, "只因你太美", 2, R.drawable.ikun1, 1, 0))
        songList.add(Song(6, "Hug Me", 2, R.drawable.ikun1, 1, 0))

        // 设置rv
        binding.mineRecentPlayRv.adapter = RecentPlayAdapter(songList)
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.mineRecentPlayRv.layoutManager = layoutManager
        
        // 绑定喜欢单击事件
        binding.toLikeBtn.setOnClickListener{
            val intent = Intent(requireContext(), SongListActivity::class.java)
            intent.putParcelableArrayListExtra("songList", songList)
            intent.putExtra("songTopTitle", getString(R.string.content_description_like))
            intent.putExtra("flag", PageFlag.LIKE_SONG_LIST_ACTIVITY)
            startActivity(intent)
        }

        // 绑定更多单击事件
        binding.toMoreBtn.setOnClickListener{
            val intent = Intent(requireContext(), SongListActivity::class.java)
            intent.putParcelableArrayListExtra("songList", songList)
            intent.putExtra("songTopTitle", getString(R.string.more_info))
            intent.putExtra("flag", PageFlag.MORE_SONG_LIST_ACTIVITY)
            startActivity(intent)
        }

    }
}