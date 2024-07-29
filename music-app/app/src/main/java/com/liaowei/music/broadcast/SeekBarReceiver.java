package com.liaowei.music.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class SeekBarReceiver extends BroadcastReceiver {
        private Handler handler;

        public SeekBarReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position", 0);
            Message message = Message.obtain();
            message.arg1 = position;
            handler.sendMessage(message);
        }
    }