package com.monetizationlib.data.base.view.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.monetizationlib.data.R

class RoundedCornerImageView : AppCompatImageView {
    private var radius = 30

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val attributesArray =
            context.obtainStyledAttributes(attrs, R.styleable.RoundedCornerImageView)

        try {
            radius =
                attributesArray.getDimension(R.styleable.RoundedCornerImageView_radius,
                    radius.toFloat()
                ).toInt()
        } finally {
            attributesArray.recycle()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val clipPath = Path()
        val w = this.width
        val h = this.height
        clipPath.addRoundRect(
            RectF(0f, 0f, w.toFloat(), h.toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            Path.Direction.CW
        )
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }

    fun setRadius(radius: Int) {
        this.radius = radius
        this.invalidate()
    }
}
