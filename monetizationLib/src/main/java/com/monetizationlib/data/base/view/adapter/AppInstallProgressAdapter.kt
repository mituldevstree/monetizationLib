package com.monetizationlib.data.base.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.monetizationlib.data.attributes.model.ThemeConfig
import com.monetizationlib.data.base.extensions.ViewUtil.applyLeftRoundedDrawableBg
import com.monetizationlib.data.base.extensions.ViewUtil.applyRightRoundedDrawableBg
import com.monetizationlib.data.base.extensions.ViewUtil.applyRoundedDrawableBg
import com.monetizationlib.data.base.extensions.dp
import com.monetizationlib.data.base.view.utility.MonetizationLibBindingAdaptersUtil
import com.monetizationlib.data.databinding.RowStepViewDownloadMonetizationBinding

class AppInstallProgressAdapter(var progress: Int, private val maxProgress: Int,private val themeConfig: ThemeConfig?) :
    RecyclerView.Adapter<AppInstallProgressAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowStepViewDownloadMonetizationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mBinding.themeConfig=themeConfig
        holder.mBinding.rootConstraintLayout.isSelected = (position < progress)
        holder.mBinding.viewLine.isSelected = (position < progress)
        holder.mBinding.rootConstraintLayout.isPressed = position == progress
        holder.mBinding.viewLine.apply {
            when (position) {
                0 -> {
                    applyLeftRoundedDrawableBg(dp(5f))
                    MonetizationLibBindingAdaptersUtil.setProgressDrawableState(
                        this,
                        themeConfig?.stepIndicatorComponent?.selectedIndicatorProgressStartColor?:"#AC6D16",
                        themeConfig?.stepIndicatorComponent?.selectedIndicatorProgressEndColor?:"#FFBD62")
                    //setImageResource(R.drawable.rounded_line_selector)
                    visibility = View.VISIBLE
                }

                maxProgress - 1 -> {
                    applyRightRoundedDrawableBg(dp(5f))
                    MonetizationLibBindingAdaptersUtil.setProgressDrawableState(
                        this,
                        themeConfig?.stepIndicatorComponent?.selectedIndicatorProgressStartColor?:"#AC6D16",
                        themeConfig?.stepIndicatorComponent?.selectedIndicatorProgressEndColor?:"#FFBD62")
                   // setImageResource(R.drawable.rounded_line_selector)
                    visibility = View.GONE
                }

                else -> {
                    visibility = View.VISIBLE
                    applyRoundedDrawableBg(dp(0f))
                    MonetizationLibBindingAdaptersUtil.setProgressDrawableState(
                        this,
                        themeConfig?.stepIndicatorComponent?.selectedIndicatorProgressStartColor?:"#AC6D16",
                        themeConfig?.stepIndicatorComponent?.selectedIndicatorProgressEndColor?:"#FFBD62")
                   // setImageResource(R.drawable.rounded_line_selector)

                }
            }

        }
    }

    override fun getItemCount(): Int {
        return maxProgress
    }


    inner class ViewHolder(val mBinding: RowStepViewDownloadMonetizationBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        init {
            val context = mBinding.rootConstraintLayout
            /* if (maxProgress < 4) {
                 mBinding.viewLine.layoutParams.width =
                     context.resources.displayMetrics.widthPixels.div(maxProgress)
             } else if (maxProgress < 7) {*/
            mBinding.viewLine.layoutParams.width =
                context.resources.displayMetrics.widthPixels.div(maxProgress.plus(1))
            // }
        }
    }
}