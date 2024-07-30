package com.liaowei.music.fragment;

import static com.liaowei.music.common.constant.MusicConstant.DEFAULT_MUSIC_TYPE;
import static com.liaowei.music.service.MusicService.GET_SONG_STATE_MSG;
import static com.liaowei.music.service.MusicService.SEND_SONG_STATE_MSG;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.liaowei.music.R;
import com.liaowei.music.broadcast.SeekBarReceiver;
import com.liaowei.music.databinding.FragmentPlayingSongBinding;
import com.liaowei.music.main.model.Song;
import com.liaowei.music.service.MusicService;


public class PlayingSongFragment extends Fragment {

    private static FragmentPlayingSongBinding binding;
    private static boolean bound = false;
    private MusicService.MusicBinder musicBinder;
    private MusicService musicService;
    private final Handler initHandler = new Handler(Looper.getMainLooper());
    private Messenger serviceMessenger;
    private final Messenger clentMessenger = new Messenger(new UpdateProgressBarHandler(Looper.getMainLooper()));
    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
            musicService = musicBinder.getService();
            serviceMessenger = new Messenger(service);
            bound = true;

           /* Message clientMsg = Message.obtain(null, GET_SONG_STATE_MSG);
            clientMsg.replyTo = clentMessenger;
            try {
                serviceMessenger.send(clientMsg);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }*/

            /*new Thread(() -> {
                while (bound) {
                    try {
                        Message clientMsg = Message.obtain(null, GET_SONG_STATE_MSG);
                        clientMsg.replyTo = clentMessenger;
                        serviceMessenger.send(clientMsg);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();*/
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBinder = null;
            serviceMessenger = null;
            bound = false;
        }
    };

    public static Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int position = msg.arg1;
            binding.progressBar.setProgress(position);
            binding.position.setText(formatTime(position));
        }
    };


    static class UpdateProgressBarHandler extends Handler {
        public UpdateProgressBarHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == GET_SONG_STATE_MSG) {
                int duration = msg.arg1; // 总时长
                int position = msg.arg2; // 当前播放进度
                // 更新进度条
                binding.progressBar.setMax(duration);
                binding.progressBar.setProgress(position);
                // 更新总时长
                binding.duration.setText(formatTime(duration));
                // 更新当前进度
                binding.position.setText(formatTime(position));
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


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!bound) {
            Intent intent = new Intent(getContext(), MusicService.class);
            intent.putExtra("PLAYING_FLAG", DEFAULT_MUSIC_TYPE);
            intent.putExtra("song", new Song(1, "周杰伦", 1L, R.drawable.jay1, R.raw.test1, 1, 1));
            requireActivity().bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        }
        initHandler.postDelayed(() -> {
            // 获取歌曲进度
            // 执行任务
            // CompletableFuture.runAsync(task);
            // new Thread(task).start();
            binding.progressBar.setMax(musicService.getDuration());
            binding.duration.setText(formatTime(musicService.getDuration()));


            // 绑定seekBar事件
            bindSeekBar();

            // 绑定播放按钮单击事件
            bindPlayBtn();

            // 进入页面初始化播放按钮
            if (musicService.getPlayStatus()) {
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80);
            } else {
                binding.playingBtn.setImageResource(R.drawable.play_circle_80);
            }

            // 绑定下一首
            binding.playingNextSongBtn.setOnClickListener(v -> {
                musicService.nextSong();
                // CompletableFuture.runAsync(task);
                // new Thread(task).start();
                binding.progressBar.setMax(musicService.getDuration());
                binding.duration.setText(formatTime(musicService.getDuration()));
                if (musicService.getIndex() == musicService.getPlayListSize() - 1) {
                    setNextSongBtnState(R.drawable.skip_next_gray, false); // 切换下一首状态为不可点
                }
                setPrevSongBtnState(R.drawable.skip_previous, true);
            });
            // 绑定上一首
            binding.playingPrevSongBtn.setOnClickListener(v -> {
                musicService.preSong();
                // CompletableFuture.runAsync(task);
                // new Thread(task).start();
                binding.progressBar.setMax(musicService.getDuration());
                binding.duration.setText(formatTime(musicService.getDuration()));
                if (musicService.getIndex() == 0) {
                    setPrevSongBtnState(R.drawable.skip_previous_gray, false);
                }
                setNextSongBtnState(R.drawable.skip_next, true); // 切换下一首状态为可点*//*
            });
        }, 500);
    }

    // 绑定seekBar事件
    private void bindSeekBar() {
        binding.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 音乐更新
                musicService.updateProgress(seekBar.getProgress());
                // 更新播放图标
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80);
            }
        });
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
            if (musicService.getPlayStatus()) { // 正在播放
                musicService.startOrPause(false); // 暂停
                binding.playingBtn.setImageResource(R.drawable.play_circle_80);
            } else {
                // musicService.playAsync(); // 播放
                binding.playingBtn.setImageResource(R.drawable.pause_circle_80);
            }
            // 执行任务
            // CompletableFuture.runAsync(task);
            // new Thread(task).start();
        });
    }

    // 格式化时间
    @SuppressLint("DefaultLocale")
    private static String formatTime(int time) {
        int minute = time / 1000 / 60;
        int second = time / 1000 % 60;
        return String.format("%02d:%02d", minute, second);
    }

}

