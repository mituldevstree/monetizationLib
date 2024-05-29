package com.monetizationlib.data.base.databinding

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.marginTop
import androidx.databinding.BindingAdapter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.monetizationlib.data.R
import com.monetizationlib.data.base.extensions.ViewUtil
import com.monetizationlib.data.base.extensions.ViewUtil.lifecycleOwner
import com.monetizationlib.data.base.extensions.fromHtml
import java.io.File
import java.text.DecimalFormat
import java.util.Locale


object BindingAdapters {
    @JvmStatic
    @BindingAdapter(value = ["android:isVisible"], requireAll = false)
    fun isVisible(view: View, isVisible: Boolean) {
        if (isVisible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["android:isGone"], requireAll = false)
    fun isGone(view: View, isGone: Boolean) {
        if (isGone) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["android:isInvisible"], requireAll = false)
    fun isInvisible(view: View, isInvisible: Boolean) {
        if (isInvisible) {
            view.visibility = View.INVISIBLE
        } else {
            view.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["android:isSelected"], requireAll = false)
    fun isSelected(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }

    @JvmStatic
    @BindingAdapter(value = ["android:setImageSelector"], requireAll = false)
    fun setImageSelector(view: ImageView, isSelected: Boolean) {
        if (isSelected) ViewUtil.setImageButtonSelector(view)
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:setBounceSelector", "android:showShadow", "android:justElevate", "android:needRounded"],
        requireAll = false
    )
    fun setBounceSelector(
        view: View,
        isSelected: Boolean,
        showShadow: Boolean?,
        justElevate: Boolean?,
        needRounded: Boolean?,
    ) {
        if (showShadow !== null && showShadow) {
            val showShadowBounds = justElevate ?: false
            val needRoundedShadow = needRounded ?: false
            val viewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                        0, 0, view.width, view.height, when {
                            needRoundedShadow -> {
                                (view.height).toFloat()
                            }

                            (view.width - view.height) > 200 -> {
                                (view.height / 8).toFloat()
                            }

                            else -> {
                                (view.height / 3.8).toFloat()
                            }
                        }
                    )
                }
            }

            view.z = ViewUtil.dipToPixel(view, 4).toFloat()
            val param = view.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(3, view.marginTop, 3, 10)
            view.layoutParams = param
            if (!showShadowBounds) {
                view.outlineProvider = viewOutlineProvider
                view.clipToOutline = true
            }
        }
        if (isSelected) {
            ViewUtil.setBounceButtonSelector(view)
        }
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:setRotationAnimation"],
        requireAll = false
    )
    fun setRotationAnimation(view: View, shouldAnimate: Boolean) {
        if(shouldAnimate){
            val alphaAnim = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f).apply {
                duration = 60.times(1000)
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
                interpolator = LinearInterpolator()
            }
            alphaAnim.start()

           /* view.lifecycleOwner.lifecycle.addObserver(object: DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    super.onResume(owner)

                }
                override fun onPause(owner: LifecycleOwner) {
                    super.onPause(owner)
                    alphaAnim.pause()
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    alphaAnim.end()
                }
            })*/
        }


    }

    @JvmStatic
    @BindingAdapter(value = ["android:setButtonSelector"], requireAll = false)
    fun setButtonSelector(view: View, isSelected: Boolean) {
        if (isSelected) ViewUtil.setButtonSelector(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["android:setViewBlur"], requireAll = false)
    fun setViewBlur(view: View, showBlur: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurScreen = RenderEffect.createBlurEffect(8f, 8f, Shader.TileMode.MIRROR)
            view.setRenderEffect(blurScreen)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["android:setTextViewSelector"], requireAll = false)
    fun setTextViewSelector(view: View, isSelected: Boolean) {
        if (isSelected) ViewUtil.setTextViewSelector(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["android:setValue"], requireAll = false)
    fun setValue(view: View, value: String?) {
        if (value != null) {
            when (view) {
                is TextView -> {
                    view.text = value.fromHtml()
                }

                is AppCompatTextView -> {
                    view.text = value.fromHtml()
                }

                is EditText -> {
                    view.setText(value)
                }

                is TextInputEditText -> {
                    view.setText(value)
                }

                is Button -> {
                    view.text = value.fromHtml()
                }

                is MaterialButton -> {
                    view.text = value.fromHtml()
                }

                is AppCompatRadioButton -> {
                    view.text = value.fromHtml()
                }
            }
        } else {
            when (view) {
                is TextView -> {
                    view.text = "".fromHtml()
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["android:setValueInDecimalPoint"], requireAll = false)
    fun setValueInDecimalPoint(view: View, value: String?) {
        val newValue = DecimalFormat("#0.00").format((value ?: "0.0").toDouble())
        if (newValue != null) {
            when (view) {
                is TextView -> {
                    view.text = newValue
                }

                is AppCompatTextView -> {
                    view.text = newValue
                }

                is EditText -> {
                    view.setText(newValue)
                }

                is TextInputEditText -> {
                    view.setText(newValue)
                }

                is Button -> {
                    view.text = newValue
                }

                is MaterialButton -> {
                    view.text = newValue
                }

                is AppCompatRadioButton -> {
                    view.text = newValue
                }
            }
        } else {
            when (view) {
                is TextView -> {
                    view.text = ""
                }
            }
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["android:setNumberPattern"], requireAll = false)
    fun setNumberPattern(textView: TextView, value: String?) {
        value?.let {
            val pattern: StringBuilder = StringBuilder(it).reverse()
            for (i in pattern.length downTo 1) {
                if (i % 3 == 0) {
                    pattern.insert(i, " ")
                } else {
                    pattern.insert(i, "")
                }
            }
            textView.text = StringBuilder(pattern.toString()).reverse()
        }
    }


    @JvmStatic
    @SuppressLint("DefaultLocale")
    @BindingAdapter(
        value = ["android:setFormattedPricePrefix", "android:setFormattedPrice", "android:setFormattedPriceSuffix"],
        requireAll = false
    )
    fun setFormattedPrice(
        textView: TextView?,
        setFormattedPricePrefix: String?,
        setFormattedPrice: Int,
        setFormattedPriceSuffix: String?,
    ) {
//        if (setFormattedPrice == 1 && setFormattedPriceSuffix?.lowercase().equals(textView?.context?.getString(R.string.invite_referrals)?.lowercase())) {
//            textView!!.text = String.format("$setFormattedPricePrefix $setFormattedPrice ${textView.context?.getString(R.string.invite_referral_content)}")
//        } else {
        textView!!.text =
            String.format("$setFormattedPricePrefix $setFormattedPrice $setFormattedPriceSuffix")
//        }
    }

    @JvmStatic
    @SuppressLint("DefaultLocale")
    @BindingAdapter(value = ["android:setPrice", "android:setCurrency"], requireAll = false)
    fun setPrice(textView: TextView?, price: Double?, currency: String?) {
        textView!!.text = (String.format(Locale.ENGLISH, "%.3f", price)).plus(" ").plus(currency)
    }

    @JvmStatic
    @BindingAdapter(value = ["android:setStrike"], requireAll = false)
    fun setStrike(textView: TextView, isStrike: Boolean) {
        if (isStrike) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:imageUrl", "android:placeHolder", "android:isCenterCrop"],
        requireAll = false
    )
    fun setImageUrl(
        imageView: ImageView,
        url: Any?,
        placeHolder: Drawable?,
        isCenterCrop: Boolean = true,
    ) {
        if (url != null) {
            if (url is String) {
                loadMedia(/*BuildConfig.SERVER_URL +*/ url, placeHolder, imageView, isCenterCrop)
            } else if (url is File) {
                loadMedia(url, placeHolder, imageView, isCenterCrop)
            }
        } else {
            imageView.setImageDrawable(placeHolder)
        }
    }


    public fun loadMedia(
        url: Any?,
        placeHolder: Drawable?,
        imageView: ImageView,
        isCenterCrop: Boolean,
    ) {
        if (url!! == "dummy_amazon") {
            imageView.setImageResource(R.drawable.ic_givvy_logo)
        } else if (url == "dummy_binance") {
            imageView.setImageResource(R.drawable.ic_givvy_logo)
        } else if (url == "dummy_coinbase") {
            imageView.setImageResource(R.drawable.ic_givvy_logo)
        } else {
            imageView.post {
                if (isCenterCrop)
                    getRequestOption(placeHolder, imageView.width, imageView.height)?.let {
                        Glide.with(imageView.context.applicationContext).load(url).apply(it)
                            .centerCrop()
                            .into(imageView)
                    }
                else
                    getRequestOption(placeHolder, imageView.width, imageView.height)?.let {
                        Glide.with(imageView.context.applicationContext).load(url).apply(it)
                            .into(imageView)
                    }
            }
        }
    }


    @BindingAdapter(value = ["android:imageResource"], requireAll = false)
    fun setImageResource(imageView: ImageView, icon: Int?) {
        icon?.let { imageView.setImageResource(it) }
    }

    fun getRequestOption(resId: Drawable?, width: Int, height: Int): RequestOptions? {
        return RequestOptions().override(width, height).placeholder(resId).error(resId)
            .fallback(resId).dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    @JvmStatic
    @BindingAdapter("android:restoreError")
    fun restoreError(editText: TextInputEditText, isRestoreError: Boolean?) {
        val viewParent = editText.parent
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (viewParent is ViewGroup) {
                    val child = viewParent.getParent()
                    if (child is TextInputLayout) {
                        child.isErrorEnabled = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    /*

        @JvmStatic
        @BindingAdapter(value = ["android:setTiltEffect"], requireAll = false)
        fun setTiltEffect(view: View, startEffect: Boolean) {
            if (startEffect) view.startAnimation(
                AnimationUtils.loadAnimation(
                    view.context, R.anim.offer_tilt_effect
                )
            )
        }

        @JvmStatic
        @BindingAdapter(value = ["android:setScaledEffect"], requireAll = false)
        fun setScaledEffect(view: View, startEffect: Boolean) {
            if (startEffect) view.startAnimation(
                AnimationUtils.loadAnimation(
                    view.context, R.anim.offer_scaled_anim
                )
            )
        }

        @JvmStatic
        @BindingAdapter(value = ["android:setFloatingEffect"], requireAll = false)
        fun setFloatingEffect(view: View, startEffect: Boolean) {
            if (startEffect) view.startAnimation(
                AnimationUtils.loadAnimation(
                    view.context, R.anim.offer_floacting_effect
                )
            )
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setImageTintDynamic"], requireAll = false)
        fun setImageTintDynamic(view: ImageView, color: String?) {
            if (color == null) {
                view.imageTintList = ColorStateList.valueOf(Color.parseColor("#12a269"))
                return
            }
            val context = view.context
            if (checkValidColor(color)) {
                view.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
            } else {
                view.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.offer_text_color))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setImageBackgroundDynamic"], requireAll = false)
        fun setImageBackgroundDynamic(view: ImageView, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
            } else {
                view.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.offer_text_color))
            }
        }


        @JvmStatic
        @BindingAdapter(value = ["app:setTextColorDynamic"], requireAll = false)
        fun setTextColorDynamic(view: TextView, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.setTextColor(Color.parseColor(color))
            } else {
                view.setTextColor(ContextCompat.getColor(context, R.color.offer_text_color))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setTitleColor"], requireAll = false)
        fun setTitleColor(view: Toolbar, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.setTitleTextColor(Color.parseColor(color))
            } else {
                view.setTitleTextColor(ContextCompat.getColor(context, R.color.offer_text_color))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setColorDynamic"], requireAll = false)
        fun setColorDynamic(view: View, color: String?) {
            if (color == null) return
            val context = view.context
            when (view) {
                is LinearLayout -> {
                    if (checkValidColor(color)) {
                        view.setBackgroundColor(Color.parseColor(color))
                    } else {
                        view.setBackgroundColor(
                            ContextCompat.getColor(
                                context, R.color.offer_background
                            )
                        )
                    }
                }

                is TextView -> {
                    if (checkValidColor(color)) {
                        view.setTextColor(Color.parseColor(color))
                    } else {
                        view.setTextColor(ContextCompat.getColor(context, R.color.offer_text_color))
                    }
                }

                is ImageView -> {
                    if (checkValidColor(color)) {
                        view.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
                    } else {
                        view.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context, R.color.offer_text_color
                            )
                        )
                    }
                }

                is ProgressBar -> {
                    if (checkValidColor(color)) {
                        view.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color))
                    } else {
                        view.indeterminateTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context, R.color.offer_text_color
                            )
                        )
                    }
                }

                is TabLayout -> {
                    if (checkValidColor(color)) {
                        view.tabTextColors = ColorStateList.valueOf(Color.parseColor(color))
                        view.setSelectedTabIndicatorColor(Color.parseColor(color))
                    } else {
                        view.tabTextColors = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.offer_text_color
                            )
                        )
                        view.setSelectedTabIndicatorColor(
                            ContextCompat.getColor(
                                context,
                                R.color.offer_text_color
                            )
                        )
                    }
                }
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setTextHintColorDynamic"], requireAll = false)
        fun setTextHintColorDynamic(view: EditText, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.setHintTextColor(Color.parseColor(color))
            } else {
                view.setHintTextColor(ContextCompat.getColor(context, R.color.offer_text_color))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setBackgroundColorDynamic"], requireAll = false)
        fun setBackgroundColorDynamic(view: CollapsingToolbarLayout, MainColor: String?) {
            if (MainColor == null) return
            val context = view.context
            if (checkValidColor(MainColor)) {
                view.setBackgroundColor(Color.parseColor(MainColor))
            } else {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.offer_background))
            }
        }


        @JvmStatic
        @BindingAdapter(value = ["app:setBackgroundColorDynamicTab"], requireAll = false)
        fun setBackgroundColorDynamicTab(view: TabLayout, MainColor: String?) {
            if (MainColor == null) return
            val context = view.context
            if (checkValidColor(MainColor)) {
                view.setBackgroundColor(Color.parseColor(MainColor))
            } else {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.offer_background))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setBackgroundTintDynamic"], requireAll = false)
        fun setBackgroundTintDynamic(view: TextView, MainColor: String?) {
            if (MainColor == null) return
            val context = view.context
            if (checkValidColor(MainColor)) {
                view.backgroundTintList = ColorStateList.valueOf(Color.parseColor(MainColor))
            } else {
                view.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context, R.color.offer_background
                    )
                )
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setBackgroundColorDynamic"], requireAll = false)
        fun setBackgroundColorDynamic(view: RelativeLayout, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.setBackgroundColor(Color.parseColor(color))
            } else {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.offer_background))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setBackgroundColorDynamic"], requireAll = false)
        fun setBackgroundColorDynamic(view: LinearLayout, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.setBackgroundColor(Color.parseColor(color))
            } else {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.offer_background))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setScreamColor"], requireAll = false)
        fun setScreamColor(view: CollapsingToolbarLayout, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.setContentScrimColor(Color.parseColor(color))
            } else {
                view.setContentScrimColor(ContextCompat.getColor(context, R.color.offer_center))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setBackgroundColorDynamicCard"], requireAll = false)
        fun setBackgroundColorDynamicCard(view: CardView, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.setBackgroundColor(Color.parseColor(color))
            } else {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.offer_background))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["app:setButtonBackgroundColorDynamic"], requireAll = false)
        fun setButtonBackgroundColorDynamic(view: Button, color: String?) {
            if (color == null) return
            val context = view.context
            if (checkValidColor(color)) {
                view.setBackgroundColor(Color.parseColor(color))
            } else {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.offer_background))
            }
        }
    */

    @JvmStatic
    @BindingAdapter(
        value = ["android:pStartColor", "android:pEndColor", "android:pTopLeftCorner", "android:pTopRightCorner", "android:pBottomLeftCorner", "android:pBottomRightCorner", "android:shapeOrientation", "android:isStroke"],
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
        shapeOrientation: GradientDrawable.Orientation?,
        isStroke: Boolean? = false,
    ) {
        view.background = GradeDrawable(
            Color.parseColor(pStartColor ?: "#0012a269"), Color.parseColor(
                pEndColor ?: "#0084c331"
            ), pTopLeftCorner ?: 0F, pTopRightCorner ?: 0F, pBottomLeftCorner
                ?: 0F, pBottomRightCorner ?: 0F, shapeOrientation
                ?: GradientDrawable.Orientation.TOP_BOTTOM, isStroke ?: false
        )
    }

    class GradeDrawable(
        pStartColor: Int,
        pEndColor: Int,
        pTopLeftCorner: Float,
        pTopRightCorner: Float,
        pBottomLeftCorner: Float,
        pBottomRightCorner: Float,
        shapeOrientation: Orientation,
        isStroke: Boolean? = false,
    ) : GradientDrawable(shapeOrientation, intArrayOf(pStartColor, pEndColor)) {
        init {
            shape = RECTANGLE
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
//            setCornerRadius(cornerRadius)
            if (isStroke == true) setStroke(4, Color.WHITE)
        }
    }

    fun checkValidColor(color: String): Boolean {
        return color.startsWith("#") && (color.trim().length == 4 || color.trim().length == 7 || color.trim().length == 9)
    }

    fun isColorDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }
}