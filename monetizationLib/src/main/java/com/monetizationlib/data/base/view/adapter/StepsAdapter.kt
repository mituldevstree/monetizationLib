package com.monetizationlib.data.base.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.monetizationlib.data.R

class StepsAdapter(
    private var totalSteps: Int,
    private var completedSteps: Int?,
) : RecyclerView.Adapter<StepsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_step_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tvSteps = holder.view.findViewById<AppCompatTextView>(R.id.tvSteps)
        val checkboxImage = holder.view.findViewById<ImageView>(R.id.checkboxImage)
        val viewLine = holder.view.findViewById<View>(R.id.viewLine)
        viewLine.visibility = View.INVISIBLE.takeIf { position == totalSteps - 1 } ?: View.VISIBLE
        completedSteps?.let {
            tvSteps.isSelected = position < it
        }

        checkboxImage.visibility = when {
            tvSteps.isSelected -> View.VISIBLE
            else -> View.INVISIBLE
        }

        tvSteps.text = "${position + 1}"
    }

    override fun getItemCount(): Int {
        return totalSteps
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}