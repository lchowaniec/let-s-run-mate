package com.lchowaniec.letsrunmate_final.utils

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.lchowaniec.letsrunmate_final.R
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

open class ImageLoader {

    private val imageDefault:Int = R.mipmap.ic_launcher
    private lateinit var mContext: Context

    constructor(context: Context?){
        if (context != null) {
            mContext = context
        }
    }

    constructor()


    fun getConfig(): ImageLoaderConfiguration{
        val default: DisplayImageOptions = DisplayImageOptions.Builder()
            .showImageOnLoading(imageDefault)
            .showImageForEmptyUri(imageDefault)
            .showImageOnFail(imageDefault)
            .considerExifParams(true)
            .cacheOnDisk(true).cacheInMemory(true)
            .cacheOnDisk(true).resetViewBeforeLoading(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .displayer(FadeInBitmapDisplayer(300))
            .build()

        val configuration: ImageLoaderConfiguration = ImageLoaderConfiguration.Builder(mContext)
            .defaultDisplayImageOptions(default)
            .memoryCache(WeakMemoryCache())
            .diskCacheSize(100*1024*1024)
            .build()

        return configuration


    }
     fun setImage(
         imgURL: String, image: ImageView,
         mProgressBar: ProgressBar?,
         append:String){
        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(append + imgURL, image, object : ImageLoadingListener {
            override fun onLoadingStarted(imageUri: String, view: View){
                if(mProgressBar != null){
                    mProgressBar.visibility = View.VISIBLE
                }


            }
            override fun onLoadingFailed(imageUri: String, view:View,failReason: FailReason){
                if(mProgressBar != null){
                    mProgressBar.visibility = View.GONE
                }
            }

            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                if(mProgressBar != null){
                    mProgressBar.visibility = View.GONE
                }            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
                if(mProgressBar != null){
                    mProgressBar.visibility = View.GONE
                }            }


        })


    }

}