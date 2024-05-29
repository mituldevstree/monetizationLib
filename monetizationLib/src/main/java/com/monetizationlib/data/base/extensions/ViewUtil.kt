package com.monetizationlib.data.base.extensions

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.util.Linkify
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Dimension
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.app.ShareCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.monetizationlib.data.R
import java.text.SimpleDateFormat
import java.util.*


object ViewUtil {


    @JvmStatic
    fun updateViewHeight(view: View, value: Int) {
        val anim: ValueAnimator = ValueAnimator.ofInt(view.measuredHeight, 2100)
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = `val`
            view.layoutParams = layoutParams
        }
        anim.duration = 200
        anim.start()

    }

    @JvmStatic
    fun getDeviceHeight(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics: WindowMetrics =
                context.getSystemService(WindowManager::class.java).currentWindowMetrics
            metrics.bounds.height()
        } else {
            val metrics = DisplayMetrics()
            (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(metrics)
            metrics.heightPixels
        }
    }

    @JvmStatic
    fun getDeviceWidth(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics: WindowMetrics =
                context.getSystemService(WindowManager::class.java).currentWindowMetrics
            metrics.bounds.width()
        } else {
            val metrics = DisplayMetrics()
            (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(metrics)
            metrics.widthPixels
        }
    }


    @JvmStatic
    fun getFormattedDate(time: Long, format: String?): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return SimpleDateFormat(format, Locale.ENGLISH).format(calendar.time)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setImageButtonSelector(vararg views: ImageView) {

        for (imageView in views) {
            imageView.setOnTouchListener(object : View.OnTouchListener {
                private var rect: Rect? = null
                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            rect = Rect(
                                imageView.left, imageView.top, imageView.right, imageView.bottom
                            )
                            if (imageView.drawable != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    imageView.drawable.setColorFilter(
                                        BlendModeColorFilter(
                                            0x44000000, BlendMode.SRC_ATOP
                                        )
                                    )
                                } else {
                                    @Suppress("DEPRECATION") imageView.drawable.setColorFilter(
                                        0x44000000, PorterDuff.Mode.SRC_ATOP
                                    )
                                }
                                imageView.invalidate()
                            }
                        }

                        MotionEvent.ACTION_MOVE -> if (!rect!!.contains(
                                imageView.left + event.x.toInt(), imageView.top + event.y.toInt()
                            )
                        ) { // User moved outside bounds
                            if (imageView.drawable != null) {
                                imageView.drawable.clearColorFilter()
                                imageView.invalidate()
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            // mVelocityTracker.recycle();
                            if (imageView.drawable != null) {
                                imageView.drawable.clearColorFilter()
                                imageView.invalidate()
                            }
                        }
                    }
                    return false
                }
            })
        }
    }

    fun setBounceButtonSelector(vararg views: View?) {
        for (view in views) {
            view?.setOnTouchListener(object : View.OnTouchListener {
                private var rect: Rect? = null

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            rect = Rect(
                                view.left, view.top, view.right, view.bottom
                            )
                            val anim: Animation = ScaleAnimation(
                                1f,
                                0.9f,
                                1f,
                                0.9f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f
                            )
                            anim.fillAfter = true
                            anim.duration = 70
                            view.startAnimation(anim)
                            view.invalidate()
                        }

                        MotionEvent.ACTION_MOVE -> {
                            if (rect != null) {
                                if (!rect?.contains(
                                        view.left + event.x.toInt(), view.top + event.y.toInt()
                                    )!!
                                ) { // User moved outside bounds
                                    val anim: Animation = ScaleAnimation(
                                        0.9f,
                                        1f,
                                        0.9f,
                                        1f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )
                                    anim.fillAfter = true
                                    anim.duration = 70
                                    view.startAnimation(anim)
                                    view.invalidate()
                                }
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            // mVelocityTracker.recycle();
                            val anim: Animation = ScaleAnimation(
                                0.9f,
                                1f,
                                0.9f,
                                1f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f
                            )
                            anim.fillAfter = true
                            anim.duration = 70
                            v?.startAnimation(anim)
                            v?.invalidate()
                        }
                    }
                    return false
                }
            })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setButtonSelector(vararg views: View) {
        for (imageView in views) {
            imageView.setOnTouchListener(object : View.OnTouchListener {
                private var rect: Rect? = null
                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            rect = Rect(
                                imageView.left,
                                imageView.top,
                                imageView.right,
                                imageView.bottom
                            )
                            if (imageView.background != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    imageView.background.colorFilter =
                                        BlendModeColorFilter(0x44000000, BlendMode.SRC_ATOP)
                                } else {
                                    @Suppress("DEPRECATION") imageView.background.setColorFilter(
                                        0x44000000,
                                        PorterDuff.Mode.SRC_ATOP
                                    )
                                }
                                imageView.invalidate()
                            }
                        }

                        MotionEvent.ACTION_MOVE -> {
                            rect?.let {
                                if (!it.contains(
                                        imageView.left + event.x.toInt(),
                                        imageView.top + event.y.toInt()
                                    )
                                ) { // User moved outside bounds
                                    if (imageView.background != null) {
                                        imageView.background.clearColorFilter()
                                        imageView.invalidate()
                                    }
                                }
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            if (imageView.background != null) {
                                imageView.background.clearColorFilter()
                                imageView.invalidate()
                            }
                        }
                    }
                    return false
                }
            })
        }
    }

    fun setTextViewSelector(vararg views: View) {
        for (view in views) {
            view.setOnTouchListener(object : View.OnTouchListener {
                private var rect: Rect? = null

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        rect = Rect(
                            v.left, v.top, v.right, v.bottom
                        )
                        view.alpha = 0.5f
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        view.alpha = 1f
                    } else if (event.action == MotionEvent.ACTION_MOVE) {
                        if (!rect!!.contains(
                                v.left + event.x.toInt(), v.top + event.y.toInt()
                            )
                        ) { // User moved outside bounds
                            view.alpha = 1f
                        }
                    }
                    return false
                }
            })
        }
    }

    fun linkify(txtMsg: TextView?) {
        Linkify.addLinks(txtMsg!!, Linkify.ALL)
    }

    fun parseDouble(value: String): Double {
        return try {
            value.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun parseFloat(value: Int?): Float {
        if (value == null) return 0f
        return try {
            value.toFloat()
        } catch (e: Exception) {
            0f
        }
    }

    fun parseLong(value: String): Long {
        return try {
            value.toLong()
        } catch (e: Exception) {
            0
        }
    }

    fun parseInt(raw: String): Int {
        return try {
            raw.toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun parseInt(raw: Double): Int {
        return try {
            raw.toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun parseBoolean(raw: String?): Boolean {
        if (raw == null || raw.isEmpty()) return false
        if (raw.contains("1") || raw.contains("0")) {
            if (raw.contains("1")) return true
            if (raw.contains("0")) return false
        } else return raw.lowercase().contains("true")
        return false
    }

    fun getLastCharacters(word: String, count: Int): String {
        if (word.length == count) return word
        return if (word.length > count) word.substring(word.length - count) else ""
    }

    fun addStrike(textView: TextView) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    fun removeStrike(textView: TextView) {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }


    fun appReferralSharing(
        activity: FragmentActivity?,
        referralLink: String,
        appName: String,
        userName: String,
    ) {
        justTry {
            val subject = String.format(
                "%s wants you to play $appName with you!", userName
            )
            val invitationLink: String = referralLink
            val msg = ("Let's play $appName together! Use my referrer link: $invitationLink")

            ShareCompat.IntentBuilder.from(activity!!).setType("text/plain").setSubject(subject)
                .setChooserTitle(subject).setText(msg).startChooser()
        }
    }

    fun copyToClipboard(context: Context, text: String, message: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun getRandomNumber(min: Int, max: Int): Int {
        return Random().nextInt(max - min + 1) + min
    }

    @JvmStatic
    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    @JvmStatic
    fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (enabled) {
            view.alpha = 1.0f
        } else {
            view.alpha = 0.9f
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setViewAndChildrenEnabled(child, enabled)
            }
        }
    }

    @JvmStatic
    fun printLog(key: String, message: String) {
//        if (BuildConfig.DEBUG) {
        Log.e(key, message)
//        }
    }

    fun dipToPixel(view: View, rate: Int): Int {
        val scale: Float = view.context.resources.displayMetrics.density
        return (rate * scale + 0.5f).toInt()
    }

    fun sessionIdEncode(s: String, k: Int): String {
        var shifted = ""
        for (element in s) {
            val `val` = element.code
            shifted += (`val` + k).toChar()
        }
        return shifted
    }

    @JvmStatic
    fun hapticFeedbackEnabled(view: View) {
        view.isHapticFeedbackEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun setWinningHighlighted(view: View) {
        var totalRepeat = 0
        val scaleAnimation = ValueAnimator.ofFloat(0.8f, 1.05f)
        scaleAnimation.duration = 250
        scaleAnimation.addUpdateListener { animation ->
            view.scaleX = animation.animatedValue as Float
            view.scaleY = animation.animatedValue as Float
        }
        scaleAnimation.addListener {
            if (totalRepeat == 2) {
                scaleAnimation.removeAllListeners()
                scaleAnimation.end()
                view.clearAnimation()
            }
            totalRepeat++
        }
        scaleAnimation.repeatCount = ValueAnimator.INFINITE
        scaleAnimation.repeatMode = ValueAnimator.REVERSE
        scaleAnimation.start()
    }

    fun getApplicationInstallDate(context: Context): Int {
        val elapsedTimestamp: Long =
            context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
        val timediff = System.currentTimeMillis() - elapsedTimestamp
        return (timediff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun ViewGroup.makeAllChildViewVisible() {
        this.visibility = View.VISIBLE
        this.children.forEach {
            it.visibility = View.VISIBLE
        }
    }

    fun ViewGroup.makeAllChildViewHidden() {
        this.visibility = View.GONE
        this.children.forEach {
            it.visibility = View.GONE
        }
    }

    fun ShapeableImageView.applyLeftRoundedDrawableBg(@Dimension radius: Float) {
        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
            .setTopLeftCorner(CornerFamily.ROUNDED, radius)
            .build()
    }

    fun ShapeableImageView.applyRightRoundedDrawableBg(@Dimension radius: Float) {
        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .setTopRightCorner(CornerFamily.ROUNDED, radius)
            .build()
    }

    fun ShapeableImageView.removeRoundedDrawableBg(@Dimension radius: Float) {
        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()
    }

    fun ShapeableImageView.applyRoundedDrawableBg(@Dimension radius: Float) {
        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
            .setAllCornerSizes(radius)
            .build()
    }

    /**
     * A helper method wrapping [ViewCompat.setOnApplyWindowInsetsListener], additionally providing the initial padding
     * and margins of the view.
     *
     * This method does not consume any window insets, allowing any and all children to receive the same insets.
     *
     * This is a `set` listener, so only the last [windowInsetsListener] applied by [doOnApplyWindowInsets] will be ran.
     *
     * This approach was based on [https://medium.com/androiddevelopers/windowinsets-listeners-to-layouts-8f9ccc8fa4d1].
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun View.doOnApplyWindowInsets(
        windowInsetsListener: (
            insetView: View,
            windowInsets: WindowInsetsCompat,
            initialPadding: Insets,
            initialMargins: Insets,
        ) -> Unit,
    ) {
        val initialPadding = Insets.of(paddingLeft, paddingTop, paddingRight, paddingBottom)
        val initialMargins = Insets.of(marginLeft, marginTop, marginRight, marginBottom)

        ViewCompat.setOnApplyWindowInsetsListener(this) { insetView, windowInsets ->
            windowInsets.also {
                windowInsetsListener(insetView, windowInsets, initialPadding, initialMargins)
            }
        }

        // Whenever a view is detached and then re-attached to the screen, we need to apply insets again.
        //
        // In particular, it is not enough to apply insets only on the first attach:
        //
        // doOnAttach { requestApplyInsets() }
        //
        // For example, considering the following scenario:
        // - A RecyclerView lays out items while in landscape.
        // - Some items that depend on the insets are laid out, and are then detached because they go off-screen.
        // - The user rotates the device 180 degrees. This is still landscape, so no configuration change occurs, but an
        // inset change _does_ occur.
        // - The detached items are reattached because they come back on-screen.
        //
        // At this point, the insets applied to the view would be out of date, and they wouldn't be updated, since the view
        // was already attached once, and the callback for the new insets caused by the rotation would have already been
        // applied, and skipped updating the detached view.
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })

        // If the view is already attached, immediately request insets be applied.
        if (isAttachedToWindow) {
            requestApplyInsets()
        }
    }

    val View.lifecycleOwner: LifecycleOwner
        get() = getTag(R.string.view_coroutine_scope) as? LifecycleOwner ?: object : LifecycleOwner,
            LifecycleEventObserver {
            val viewLifecycle = LifecycleRegistry(this)

            init {
                doOnAttach {
                    findViewTreeLifecycleOwner()?.lifecycle?.addObserver(this)
                }
                doOnDetach {
                    findViewTreeLifecycleOwner()?.lifecycle?.removeObserver(this)
                    viewLifecycle.currentState = Lifecycle.State.DESTROYED
                }
            }

            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                viewLifecycle.currentState = event.targetState
            }


            override val lifecycle: Lifecycle
                get() = viewLifecycle


        }.also {
            setTag(R.string.view_coroutine_scope, it)
        }


}