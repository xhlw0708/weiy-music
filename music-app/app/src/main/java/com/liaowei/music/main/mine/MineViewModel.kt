package com.liaowei.music.main.mine

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MineViewModel : ViewModel() {
    var fragment: MutableLiveData<Fragment>? = null

    // fun getFragment(): MutableLiveData<Fragment>? = fragment

    fun setFragment(fragment: Fragment) {
        if (this.fragment == null) {
            this.fragment = MutableLiveData()
        }
        this.fragment?.value = fragment
    }

}