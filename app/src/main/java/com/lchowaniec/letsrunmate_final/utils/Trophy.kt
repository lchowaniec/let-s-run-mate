package com.lchowaniec.letsrunmate_final.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView

open class Trophy() {
    private val DecelerateInterpolator = DecelerateInterpolator()
    private val AccelerateInterpolator = AccelerateInterpolator()

    private lateinit var mTrophyWhite: ImageView
    private lateinit var mTrophyGold: ImageView
    constructor(mTrophyWhite:ImageView,mTrophyGold:ImageView):this(){
        this.mTrophyGold = mTrophyGold
        this.mTrophyWhite = mTrophyWhite
    }


    open fun throphing(){
        val animation = AnimatorSet()
        if(mTrophyGold.visibility == View.VISIBLE){
            mTrophyGold.scaleX = 0.1f
            mTrophyGold.scaleY = 0.1f
            val downY = ObjectAnimator.ofFloat(mTrophyGold,"scaleY",1f,0f)
            downY.duration = 200
            downY.interpolator = AccelerateInterpolator
            val downX = ObjectAnimator.ofFloat(mTrophyGold,"scaleX",1f,0f)
            downX.duration = 200
            downX.interpolator = AccelerateInterpolator
            mTrophyGold.visibility = View.GONE
            mTrophyWhite.visibility = View.VISIBLE
            animation.playTogether(downY,downX)

        }else if (mTrophyGold.visibility == View.GONE){
            mTrophyGold.scaleX = 0.1f
            mTrophyGold.scaleY = 0.1f
            val downY = ObjectAnimator.ofFloat(mTrophyGold,"scaleY",0.1f,1f)
            downY.duration = 200
            downY.interpolator = DecelerateInterpolator
            val downX = ObjectAnimator.ofFloat(mTrophyGold,"scaleX",0.1f,1f)
            downX.duration = 200
            downX.interpolator = DecelerateInterpolator
            mTrophyGold.visibility = View.VISIBLE
            mTrophyWhite.visibility = View.GONE
            animation.playTogether(downY,downX)

        }
        animation.start()


    }


}