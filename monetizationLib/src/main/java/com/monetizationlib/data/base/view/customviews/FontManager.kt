package com.monetizationlib.data.base.view.customviews

import android.content.Context
import android.graphics.Typeface

/**
 * The font manager will encapsulate the initialization of all the possible fonts we are
 * using in the application.
 *
 * Fonts should always be accessed through the proper Enum of the font.
 */
object FontManager {
    var sanFranciscoBoldTypeface: Typeface? = null
    var sanFranciscoMediumTypeface: Typeface? = null
    var sanFranciscoRegularTypeface: Typeface? = null
    var sanFranciscoSemiBoldTypeface: Typeface? = null

    fun init(context: Context) {
        sanFranciscoRegularTypeface = Typeface.createFromAsset(context.assets, "font/SF-Pro-Display-Regular.otf")
        sanFranciscoBoldTypeface = Typeface.createFromAsset(context.assets, "font/SF-Pro-Display-Bold.otf")
        sanFranciscoMediumTypeface= Typeface.createFromAsset(context.assets, "font/SF-Pro-Display-Medium.otf")
        sanFranciscoSemiBoldTypeface= Typeface.createFromAsset(context.assets, "font/SF-Pro-Display-Semibold.otf")
    }
}


/**
 * SanFrancisco is the main font of the application, the
 * enumeration represents all the possible styles of the font.
 */
enum class Font {

    BOLD {
        override fun getFont() = FontManager.sanFranciscoBoldTypeface
    },
    REGULAR {
        override fun getFont() = FontManager.sanFranciscoRegularTypeface
    },
    MEDIUM {
        override fun getFont() = FontManager.sanFranciscoMediumTypeface
    },
    SEMIBOLD {
        override fun getFont() = FontManager.sanFranciscoSemiBoldTypeface
    };

    abstract fun getFont(): Typeface?
}

