package com.monetizationlib.data.ads

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.facebook.ads.MediaView
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdLayout
import com.monetizationlib.data.R


class TemplateView : FrameLayout {
    private var templateType = 0

    private var facebookNativeAdView: NativeAdLayout? = null

    private var primaryView: TextView? = null

    private var tertiaryView: TextView? = null

    private var facebookMediaView: com.facebook.ads.MediaView? = null

    private var callToActionView: Button? = null

    private var socialView: TextView? = null

    private var isFacebook: Boolean = false

    constructor(context: Context?) : super(context!!) {}

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    fun inflateAd(nativeAd: NativeAd) {
        nativeAd.unregisterView()

        // Add the Ad view into the ad container.
        val inflater = LayoutInflater.from(this.context)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        // Add the AdOptionsView
//        val adChoicesContainer = findViewById<LinearLayout>(R.id.ad_choices_container)
//        val adOptionsView =
//            AdOptionsView(this.context, nativeAd, facebookNativeAdView)
//        adChoicesContainer.removeAllViews()
//        adChoicesContainer.addView(adOptionsView, 0)
        val nativeAdIcon: MediaView? = facebookNativeAdView?.findViewById(R.id.native_ad_icon)

        val adChoicesContainer = findViewById<LinearLayout>(R.id.ad_choices_container)
        val adOptionsView = AdOptionsView(this.context, nativeAd, facebookNativeAdView)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Set the Text.
        primaryView?.text = nativeAd.advertiserName
        tertiaryView?.text = nativeAd.adBodyText
        callToActionView?.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        callToActionView?.text = nativeAd.adCallToAction

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        primaryView?.let { clickableViews.add(it) }
        callToActionView?.let { clickableViews.add(it) }
        tertiaryView?.let { clickableViews.add(it) }
        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            facebookNativeAdView,
            facebookMediaView,
            nativeAdIcon,
            clickableViews
        )
    }

    private fun initView(
        context: Context,
        attributeSet: AttributeSet?
    ) {
        val attributes =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0)
        try {
            templateType =
                attributes.getResourceId(
                    R.styleable.TemplateView_gnt_template_type, R.layout.gnt_medium_template_view_facebook
                )

            isFacebook = attributes.getBoolean(R.styleable.TemplateView_isFacebook, false)
        } finally {
            attributes.recycle()
        }
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(templateType, this)
    }

    public override fun onFinishInflate() {
        super.onFinishInflate()
        facebookNativeAdView = findViewById(R.id.native_ad_view)
        primaryView = findViewById(R.id.primary)
        tertiaryView = findViewById(R.id.body)
        callToActionView = findViewById(R.id.cta)
        facebookMediaView = findViewById(R.id.media_view)
    }
}