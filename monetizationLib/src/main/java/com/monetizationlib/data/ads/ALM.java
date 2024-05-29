package com.monetizationlib.data.ads;

import android.app.Activity;
import android.content.Context;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.monetizationlib.data.Monetization;

class ALM {
    private final Object MAIN_LOCK = new Object();
    private final MaxAdsLoadingHandlerEventsListener eventsHandler;

    private AdDesignType adDesignType;
    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd loadedNativeAd;
    private Activity activity;

    ALM(Context context,
        MaxAdsLoadingHandlerEventsListener eventsHandler,
        AdDesignType adDesignType, Activity activity) {
        this.eventsHandler = eventsHandler;
        this.adDesignType = adDesignType;
        this.activity = activity;

        String unitId = "";

        try {
            switch ((adDesignType)) {
                case LARGE:
                    unitId = Monetization.INSTANCE.getAdConfig().getNativeLargeAdUnit();
                    break;
                case MEDIUM:
                    unitId = Monetization.INSTANCE.getAdConfig().getNativeMediumAdUnit();
                    break;
                case SMALL:
                    unitId = Monetization.INSTANCE.getAdConfig().getNativeSmallAdUnit();
                    break;
                case EXTRA_SMALL:
                    unitId = Monetization.INSTANCE.getAdConfig().getNativeAdditionalTwoAdUnit();
                    break;
            }
        } catch (Throwable throwable) {

        }

        if (unitId == null) {
            unitId = "";
        }

        nativeAdLoader = new MaxNativeAdLoader(unitId, activity);
        nativeAdLoader.setRevenueListener(MaxRevenueTracker.INSTANCE.getMaxRevenuTracker());
        nativeAdLoader.setNativeAdListener(new NativeAdListener());
    }

    private class NativeAdListener extends MaxNativeAdListener {
        @Override
        public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd nativeAd) {
            // Clean up any pre-existing native ad to prevent memory leaks.
            if (loadedNativeAd != null) {
                nativeAdLoader.destroy(loadedNativeAd);
            }

            // Save ad for cleanup.
            loadedNativeAd = nativeAd;
            synchronized (MAIN_LOCK) {
                loadedNativeAd = nativeAd;

                eventsHandler.onAdLoaded(ALM.this, nativeAdView);
            }

        }

        @Override
        public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
            if (eventsHandler != null) {
                eventsHandler.onAdFailed(ALM.this);
            }
        }

        @Override
        public void onNativeAdClicked(final MaxAd nativeAd) {
        }
    }

    public ALM loadAds() {
        try {
            nativeAdLoader.loadAd(createNativeAdView());
        } catch (Exception exc) {
        }

        return this;
    }

    private MaxNativeAdView createNativeAdView() {
        Integer resId = 0;

        switch (adDesignType) {
            case LARGE:
                resId = Monetization.getNativeXmlTwo();
                break;
            case MEDIUM:
                resId = Monetization.getNativeXmlOne();
                break;
            case SMALL:
                resId = Monetization.getNativeXmlThree();
                break;
            case EXTRA_SMALL:
                resId = Monetization.getNativeXmlFour();
                break;
        }

        MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(resId)
//                .setTitleTextViewId(R.id.title_text_view)
//                .setBodyTextViewId(R.id.body_text_view)
//                .setAdvertiserTextViewId(R.id.advertiser_textView)
//                .setIconImageViewId(R.id.icon_image_view)
//                .setMediaContentViewGroupId(R.id.media_view_container)
//                .setOptionsContentViewGroupId(R.id.options_view)
//                .setCallToActionButtonId(R.id.cta_button)
                .build();

        return new MaxNativeAdView(binder, activity);
    }
}