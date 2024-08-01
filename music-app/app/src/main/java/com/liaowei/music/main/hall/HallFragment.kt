package com.liaowei.music.main.hall

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.liaowei.music.common.adapter.FragmentViewPagerAdapter
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.common.fragment.SongFragment
import com.liaowei.music.databinding.FragmentHallBinding
import com.liaowei.music.event.EventMessage
import com.liaowei.music.model.domain.Song
import com.liaowei.music.model.provider.MusicContentProvider.Companion.SONG_CONTENT_URI
import org.greenrobot.eventbus.EventBus

class HallFragment : Fragment() {

    private val binding: FragmentHallBinding by lazy { FragmentHallBinding.inflate(layoutInflater) }

    companion object {
        fun newInstance() = HallFragment()
        val classifyList: List<String> = listOf("华语", "流行", "摇滚", "民谣", "电子", "网络流行", "日语", "英语",)
    }

    private val viewModel: HallViewModel by viewModels()

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
        // 添加fragment
        val fragmentList = mutableListOf<Fragment>()
        for (i in classifyList.indices) {
            fragmentList.add(SongFragment.newInstance())
        }
        // 设置viewPager
        val fragmentViewPagerAdapter = FragmentViewPagerAdapter(this, fragmentList)
        binding.hallViewPager.adapter = fragmentViewPagerAdapter

        // 动态自定义Tab
        TabLayoutMediator(
            binding.hallTabLayout,
            binding.hallViewPager
        ) { tab: TabLayout.Tab, position: Int ->
            // 自定义TabView
            val tabView = TextView(context)
            tabView.text = classifyList[position]
            tabView.setGravity(Gravity.CENTER)
            tabView.textSize = 20f

            // 将tabItem绑定到tab
            tab.setCustomView(tabView)
        }.attach()

        // 绑定tab选择事件
        binding.hallViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                getCategoryListAndPush(position)
            }
        })
    }

    fun getCategoryListAndPush(position: Int) {
        val songList: ArrayList<Song> = ArrayList()
        // 根据分类查询
        val cursor = requireActivity()
            .contentResolver
            .query(SONG_CONTENT_URI, null, "category = ?", Array(1){ classifyList[position] }, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name")) // 歌曲名称
                val resourceId = cursor.getString(cursor.getColumnIndexOrThrow("resourceId")) // 文件路径
                val singerName = cursor.getString(cursor.getColumnIndexOrThrow("singerName")) // 歌手名称
                val song = Song(name, singerName, resourceId)
                song.id = id
                songList.add(song)
            }
            cursor.close()
        }
        EventBus.getDefault().post(EventMessage(position, songList))
    }
}