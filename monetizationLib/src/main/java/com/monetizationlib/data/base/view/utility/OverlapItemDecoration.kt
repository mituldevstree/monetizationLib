package com.monetizationlib.data.base.view.utility

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OverlapItemDecoration(context: Context, overlapDp: Int, private val totalSize: Int) :
    RecyclerView.ItemDecoration() {

    private val overlapPx: Int =
        (context.resources.displayMetrics.density * overlapDp.toFloat()).toInt()

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position in 1 until totalSize.minus(1))
            if (position == totalSize.minus(2)) {
                outRect.set(-overlapPx, 0, 0, 0)
            } else {
                outRect.set(-overlapPx, 0, overlapPx, 0)
            }

    }
}