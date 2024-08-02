package com.liaowei.music.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import com.liaowei.music.PlayingActivity
import com.liaowei.music.R


class MusicForegroundService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "my_channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "My Channel"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "My Channel Description"
        const val NOTIFICATION_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
        channel.description = NOTIFICATION_CHANNEL_DESCRIPTION
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.music_video)
            .setContentTitle(
                intent?.getStringExtra("name") ?: getString(R.string.unknown_song_name)
            )
            .setContentText(
                intent?.getStringExtra("singerName") ?: getString(R.string.unknown_singer_name)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
        // 处理点击Notification的逻辑 返回正在运行的activity
        /*val resultIntent = Intent(baseContext, PlayingActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
            baseContext,
            0x004,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.setContentIntent(resultPendingIntent)*/
        val notification = notificationBuilder.build()

        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

}