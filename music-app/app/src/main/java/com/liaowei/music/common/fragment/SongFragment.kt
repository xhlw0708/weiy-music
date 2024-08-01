package com.liaowei.music.common.fragment


import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.liaowei.music.R
import com.liaowei.music.common.adapter.SongListAdapter
import com.liaowei.music.common.constant.PageFlag
import com.liaowei.music.databinding.FragmentSongBinding
import com.liaowei.music.event.EventMessage
import com.liaowei.music.main.hall.HallFragment.Companion.classifyList
import com.liaowei.music.model.domain.Song
import com.liaowei.music.model.provider.MusicContentProvider.Companion.SONG_CONTENT_URI
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SongFragment : Fragment() {

    private val binding: FragmentSongBinding by lazy { FragmentSongBinding.inflate(layoutInflater) }
    private val viewModel: SongViewModel by viewModels()
    private var songList: ArrayList<Song> = ArrayList()
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
        // 注册eventBus
        EventBus.getDefault().register(this)
        return binding.root
    }

    //接收事件
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true, priority = 1)
    fun onReceiveMsg(message: EventMessage<ArrayList<Song>>){
        Log.e("EventBus_Subscriber", "onReceiveMsg_POSTING: $message");
        songList = message.message
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (songList.isEmpty()) {
            // 根据分类查询
            val cursor = requireActivity()
                .contentResolver
                .query(SONG_CONTENT_URI, null, "category = ?", Array(1){ classifyList[0] }, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name")) // 歌曲名称
                    val resourceId = cursor.getString(cursor.getColumnIndexOrThrow("resourceId")) // 文件路径
                    val singerName = cursor.getString(cursor.getColumnIndexOrThrow("singerName")) // 歌手名称
                    val isLike = cursor.getInt(cursor.getColumnIndexOrThrow("isLike")) // 喜欢状态
                    val song = Song(name, singerName, resourceId, isLike)
                    song.id = id
                    songList.add(song)
                }
                cursor.close()
            }
        }
        binding.commonSongListRv.adapter = SongListAdapter(this, songList, PageFlag.SONG_FRAGMENT)
        binding.commonSongListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消注册eventBus
        EventBus.getDefault().unregister(this)
    }

}