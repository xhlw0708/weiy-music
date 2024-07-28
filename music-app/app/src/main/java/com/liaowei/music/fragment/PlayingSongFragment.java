package com.liaowei.music.fragment;

import static com.liaowei.music.common.constant.MusicConstant.DEFAULT_MUSIC_TYPE;
import static com.liaowei.music.service.MusicService.GET_SONG_STATE_MSG;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.liaowei.music.R;
import com.liaowei.music.databinding.FragmentPlayingSongBinding;
import com.liaowei.music.fragment.PlayingSongViewModel;
import com.liaowei.music.main.model.Song;
import com.liaowei.music.service.MusicService;

public class PlayingSongFragment extends Fragment {

    private FragmentPlayingSongBinding binding;
    private static boolean bound = false;
    private static boolean progress = false;
    private MusicService.MusicBinder musicBinder;
    private MusicService musicService;
    private Handler initHandler = new Handler(Looper.getMainLooper());
    private Messenger serviceMessenger;
    private Messenger clentMessenger = new Messenger(new UpdateProgressBarHandler(Looper.getMainLooper()));
    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
            serviceMessenger = new Messenger(service);
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBinder = null;
            serviceMessenger = null;
            bound = false;
        }
    };

    class UpdateProgressBarHandler extends Handler {
        public UpdateProgressBarHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MusicService.SEND_SONG_STATE_MSG) {
                int duration = msg.arg1; // 总时长
                int position = msg.arg2; // 当前播放进度
                // 更新进度条
                binding.progressBar.setMax(duration);
                binding.progressBar.setProgress(position);
            }
        }
    }

    public static PlayingSongFragment newInstance() {
        return new PlayingSongFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayingSongBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bound) {
            requireActivity().unbindService(mConn);
            bound = false;
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!bound) {
            Intent intent = new Intent(getContext(), MusicService.class);
            intent.putExtra("PLAYING_FLAG", DEFAULT_MUSIC_TYPE);
            intent.putExtra("song", new Song(1, "周杰伦", 1L, R.drawable.jay1, R.raw.test3, 1, 1));
            requireActivity().bindService(intent, mConn, Context.BIND_AUTO_CREATE);
            progress = true;
        }
        initHandler.postDelayed(() -> {
            // 获取歌曲进度
            /*try {
                Message clientMsg = Message.obtain(null, GET_SONG_STATE_MSG);
                clientMsg.replyTo = clentMessenger;
                serviceMessenger.send(clientMsg);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }*/

            if (progress) {
                new Thread(() -> {
                    int duration = musicBinder.callGetDuration();
                    int position = musicBinder.callGetPosition();
                    binding.progressBar.setMax(duration);
                    binding.progressBar.setProgress(position);
                }).start();
            }

            // 绑定播放按钮单击事件
            bindPlayBtn();

            // 监听播放进度
            // musicBinder.callIsPlaying(true);

            // 进入页面初始化播放按钮
            if (musicBinder.callGetPlayStatus()) {
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80);
            } else {
                binding.playingBtn.setImageResource(R.drawable.play_circle_80);
            }

            // 绑定下一首
            binding.playingNextSongBtn.setOnClickListener(v -> {
                musicBinder.callNextSong();
                if (musicBinder.callGetIndex() == musicBinder.callGetPlayListSize() - 1) {
                    setNextSongBtnState(R.drawable.skip_next_gray, false); // 切换下一首状态为不可点
                }
                setPrevSongBtnState(R.drawable.skip_previous, true);
            });
            // 绑定上一首
            binding.playingPrevSongBtn.setOnClickListener(v -> {
                musicBinder.callPreSong();
                if (musicBinder.callGetIndex() == 0) {
                    setPrevSongBtnState(R.drawable.skip_previous_gray, false);
                }
                setNextSongBtnState(R.drawable.skip_next, true); // 切换下一首状态为可点
            });
        }, 200);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("duration", musicBinder.callGetDuration());
        outState.putInt("position", musicBinder.callGetPosition());
    }

    // 设置上一首按钮状态
    private void setPrevSongBtnState(int resId, boolean clickable) {
        binding.playingPrevSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, resId, 0);
        binding.playingPrevSongBtn.setClickable(clickable);
    }

    // 设置下一首按钮状态
    private void setNextSongBtnState(int resId, boolean clickable) {
        binding.playingNextSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
        binding.playingNextSongBtn.setClickable(clickable);
    }

    // 绑定播放按钮
    private void bindPlayBtn() {
        binding.playingBtn.setOnClickListener(v -> {
            if (musicBinder.callGetPlayStatus()) { // 正在播放
                musicBinder.callsStartOrPause(false); // 暂停
                binding.playingBtn.setImageResource(R.drawable.play_circle_80);
            } else {
                musicBinder.callsStartOrPause(true); // 播放
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80);
            }
        });
    }
}
