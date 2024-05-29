package com.monetizationlib.data.ads;

import com.applovin.mediation.nativeAds.MaxNativeAdView;

public interface MaxAdsLoadingHandlerEventsListener {
    void onAdStartedLoading();

    void onAdLoaded(ALM adsLoadingHandler, MaxNativeAdView ad);

    void onAdFailed(ALM adsLoadingHandler);

    void onAdsFailedAboveThreshold(boolean isForced, int errorCode);

    void onAdImpression(MaxNativeAdView loadedAd);
}
