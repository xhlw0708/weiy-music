package com.liaowei.music

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liaowei.music.common.adapter.SingerAdapter
import com.liaowei.music.databinding.ActivitySingerBinding
import com.liaowei.music.model.domain.Singer


class SingerActivity : AppCompatActivity() {

    private val binding: ActivitySingerBinding by lazy {
        ActivitySingerBinding.inflate(
            layoutInflater
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.singer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val singerList = intent.getParcelableArrayListExtra(
            "singerList",
            Singer::class.java
        ) as ArrayList<Singer>

        binding.singerRv.adapter = SingerAdapter(singerList)
        // binding.singerRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        binding.singerRv.layoutManager = layoutManager

        binding.singerTopTitle.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    val drawable = binding.singerTopTitle.compoundDrawables[0]
                    if (drawable != null) {
                        val drawableRect = drawable.bounds
                        val x = event.x.toInt()
                        val y = event.y.toInt()
                        if (x in drawableRect.left until drawableRect.width() && y in drawableRect.top until drawableRect.height()) {
                            finish()
                        }
                    }
                }
            }
            true
        }
    }
}