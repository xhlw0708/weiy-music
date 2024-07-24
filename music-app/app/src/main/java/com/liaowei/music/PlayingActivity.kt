package com.liaowei.music

import android.annotation.SuppressLint
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.liaowei.music.adapter.PlayingAdapter
import com.liaowei.music.databinding.ActivityPlayingBinding
import com.liaowei.music.fragment.PlayingLyricFragment
import com.liaowei.music.fragment.PlayingSongFragment

class PlayingActivity : AppCompatActivity() {

    private val binding: ActivityPlayingBinding by lazy {
        ActivityPlayingBinding.inflate(
            layoutInflater
        )
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.playing)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun initView() {
        binding.playingBack.setOnClickListener { finish() }
        val fragments: List<Fragment> =
            listOf(PlayingSongFragment.newInstance(), PlayingLyricFragment.newInstance())
        binding.playingViewPager.adapter = PlayingAdapter(this, fragments)
        // 切换页面更换上标题
        binding.playingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            @SuppressLint("ResourceAsColor")
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position == 0) {
                    // binding.playingSongTitle.setTextColor(R.color.weiy_theme_color)
                    binding.playingLyricTitle.setTextColor(R.color.weiy_playing_color)
                } else {
                    binding.playingLyricTitle.setTextColor(R.color.weiy_theme_color)
                    // binding.playingSongTitle.setTextColor(R.color.weiy_playing_color)
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun finish() {
        super.finish()
        // overridePendingTransition(0, R.anim.playing_from_top_to_bottom)
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, R.anim.playing_from_top_to_bottom)
    }
}