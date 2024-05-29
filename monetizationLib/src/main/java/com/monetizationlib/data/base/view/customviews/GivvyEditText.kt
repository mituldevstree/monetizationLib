package com.monetizationlib.data.base.view.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.monetizationlib.data.R

open class GivvyEditText: androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, androidx.appcompat.R.attr.editTextStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        val attributesArray = context.obtainStyledAttributes(attrs, R.styleable.GivvyEditText)

        try {

            applyStyle()
        } finally {
            attributesArray.recycle()
        }
    }

    private fun applyStyle() {
        typeface = Typeface.DEFAULT_BOLD
    }

}