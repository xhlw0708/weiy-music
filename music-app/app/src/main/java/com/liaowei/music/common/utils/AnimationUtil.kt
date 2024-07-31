package com.liaowei.music.common.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation

class AnimationUtil {

    companion object {
        fun startRotateAnimation(view: View) {
            val rotateAnimation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
            rotateAnimation.duration = 10000 // 动画持续时间，单位毫秒
            rotateAnimation.repeatCount = ObjectAnimator.INFINITE // 无限循环
            rotateAnimation.interpolator = LinearInterpolator() // 使用线性插值器保证匀速旋转
            rotateAnimation.start()
        }
    }

}