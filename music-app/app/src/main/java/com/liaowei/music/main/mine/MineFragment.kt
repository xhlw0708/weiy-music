package com.liaowei.music.main.mine

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.liaowei.music.SongListActivity
import com.liaowei.music.R
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.databinding.FragmentMineBinding
import com.liaowei.music.main.mine.adapter.RecentPlayAdapter
import com.liaowei.music.model.domain.Song
import com.liaowei.music.model.provider.MusicContentProvider.Companion.SONG_CONTENT_URI
import java.util.stream.Collectors.toList


class MineFragment : Fragment() {

    private val binding: FragmentMineBinding by lazy { FragmentMineBinding.inflate(layoutInflater) }
    companion object {
        fun newInstance() = MineFragment()
        var songList: ArrayList<Song> = ArrayList()
    }
    private lateinit var viewModel: MineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 获取最近播放的歌曲id
        val recentPlayMap: MutableMap<String, *>? = requireActivity().getSharedPreferences("recentPlay", MODE_PRIVATE).all
        // 按时间排序
        val recentPlayIds = recentPlayMap?.entries
            ?.sortedByDescending { it.key } // 按照key降序排列
            ?.take(6) // 只取前6个
            ?.map { it.value } // 获取value
            ?.toList() // 返回list
        // 根据ids查询数据
        recentPlayIds?.map {
            val cursor = requireActivity().contentResolver.query(SONG_CONTENT_URI, null, "id = ?" , Array(1){ it.toString() }, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val singerName = cursor.getString(cursor.getColumnIndexOrThrow("singerName"))
                    val path = cursor.getString(cursor.getColumnIndexOrThrow("resourceId"))
                    songList.add(Song(name, singerName, path))
                }
                cursor.close()
            }
        }
        // 设置rv
        binding.mineRecentPlayRv.adapter = RecentPlayAdapter(songList)
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.mineRecentPlayRv.layoutManager = layoutManager
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MineViewModel::class.java]
        // 绑定喜欢单击事件
        binding.toLikeBtn.setOnClickListener{
            // 查询喜欢歌曲列表
            val tmpList = ArrayList<Song>()
            val cursor = requireActivity().contentResolver.query(SONG_CONTENT_URI, null, "isLike = ?" , Array(1){ "1" }, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val singerName = cursor.getString(cursor.getColumnIndexOrThrow("singerName"))
                    val path = cursor.getString(cursor.getColumnIndexOrThrow("resourceId"))
                    val song = Song(name, singerName, path)
                    song.id = id
                    tmpList.add(song)
                }
                cursor.close()
            }
            songList = tmpList
            val intent = Intent(requireContext(), SongListActivity::class.java)
            intent.putParcelableArrayListExtra("songList", songList)
            intent.putExtra("songTopTitle", getString(R.string.content_description_like))
            intent.putExtra("flag", PageFlag.LIKE_SONG_LIST_ACTIVITY)
            viewModel.setFragment(this)
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