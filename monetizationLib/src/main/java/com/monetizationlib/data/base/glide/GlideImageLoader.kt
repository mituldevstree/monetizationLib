package com.monetizationlib.data.base.glide

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

class GlideImageLoader(private val mImageView: ImageView, private val mProgressBar: ProgressBar?) {

    fun load(url: String?, options: RequestOptions?) {
        if (url == null || options == null) return

        onConnecting()

        //set Listener & start
        ProgressAppGlideModule.expect(
            url,
            object : ProgressAppGlideModule.UIonProgressListener {

                override val granualityPercentage: Float
                    get() = 1.0f

                override fun onProgress(bytesRead: Long, expectedLength: Long) {
                    if (mProgressBar != null) {
                        mProgressBar.progress = (100 * bytesRead / expectedLength).toInt()
                    }
                }
            })
        Glide.with(mImageView.context)
            .load(url)
            .apply(options.skipMemoryCache(false))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    ProgressAppGlideModule.forget(url)
                    onFinished()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    ProgressAppGlideModule.forget(url)
                    onFinished()
                    return false
                }
            })
            .into(mImageView)
    }


    private fun onConnecting() {
        if (mProgressBar != null) mProgressBar.visibility = View.VISIBLE
    }

    private fun onFinished() {
        if (mProgressBar != null && mImageView != null) {
            mProgressBar.visibility = View.GONE
            mImageView.visibility = View.VISIBLE
        }
    }
}