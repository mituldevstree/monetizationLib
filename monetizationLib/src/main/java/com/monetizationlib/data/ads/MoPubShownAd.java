package com.monetizationlib.data.ads;

import android.view.View;

import com.applovin.mediation.nativeAds.MaxNativeAdView;


public class MoPubShownAd {

    View cachedAdView;
    MaxNativeAdView cachedAd;
    FacebookCPMType facebookCPMType;
    long shownAt;
    boolean hasImpression;

    public MoPubShownAd(View cachedAdView, MaxNativeAdView cachedAd, FacebookCPMType FacebookCPMType, long shownAt) {
        this.cachedAdView = cachedAdView;
        this.cachedAd = cachedAd;
        this.shownAt = shownAt;
        this.facebookCPMType = FacebookCPMType;
        this.hasImpression = false;
    }

    public void setHasImpression(boolean hasImpression) {
        this.hasImpression = hasImpression;
    }

    public boolean canBeBoundAgain() {
        return !hasImpression;
    }
}

