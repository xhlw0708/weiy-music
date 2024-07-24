package com.liaowei.music.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liaowei.music.R

class PlayingLyricFragment : Fragment() {

    companion object {
        fun newInstance() = PlayingLyricFragment()
    }

    private val viewModel: PlayingLyricViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playing_lyric, container, false)
    }
}