package com.liaowei.music.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liaowei.music.service.MusicService

data class PlayingSongViewModel(
    var duration: MutableLiveData<Int>,
    var position: MutableLiveData<Int>
) : ViewModel()