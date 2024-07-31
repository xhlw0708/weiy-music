package com.liaowei.music.main.home

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.liaowei.music.R
import com.liaowei.music.SingerActivity
import com.liaowei.music.common.adapter.SongListAdapter
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.databinding.FragmentHomeBinding
import com.liaowei.music.main.home.carousel.Carousel
import com.liaowei.music.model.domain.Singer
import com.liaowei.music.model.domain.Song
import com.liaowei.music.model.provider.MusicContentProvider.Companion.SONG_CONTENT_URI


class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var carousel: Carousel

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
            val mainTabLayout = activity?.findViewById<TabLayout>(com.liaowei.music.R.id.main_tab_layout)
            mainTabLayout?.getTabAt(1)?.select() // TODO("改为常量") 跳转到乐馆
        }

        val songList: ArrayList<Song> = ArrayList()
        // 加载数据库歌曲列表
        val cursor =
            requireActivity().contentResolver.query(SONG_CONTENT_URI, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val singerName = cursor.getString(cursor.getColumnIndexOrThrow("singerName"))
                val path = cursor.getString(cursor.getColumnIndexOrThrow("resourceId"))
                val song = Song(name, singerName, path)
                songList.add(song)
            }
            cursor.close()
        }

        // 加载“大家都在听”数据
        binding.homeListenRv.adapter = SongListAdapter(this, songList, PageFlag.HOME_FRAGMENT)
        binding.homeListenRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val singerList: ArrayList<Singer> = arrayListOf(
            Singer(1, "周杰伦", R.drawable.jay1),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(1, "周杰伦", R.drawable.jay1),
            Singer(1, "周杰伦", R.drawable.jay1),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(1, "周杰伦", R.drawable.jay1),
            Singer(1, "周杰伦", R.drawable.jay1),
            Singer(2, "蔡徐坤", R.drawable.ikun5),
            Singer(2, "蔡徐坤", R.drawable.ikun5)
        )
        // 绑定歌手单击事件
        binding.homeSpecialColumnSinger.setOnClickListener {
            val intent = Intent(requireContext(), SingerActivity::class.java)
            intent.putParcelableArrayListExtra("singerList", singerList)
            startActivity(intent)
        }

        // 轮播图
        carousel = Carousel(context, binding.homeCarouselDot, binding.homeCarouselViewPager)
        val ids = intArrayOf(
            R.drawable.ikun1,
            R.drawable.ikun2,
            R.drawable.ikun3,
            R.drawable.ikun4,
        )
        carousel.initViews(ids)
        carousel.startAutoScroll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        carousel.stopAutoScroll()
    }
}