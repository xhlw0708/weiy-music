package com.liaowei.music.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liaowei.music.R
import com.liaowei.music.databinding.FragmentPlayingSongBinding

class PlayingSongFragment : Fragment() {

    private val binding: FragmentPlayingSongBinding by lazy { FragmentPlayingSongBinding.inflate(layoutInflater) }

    companion object {
        fun newInstance() = PlayingSongFragment()
    }

    private val viewModel: PlayingSongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}