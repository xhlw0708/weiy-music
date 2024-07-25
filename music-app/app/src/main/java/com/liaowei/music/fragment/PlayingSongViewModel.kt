package com.liaowei.music.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class PlayingSongViewModel(
    val playStatus: MutableLiveData<Boolean>
) : ViewModel() {
}