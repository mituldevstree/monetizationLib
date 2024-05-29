package com.monetizationlib.data.base.view.utility


import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.PorterDuff
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.media.Image
import android.util.Log
import android.util.StateSet
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mobilefuse.sdk.utils.dpToPx
import com.mobilefuse.sdk.utils.pxToDp
import com.monetizationlib.data.R
import com.monetizationlib.data.base.extensions.isValidColor
import java.io.File


object MonetizationLibBindingAdaptersUtil {


    @JvmStatic
    @BindingAdapter(value = ["app:setColorDynamic"], requireAll = false)
    fun setColorDynamic(view: View, color: String?) {
        if (color == null) return
        val context = view.context
        when (view) {
            is LinearLayout -> {
                if (color.isValidColor()) {
                    view.setBackgroundColor(Color.parseColor(color))
                } else {
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            context, R.color.windowBackground
                        )
                    )
                }
            }

            is TextView -> {
                if (color.isValidColor()) {
                    view.setTextColor(Color.parseColor(color))
                } else {
                    view.setTextColor(ContextCompat.getColor(context, R.color.windowBackground))
                }
            }

            is ImageView -> {
                if (color.isValidColor()) {
                    view.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
                } else {
                    view.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context, R.color.windowBackground
                        )
                    )
                }
            }

            is ProgressBar -> {
                if (color.isValidColor()) {
                    view.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color))
                } else {
                    view.indeterminateTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context, R.color.windowBackground
                        )
                    )
                }
            }
        }
    }




    @JvmStatic
    @BindingAdapter(value = ["lib:generateTextColorPrimaryDynamic"], requireAll = false)
    fun generateTextColorPrimaryDynamic(view: View, color: String?) {
        if (color == null) return
        val context = view.context
        if (view is TextView) {
            if (color.isValidColor()) {
                view.setTextColor(Color.parseColor(color))
            } else {
                view.setTextColor(ContextCompat.getColor(context, R.color.textColorLibPrimary))
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["lib:generateTextColorSecondaryDynamic"], requireAll = false)
    fun generateTextColorSecondaryDynamic(view: View, color: String?) {
        if (color == null) return
        val context = view.context
        if (view is TextView) {
            if (color.isValidColor()) {
                view.setTextColor(Color.parseColor(color))
            } else {
                view.setTextColor(ContextCompat.getColor(context, R.color.textColorLibSecondary))
            }
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["lib:generateTextColorFocusSecondaryDynamic"], requireAll = false)
    fun generateTextColorFocusSecondaryDynamic(view: TextView, color: String?) {
        if (color == null) return
        val context = view.context
        if (color.isValidColor()) {
            view.setTextColor(Color.parseColor(color))
        } else {
            view.setTextColor(ContextCompat.getColor(context, R.color.textColorLibFocusSecondary))
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["app:setBackgroundColorDynamic"], requireAll = false)
    fun setBackgroundColorDynamic(view: View, mainColor: String?) {
        if (mainColor == null) return
        val context = view.context
        if (mainColor.isValidColor()) {
            view.setBackgroundColor(Color.parseColor(mainColor))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.windowBackground))
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["app:setProgressBarColorDynamic"], requireAll = false)
    fun setProgressBarColorDynamic(view: ProgressBar, color: String?) {
        if (color == null) return
        val context = view.context
        if (color.isValidColor()) {
            view.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color))
        } else {
            view.indeterminateTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context, R.color.primaryButtonStartColor
                )
            )
        }
    }


    @JvmStatic
    @BindingAdapter(
        value = ["android:pStartColor", "android:pEndColor", "android:pTopLeftCorner", "android:pTopRightCorner", "android:pBottomLeftCorner", "android:pBottomRightCorner"],
        requireAll = false
    )
    fun setBackgroundShape(
        view: View,
        pStartColor: String?,
        pEndColor: String?,
        pTopLeftCorner: Float?,
        pTopRightCorner: Float?,
        pBottomLeftCorner: Float?,
        pBottomRightCorner: Float?,
    ) {
        view.background = GradeDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            if (pStartColor?.isValidColor() == true) {
                Color.parseColor(pStartColor)
            } else Color.parseColor("#00543bd4"),
            if (pEndColor?.isValidColor() == true) {
                Color.parseColor(
                    pEndColor
                )
            } else {
                Color.parseColor(
                    "#00935dbf"
                )
            },
            pTopLeftCorner ?: 0F,
            pTopRightCorner ?: 0F,
            pBottomLeftCorner ?: 0F,
            pBottomRightCorner ?: 0F
        )
    }


    @JvmStatic
    @BindingAdapter(
        value = ["lib:mainComponentStartColor", "lib:mainComponentEndColor", "lib:mainComponentStartBorderColor", "lib:mainComponentEndBorderColor"],
        requireAll = false
    )
    fun setMainComponentBackground(
        mainComponent: View,
        startColor: String?,
        endColor: String?,
        startBorderColor: String?,
        endBorderColor: String?,
    ) {

        val radius = mainComponent.context.resources.getDimension(com.intuit.sdp.R.dimen._9sdp)
            .dpToPx(mainComponent.context).toFloat()
        val shadowRadius =
            mainComponent.context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
                .dpToPx(mainComponent.context).toFloat()
        val shadowWidth = 10f.pxToDp(mainComponent.context).toInt()
        val strokeWidth = shadowWidth.plus(9f).pxToDp(mainComponent.context).toInt()
        val shadowRadii = floatArrayOf(
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius
        )
        val borderRadii =
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val innerRadii =
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        mainComponent.visibilityChanged { view ->
            when (view.visibility) {
                VISIBLE -> {
                    val shadowRect = RoundRectShape(shadowRadii, null, null)
                    val shadowRectangleDrawable = ShapeDrawable(shadowRect)
                    shadowRectangleDrawable.paint.shader = LinearGradient(
                        0f,
                        0f,
                        0f,
                        view.measuredHeight.toFloat(),
                        Color.parseColor("#10000000"),
                        Color.parseColor("#13000000"),
                        Shader.TileMode.CLAMP
                    )

                    val borderRectangle = RoundRectShape(borderRadii, null, null)
                    val borderRectangleDrawable = ShapeDrawable(borderRectangle)
                    borderRectangleDrawable.paint.shader = LinearGradient(
                        0f,
                        0f,
                        0f,
                        view.measuredHeight.toFloat(),
                        if (startBorderColor?.isValidColor() == true) Color.parseColor(
                            startBorderColor
                        )
                        else {
                            ContextCompat.getColor(
                                view.context, R.color.mainContainerBorderStartColor
                            )
                        },
                        if (endBorderColor?.isValidColor() == true) Color.parseColor(endBorderColor)
                        else {
                            ContextCompat.getColor(
                                view.context, R.color.mainContainerBorderEndColor
                            )
                        },
                        Shader.TileMode.CLAMP
                    )

                    val innerRectangleShape = RoundRectShape(innerRadii, null, null)
                    val innerRectangleDrawable = ShapeDrawable(innerRectangleShape)
                    innerRectangleDrawable.paint.shader = RadialGradient(
                        if (view.measuredWidth.toFloat() <= 0) 500f else view.measuredWidth.toFloat() / 2,
                        if (view.measuredHeight.toFloat() <= 0) 600f else view.measuredHeight.toFloat() / 3,
                        if (view.measuredWidth.toFloat() <= 0) 800f
                        else view.measuredWidth.toFloat(),
                        if (startColor?.isValidColor() == true) Color.parseColor(startColor)
                        else {
                            ContextCompat.getColor(
                                view.context, R.color.mainContainerStartColor
                            )
                        },
                        if (endColor?.isValidColor() == true) Color.parseColor(endColor) else {
                            ContextCompat.getColor(
                                view.context, R.color.mainContainerEndColor
                            )
                        },
                        Shader.TileMode.CLAMP
                    )

                    val drawableList = arrayOf<Drawable>(
                        shadowRectangleDrawable, borderRectangleDrawable, innerRectangleDrawable
                    )
                    val layeredListDrawable = LayerDrawable(drawableList)
                    layeredListDrawable.setLayerInset(0, 0, 0, 0, 0)
                    layeredListDrawable.setLayerInset(
                        1, shadowWidth, shadowWidth, shadowWidth, shadowWidth
                    )
                    layeredListDrawable.setLayerInset(
                        2, strokeWidth, strokeWidth, strokeWidth, strokeWidth
                    )
                    view.background = layeredListDrawable
                }

                else -> {

                }
            }
        }

    }


    @JvmStatic
    @BindingAdapter(
        value = ["lib:secondaryComponentStartColor", "lib:secondaryComponentEndColor", "lib:secondaryComponentStartBorderColor", "lib:secondaryComponentEndBorderColor"],
        requireAll = false
    )
    fun setSecondaryComponentBackground(
        secondaryComponent: View,
        startColor: String?,
        endColor: String?,
        startBorderColor: String?,
        endBorderColor: String?,
    ) {
        val radius = secondaryComponent.context.resources.getDimension(com.intuit.sdp.R.dimen._9sdp)
            .dpToPx(secondaryComponent.context).toFloat()
        val shadowRadius =
            secondaryComponent.context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
                .dpToPx(secondaryComponent.context).toFloat()
        val shadowWidth = 8f.pxToDp(secondaryComponent.context).toInt()
        val strokeWidth = shadowWidth.plus(6f).pxToDp(secondaryComponent.context).toInt()
        val shadowRadii = floatArrayOf(
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius
        )
        val borderRadii =
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val innerRadii =
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        secondaryComponent.visibilityChanged { view ->
            when (view.visibility) {
                VISIBLE -> {
                    val shadowRect = RoundRectShape(shadowRadii, null, null)
                    val shadowRectangleDrawable = ShapeDrawable(shadowRect)
                    shadowRectangleDrawable.paint.shader = LinearGradient(
                        0f,
                        0f,
                        0f,
                        view.measuredHeight.toFloat(),
                        Color.parseColor("#23000000"),
                        Color.parseColor("#13000000"),
                        Shader.TileMode.CLAMP
                    )

                    val borderRectangle = RoundRectShape(borderRadii, null, null)
                    val borderRectangleDrawable = ShapeDrawable(borderRectangle)
                    borderRectangleDrawable.paint.shader = LinearGradient(
                        0f,
                        0f,
                        0f,
                        view.measuredHeight.toFloat(),
                        if (endBorderColor?.isValidColor() == true) Color.parseColor(endBorderColor) else {
                            ContextCompat.getColor(
                                view.context, R.color.secondaryContainerBorderEndColor
                            )
                        },
                        if (startBorderColor?.isValidColor() == true) Color.parseColor(
                            startBorderColor
                        ) else {
                            ContextCompat.getColor(
                                view.context, R.color.secondaryContainerBorderStartColor
                            )
                        },
                        Shader.TileMode.CLAMP
                    )

                    val innerRectangleShape = RoundRectShape(innerRadii, null, null)
                    val innerRectangleDrawable = ShapeDrawable(innerRectangleShape)
                    innerRectangleDrawable.paint.shader = RadialGradient(
                        view.measuredWidth.toFloat() / 2,
                        view.measuredHeight.toFloat() / 2,
                        if (view.measuredWidth.toFloat() <= 0) 500f
                        else view.measuredWidth.toFloat(),
                        if (startColor?.isValidColor() == true) Color.parseColor(startColor)
                        else {
                            ContextCompat.getColor(
                                view.context, R.color.secondaryContainerStartColor
                            )
                        },
                        if (endColor?.isValidColor() == true) Color.parseColor(endColor) else {
                            ContextCompat.getColor(
                                view.context, R.color.secondaryContainerEndColor
                            )
                        },
                        Shader.TileMode.CLAMP
                    )

                    val drawableList = arrayOf<Drawable>(
                        shadowRectangleDrawable, borderRectangleDrawable, innerRectangleDrawable
                    )
                    val layeredListDrawable = LayerDrawable(drawableList)
                    layeredListDrawable.setLayerInset(0, 0, 0, 0, 0)
                    layeredListDrawable.setLayerInset(
                        1, shadowWidth, shadowWidth, shadowWidth, shadowWidth
                    )
                    layeredListDrawable.setLayerInset(
                        2, strokeWidth, strokeWidth, strokeWidth, strokeWidth
                    )
                    view.background = layeredListDrawable

                }

                else -> {

                }
            }
        }
    }


    @JvmStatic
    @BindingAdapter(
        value = ["lib:buttonPrimaryStartColor", "lib:buttonPrimaryEndColor", "lib:buttonPrimaryStartBorderColor", "lib:buttonPrimaryEndBorderColor", "lib:buttonSecondaryStartColor", "lib:buttonSecondaryEndColor", "lib:buttonSecondaryStartBorderColor", "lib:buttonSecondaryEndBorderColor"],
        requireAll = false
    )
    fun setButtonComponent(
        buttonView: Button,
        primaryStartColor: String?,
        primaryEndColor: String?,
        primaryStartBorderColor: String?,
        primaryEndBorderColor: String?,

        secondaryStartColor: String?,
        secondaryEndColor: String?,
        secondaryStartBorderColor: String?,
        secondaryEndBorderColor: String?,
    ) {
        buttonView.visibilityChanged { view ->
            when (view.visibility) {
                VISIBLE -> {

                    val defaultLayeredListDrawable = createRoundedLayeredListWithBorder(
                        view,
                        startColor = primaryStartColor,
                        endColor = primaryEndColor,
                        startBorderColor = primaryStartBorderColor,
                        endBorderColor = primaryEndBorderColor
                    )

                    val pressedDrawableList = createRoundedLayeredListWithBorder(
                        view,
                        startColor = primaryStartColor,
                        endColor = primaryEndColor,
                        startBorderColor = primaryStartBorderColor,
                        endBorderColor = primaryEndBorderColor
                    ).apply {
                        this.setTint(ContextCompat.getColor(view.context, R.color.blackAlpha25))
                        this.setTintMode(PorterDuff.Mode.SRC_ATOP)
                    }

                    val disableDrawableList = createRoundedLayeredListWithBorder(
                        view,
                        startColor = primaryStartColor,
                        endColor = primaryEndColor,
                        startBorderColor = primaryStartBorderColor,
                        endBorderColor = primaryEndBorderColor
                    ).apply {
                        this.setTint(ContextCompat.getColor(view.context, R.color.blackAlpha45))
                        this.setTintMode(PorterDuff.Mode.SRC_ATOP)
                    }


                    val defaultLayeredListDrawableSecondary = createRoundedLayeredListWithBorder(
                        view,
                        startColor = secondaryStartColor,
                        endColor = secondaryEndColor,
                        startBorderColor = secondaryStartBorderColor,
                        endBorderColor = secondaryEndBorderColor
                    )

                    val pressedSecondaryDrawableList = createRoundedLayeredListWithBorder(
                        view,
                        startColor = secondaryStartColor,
                        endColor = secondaryEndColor,
                        startBorderColor = secondaryStartBorderColor,
                        endBorderColor = secondaryEndBorderColor
                    ).apply {
                        this.setTint(
                            ContextCompat.getColor(
                                view.context, R.color.blackAlpha25
                            )
                        )
                        this.setTintMode(PorterDuff.Mode.SRC_ATOP)
                    }

                    val res = StateListDrawable()
                    // res.setExitFadeDuration(100)
                    res.addState(
                        intArrayOf(
                            -android.R.attr.state_enabled, -android.R.attr.state_selected
                        ), disableDrawableList
                    )

                    res.addState(
                        intArrayOf(
                            -android.R.attr.state_pressed, android.R.attr.state_selected
                        ), defaultLayeredListDrawableSecondary
                    )

                    res.addState(
                        intArrayOf(
                            android.R.attr.state_pressed, android.R.attr.state_selected
                        ), pressedSecondaryDrawableList
                    )

                    res.addState(
                        intArrayOf(
                            android.R.attr.state_pressed, -android.R.attr.state_selected
                        ), pressedDrawableList
                    )

                    res.addState(
                        intArrayOf(android.R.attr.state_focused), defaultLayeredListDrawable
                    )
                    res.addState(
                        StateSet.WILD_CARD, defaultLayeredListDrawable
                    )


                    view.background = res
                }


                else -> {

                }
            }
        }
    }

    private fun createRoundedLayeredListWithBorder(
        view: View, startColor: String?,
        endColor: String?,
        startBorderColor: String?,
        endBorderColor: String?,
    ): LayerDrawable {
        Log.e("StartBorderColor", startBorderColor.toString())
        val radius =
            view.context.resources.getDimension(com.intuit.sdp.R.dimen._8sdp).dpToPx(view.context)
                .toFloat()
        val shadowRadius =
            view.context.resources.getDimension(com.intuit.sdp.R.dimen._9sdp).dpToPx(view.context)
                .toFloat()
        val shadowRadii = floatArrayOf(
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius
        )
        val borderRadii =
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)


        val shadowWidth = 8f.pxToDp(view.context).toInt()
        val strokeWidth = shadowWidth.plus(8f).pxToDp(view.context).toInt()

        val shadowRect = RoundRectShape(shadowRadii, null, null)
        val shadowRectangleDrawable = ShapeDrawable(shadowRect)
        shadowRectangleDrawable.paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            view.measuredHeight.toFloat(),
            Color.parseColor("#10000000"),
            Color.parseColor("#23000000"),
            Shader.TileMode.CLAMP
        )


        val borderSecondaryRectangle = RoundRectShape(borderRadii, null, null)
        val borderSecondaryRectangleDrawable = ShapeDrawable(borderSecondaryRectangle)
        borderSecondaryRectangleDrawable.paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            view.measuredHeight.toFloat(),
            if (startBorderColor?.isValidColor() == true) Color.parseColor(startBorderColor) else {
                ContextCompat.getColor(
                    view.context, R.color.primaryButtonBorderStartColor
                )
            },
            if (endBorderColor?.isValidColor() == true) Color.parseColor(endBorderColor) else {
                ContextCompat.getColor(
                    view.context, R.color.primaryButtonBorderEndColor
                )
            },
            Shader.TileMode.CLAMP
        )

        val innerSecondaryRectangle = RoundRectShape(borderRadii, null, null)
        val innerSecondaryRectangleDrawable = ShapeDrawable(innerSecondaryRectangle)
        innerSecondaryRectangleDrawable.paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            view.measuredHeight.toFloat(),
            if (startColor?.isValidColor() == true) Color.parseColor(startColor) else {
                ContextCompat.getColor(
                    view.context, R.color.primaryButtonStartColor
                )
            },
            if (endColor?.isValidColor() == true) Color.parseColor(endColor) else {
                ContextCompat.getColor(
                    view.context, R.color.primaryButtonEndColor
                )
            },
            Shader.TileMode.CLAMP
        )

        val drawableListSecondary = arrayOf<Drawable>(
            shadowRectangleDrawable,
            borderSecondaryRectangleDrawable,
            innerSecondaryRectangleDrawable
        )
        val defaultLayeredListDrawableSecondary = LayerDrawable(drawableListSecondary)

        defaultLayeredListDrawableSecondary.setLayerInset(0, 0, 0, 0, 0)
        defaultLayeredListDrawableSecondary.setLayerInset(
            1, shadowWidth, shadowWidth, shadowWidth, shadowWidth
        )
        defaultLayeredListDrawableSecondary.setLayerInset(
            2, strokeWidth, strokeWidth, strokeWidth, strokeWidth
        )
        return defaultLayeredListDrawableSecondary
    }


    @JvmStatic
    @BindingAdapter(
        value = ["lib:progressButtonPrimaryStartColor", "lib:progressButtonPrimaryEndColor", "lib:progressButtonPrimaryStartBorderColor", "lib:progressButtonPrimaryEndBorderColor", "lib:progressButtonSecondaryStartColor", "lib:progressButtonSecondaryEndColor", "lib:progressButtonSecondaryStartBorderColor", "lib:progressButtonSecondaryEndBorderColor"],
        requireAll = false
    )
    fun setProgressButton(
        buttonView: View,
        primaryStartColor: String?,
        primaryEndColor: String?,
        primaryStartBorderColor: String?,
        primaryEndBorderColor: String?,

        secondaryStartColor: String?,
        secondaryEndColor: String?,
        secondaryStartBorderColor: String?,
        secondaryEndBorderColor: String?,
    ) {
        buttonView.visibilityChanged { view ->
            when (view.visibility) {
                VISIBLE -> {

                    val defaultLayeredListDrawable = createRoundedLayeredListWithBorder(
                        view,
                        startColor = primaryStartColor,
                        endColor = primaryEndColor,
                        startBorderColor = primaryStartBorderColor,
                        endBorderColor = primaryEndBorderColor
                    )


                    val defaultLayeredListDrawableSecondary = createRoundedLayeredListWithBorder(
                        view,
                        startColor = secondaryStartColor,
                        endColor = secondaryEndColor,
                        startBorderColor = secondaryStartBorderColor,
                        endBorderColor = secondaryEndBorderColor
                    )

                    val res = StateListDrawable()
                    // res.setExitFadeDuration(100)
                    res.addState(
                        intArrayOf(
                            android.R.attr.state_selected, -android.R.attr.state_enabled
                        ), defaultLayeredListDrawableSecondary
                    )
                    res.addState(
                        intArrayOf(android.R.attr.state_focused), defaultLayeredListDrawable
                    )
                    res.addState(
                        StateSet.WILD_CARD, defaultLayeredListDrawable
                    )
                    view.background = res
                }


                else -> {

                }
            }
        }
    }


    @JvmStatic
    @BindingAdapter(
        value = ["lib:stepIndicatorStartColor", "lib:stepIndicatorEndColor", "lib:stepIndicatorStartBorderColor", "lib:stepIndicatorEndBorderColor", "lib:stepIndicatorDefaultProgressStartColor", "lib:stepIndicatorDefaultProgressEndColor"],
        requireAll = false
    )
    fun stepIndicatorBackgroundDrawable(
        indicatorBackgroundView: View,
        startColor: String?,
        endColor: String?,
        startBorderColor: String?,
        endBorderColor: String?,
        defaultProgressStartColor: String?,
        defaultProgressEndColor: String?,

        ) {

        val radius =
            indicatorBackgroundView.context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
                .dpToPx(indicatorBackgroundView.context).toFloat()
        val borderRadii =
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val innerRadii =
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        indicatorBackgroundView.visibilityChanged { view ->
            when (view.visibility) {
                VISIBLE -> {
                    val outerRectangleShape = RoundRectShape(innerRadii, null, null)
                    val outerRectangleDrawable = ShapeDrawable(outerRectangleShape)
                    outerRectangleDrawable.paint.shader = LinearGradient(
                        0f,
                        0f,
                        0f,
                        view.measuredHeight.toFloat(),
                        if (startColor?.isValidColor() == true) Color.parseColor(startColor) else {
                            Color.parseColor("#893060")
                        },
                        if (endColor?.isValidColor() == true) Color.parseColor(endColor) else {
                            Color.parseColor("#72244b")
                        },
                        Shader.TileMode.CLAMP
                    )

                    val borderRectangle = RoundRectShape(borderRadii, null, null)
                    val borderRectangleDrawable = ShapeDrawable(borderRectangle)
                    borderRectangleDrawable.paint.shader = LinearGradient(
                        0f,
                        0f,
                        0f,
                        view.measuredHeight.toFloat(),
                        if (startBorderColor?.isValidColor() == true) Color.parseColor(
                            startBorderColor
                        ) else {
                            Color.parseColor("#ad4c82")
                        },
                        if (endBorderColor?.isValidColor() == true) Color.parseColor(endBorderColor) else {
                            Color.parseColor("#5F1D3D")
                        },
                        Shader.TileMode.CLAMP
                    )


                    val defaultProgressRectangleShape = RoundRectShape(innerRadii, null, null)
                    val defaultProgressDrawable = ShapeDrawable(defaultProgressRectangleShape)
                    defaultProgressDrawable.paint.shader = RadialGradient(
                        view.measuredWidth.toFloat() / 2,
                        view.measuredHeight.toFloat() / 2,
                        (view.measuredWidth / 1.5).toFloat(),
                        if (defaultProgressStartColor?.isValidColor() == true) Color.parseColor(
                            defaultProgressStartColor
                        ) else {
                            Color.parseColor("#95386B")
                        },
                        if (defaultProgressEndColor?.isValidColor() == true) Color.parseColor(
                            defaultProgressEndColor
                        ) else {
                            Color.parseColor("#95386B")
                        },
                        Shader.TileMode.CLAMP
                    )

                    val drawableList = arrayOf<Drawable>(
                        outerRectangleDrawable, borderRectangleDrawable, defaultProgressDrawable
                    )
                    val layeredListDrawable = LayerDrawable(drawableList)
                    val outerStrokeWidth = 10f.pxToDp(view.context).toInt()
                    val innerStrokeWidth = 12f.pxToDp(view.context).toInt()
                    layeredListDrawable.setLayerInset(0, 0, 0, 0, 0)
                    layeredListDrawable.setLayerInset(
                        1, outerStrokeWidth, outerStrokeWidth, outerStrokeWidth, outerStrokeWidth
                    )
                    layeredListDrawable.setLayerInset(
                        2, innerStrokeWidth, innerStrokeWidth, innerStrokeWidth, innerStrokeWidth
                    )
                    view.background = layeredListDrawable
                }

                else -> {

                }
            }
        }
    }


    @JvmStatic
    @BindingAdapter(
        value = ["lib:stepCirclePrimaryStartColor", "lib:stepCirclePrimaryEndColor", "lib:stepCirclePrimaryBorderStartColor", "lib:stepCirclePrimaryBorderEndColor", "lib:stepCircleSecondaryStartColor", "lib:stepCircleSecondaryEndColor", "lib:stepCircleSecondaryBorderStartColor", "lib:stepCircleSecondaryBorderEndColor"],
        requireAll = false
    )
    fun stepCircleComponent(
        stepCircle: AppCompatImageView,
        defaultStartColor: String?,
        defaultEndColor: String?,
        defaultStartBorderColor: String?,
        defaultEndBorderColor: String?,
        selectedStartColor: String?,
        selectedEndColor: String?,
        selectedStartBorderColor: String?,
        selectedEndBorderColor: String?,
    ) {
        stepCircle.visibilityChanged { view ->
            when (view.visibility) {
                VISIBLE -> {

                    val defaultLayeredListDrawable = createRoundedGradientLayeredListWithBorder(
                        view,
                        startColor = defaultStartColor,
                        endColor = defaultEndColor,
                        startBorderColor = defaultStartBorderColor,
                        endBorderColor = defaultEndBorderColor,
                        4f.pxToDp(view.context).toInt()
                    )

                    val defaultLayeredListDrawableSecondary =
                        createRoundedGradientLayeredListWithBorder(
                            view,
                            startColor = selectedStartColor,
                            endColor = selectedEndColor,
                            startBorderColor = selectedStartBorderColor,
                            endBorderColor = selectedEndBorderColor,
                            4f.pxToDp(view.context).toInt()
                        )

                    val activeStateDrawable = createRoundedGradientLayeredListWithBorder(
                        view,
                        startColor = selectedStartBorderColor,
                        endColor = selectedEndBorderColor,
                        startBorderColor = defaultStartColor,
                        endBorderColor = defaultEndColor,
                        6f.pxToDp(view.context).toInt()
                    )

                    val res = StateListDrawable()

                    res.addState(
                        intArrayOf(android.R.attr.state_selected),
                        defaultLayeredListDrawableSecondary
                    )
                    res.addState(
                        intArrayOf(android.R.attr.state_pressed),
                        defaultLayeredListDrawableSecondary
                    )
                    res.addState(
                        StateSet.WILD_CARD, defaultLayeredListDrawable
                    )
                    view.background = res
                }


                else -> {

                }
            }
        }
    }


    private fun createRoundedGradientLayeredListWithBorder(
        view: View, startColor: String?,
        endColor: String?,
        startBorderColor: String?,
        endBorderColor: String?,
        strokeWidth: Int,
    ): LayerDrawable {
        val radius =
            view.context.resources.getDimension(com.intuit.sdp.R.dimen._8sdp).dpToPx(view.context)
                .toFloat()
        val shadowRadius =
            view.context.resources.getDimension(com.intuit.sdp.R.dimen._9sdp).dpToPx(view.context)
                .toFloat()
        val borderRadii =
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val shadowRadii = floatArrayOf(
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius,
            shadowRadius
        )
        val shadowWidth = 6f.pxToDp(view.context).toInt()
        val strokeWidth = shadowWidth.plus(strokeWidth).pxToDp(view.context).toInt()

        val shadowRect = RoundRectShape(shadowRadii, null, null)
        val shadowRectangleDrawable = ShapeDrawable(shadowRect)
        shadowRectangleDrawable.paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            view.measuredHeight.toFloat(),
            Color.parseColor("#13000000"),
            Color.parseColor("#20000000"),
            Shader.TileMode.CLAMP
        )


        val borderSecondaryRectangle = RoundRectShape(borderRadii, null, null)
        val borderSecondaryRectangleDrawable = ShapeDrawable(borderSecondaryRectangle)
        borderSecondaryRectangleDrawable.paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            view.measuredHeight.toFloat(),
            Color.parseColor(startBorderColor),
            Color.parseColor(endBorderColor),
            Shader.TileMode.CLAMP
        )

        val innerRectangleShape = RoundRectShape(borderRadii, null, null)
        val innerRectangleDrawable = ShapeDrawable(innerRectangleShape)
        innerRectangleDrawable.paint.shader = RadialGradient(
            view.measuredWidth.toFloat() / 2,
            view.measuredHeight.toFloat() / 2,
            if (view.measuredWidth.toFloat() <= 0) 300f
            else view.measuredWidth.toFloat(),
            Color.parseColor(startColor),
            Color.parseColor(endColor),
            Shader.TileMode.CLAMP
        )

        val drawableListSecondary = arrayOf<Drawable>(
            shadowRectangleDrawable, borderSecondaryRectangleDrawable, innerRectangleDrawable
        )
        val defaultLayeredListDrawableSecondary = LayerDrawable(drawableListSecondary)
        defaultLayeredListDrawableSecondary.setLayerInset(0, 0, 0, 0, 0)
        defaultLayeredListDrawableSecondary.setLayerInset(
            1, shadowWidth, shadowWidth, shadowWidth, shadowWidth
        )
        defaultLayeredListDrawableSecondary.setLayerInset(
            2, strokeWidth, strokeWidth, strokeWidth, strokeWidth
        )
        return defaultLayeredListDrawableSecondary
    }


    fun setProgressDrawableState(progressBarView: View, startColor: String, endColor: String) {
        progressBarView.visibilityChanged { view ->
            when (view.visibility) {
                VISIBLE -> {

                    val selectedDrawableState = GradeDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        if (startColor?.isValidColor() == true) Color.parseColor(startColor) else {
                            ContextCompat.getColor(
                                view.context, R.color.selectedIndicatorStartColor
                            )
                        },
                        if (endColor?.isValidColor() == true) Color.parseColor(endColor) else {
                            ContextCompat.getColor(
                                view.context, R.color.selectedIndicatorEndColor
                            )
                        },
                        0f,
                        0f,
                        0f,
                        0f
                    )
                    val defaultDrawableState = GradeDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        android.R.color.transparent,
                        android.R.color.transparent,
                        0f,
                        0f,
                        0f,
                        0f
                    )

                    val res = StateListDrawable()
                    res.addState(
                        intArrayOf(android.R.attr.state_selected), selectedDrawableState
                    )
                    res.addState(
                        intArrayOf(-android.R.attr.state_selected), defaultDrawableState
                    )
                    res.addState(StateSet.WILD_CARD, defaultDrawableState)
                    view.background = res
                }


                else -> {

                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(
        value = ["lib:circlePrimaryStartColor", "lib:circlePrimaryEndColor", "lib:circlePrimaryStartBorderColor", "lib:circlePrimaryEndBorderColor"],
        requireAll = false
    )
    fun circlePrimaryDrawable(
        view: View,
        primaryStartColor: String?,
        primaryEndColor: String?,
        primaryStartBorderColor: String?,
        primaryEndBorderColor: String?,
    ) {
        view.visibilityChanged { view ->
            when (view.visibility) {
                VISIBLE -> {

                    val circleDrawable = createRoundedLayeredListWithBorder(
                        view,
                        startColor = primaryStartColor,
                        endColor = primaryEndColor,
                        startBorderColor = primaryStartBorderColor,
                        endBorderColor = primaryEndBorderColor
                    )
                    view.background = circleDrawable

                }

                else -> {

                }
            }
        }

    }

    class GradeDrawable(
        angleType: Orientation,
        pStartColor: Int,
        pEndColor: Int,
        pTopLeftCorner: Float,
        pTopRightCorner: Float,
        pBottomLeftCorner: Float,
        pBottomRightCorner: Float,
    ) : GradientDrawable(Orientation.LEFT_RIGHT, intArrayOf(pStartColor, pEndColor)) {
        init {
            shape = RECTANGLE
            setOrientation(angleType)
            cornerRadii = floatArrayOf(
                pTopLeftCorner,
                pTopLeftCorner,
                pTopRightCorner,
                pTopRightCorner,
                pBottomLeftCorner,
                pBottomLeftCorner,
                pBottomRightCorner,
                pBottomRightCorner
            )
            //setCornerRadius(cornerRadius)
        }
    }


    fun View.visibilityChanged(action: (View) -> Unit) {
        this.viewTreeObserver.addOnGlobalLayoutListener {
            val newVis: Int = this.visibility
            if (this.tag as Int? != newVis) {
                this.tag = this.visibility

                // visibility has changed
                action(this)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(
        value = ["lib:imageUrl", "lib:placeHolder", "lib:imageSize"], requireAll = false
    )
    fun setImageUrl(imageView: ImageView, url: Any?, placeHolder: Drawable?, imageSize: Int = 0) {
        if (url != null) {
            when (url) {
                is String -> {
                    loadMedia(url, placeHolder, imageView, imageSize)
                }

                is File -> {
                    loadMedia(url, placeHolder, imageView, imageSize)
                }

                else -> {
                    loadMedia(url, placeHolder, imageView, imageSize)
                }
            }
        } else {
            imageView.setImageDrawable(placeHolder)
        }
    }


    private fun loadMedia(url: Any?, placeHolder: Drawable?, imageView: ImageView, imageSize: Int) {
        getRequestOption(placeHolder, imageSize).let {
            Glide.with(imageView.context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(it).into(imageView)
        }
    }

    private fun getRequestOption(resId: Drawable?, imageSize: Int): RequestOptions {
        return if (imageSize != 0) {
            RequestOptions().placeholder(resId).error(resId).fallback(resId).override(imageSize)
                .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
        } else {
            RequestOptions().placeholder(resId).error(resId).fallback(resId).dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
        }
    }


}