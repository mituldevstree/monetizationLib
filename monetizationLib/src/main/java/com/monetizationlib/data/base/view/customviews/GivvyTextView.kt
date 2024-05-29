package com.monetizationlib.data.base.view.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.monetizationlib.data.R

/**
 * GivvyTextView class represent the base text view class which is used
 * throughout the application.
 *
 * TextView class usage must be avoided.
 */
open class GivvyTextView : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        val attributesArray = context.obtainStyledAttributes(attrs, R.styleable.GivvyTextView)

        try {
            val styleIndex =
                attributesArray.getInt(
                    R.styleable.GivvyTextView_textFont,
                    TextFont.REGULAR.getAttributeIndex()
                )

            val textStyle = TextFont.values().first { it.getAttributeIndex() == styleIndex }

            applyStyle(textStyle)
        } finally {
            attributesArray.recycle()
        }
    }

    fun applyStyle(font: TextFont) {
        typeface = font.getFont()

        letterSpacing = font.getLetterSpacing()
    }
}

/**
 * Enum representing all the possible styles of the
 */
enum class TextFont {

    BOLD {
        override fun getLetterSpacing() = DEFAULT_TEXT_SPACING

        override fun getFont() = Font.BOLD.getFont()

        override fun getAttributeIndex() = 0
    },
    REGULAR {
        override fun getLetterSpacing() = DEFAULT_TEXT_SPACING

        override fun getFont() = Font.REGULAR.getFont()

        override fun getAttributeIndex() = 1
    },
    MEDIUM {
        override fun getLetterSpacing() = DEFAULT_TEXT_SPACING

        override fun getFont() = Font.MEDIUM.getFont()

        override fun getAttributeIndex() = 2
    },
    SEMIBOLD {
        override fun getLetterSpacing() = DEFAULT_TEXT_SPACING

        override fun getFont() = Font.SEMIBOLD.getFont()

        override fun getAttributeIndex() = 5
    }
    ;

    internal val DEFAULT_TEXT_SPACING = 0f

    abstract fun getFont(): Typeface?

    abstract fun getLetterSpacing(): Float

    abstract fun getAttributeIndex(): Int
}
