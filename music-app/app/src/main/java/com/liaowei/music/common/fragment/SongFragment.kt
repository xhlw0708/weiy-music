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
import com.liaowei.music.model.domain.Song
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SongFragment : Fragment() {

    private val binding: FragmentSongBinding by lazy { FragmentSongBinding.inflate(layoutInflater) }
    private val viewModel: SongViewModel by viewModels()
    private val songList: ArrayList<Song> = ArrayList()
    companion object {
        fun newInstance() = SongFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        // 注册eventBus
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    //接收事件
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true, priority = 1)
    fun onReceiveMsg(message: EventMessage<String>){
        Log.e("EventBus_Subscriber", "onReceiveMsg_POSTING: $message");
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.commonSongListRv.adapter = SongListAdapter(this, songList, PageFlag.SONG_FRAGMENT)
        binding.commonSongListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消注册eventBus
        EventBus.getDefault().unregister(this)
    }

}