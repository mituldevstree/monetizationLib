package com.monetizationlib.data.ads;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.monetizationlib.data.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public abstract class MNAL implements INativeAdsLoader, MaxAdsLoadingHandlerEventsListener {

    private static final Object main_lock = new Object();

    private static boolean areAdsSdksInitialized = false;

    private final Object show_ads_lock = new Object();

    private Set<ALM> almSet = new HashSet<>();

    public static HashMap<String, Integer> adSessionDataMap = new HashMap<>();

    public static UUID adSessionId = null;

    private final Comparator<Pair<FacebookCPMType, MaxNativeAdView>> adComparator = (t1, t2) -> 1;

    private final TreeSet<Pair<FacebookCPMType, MaxNativeAdView>> loadedAds = new TreeSet<>(adComparator);

    protected final Map<String, MoPubShownAd> moPubShownAdMap = new HashMap<>();

    private volatile boolean adsEnabled = true;

    private volatile long lastAdRequestTimeNanos;

    private volatile boolean isInForeground;

    protected Context context;

    private volatile boolean isInitialized = false;
    private Activity activity;

    public MNAL() {
        currentLoaders.add(this);

        adSessionId = UUID.randomUUID();

        adSessionDataMap.put("max", 0);
        adSessionDataMap.put("high", 0);
        adSessionDataMap.put("medium", 0);
        adSessionDataMap.put("low-medium", 0);
        adSessionDataMap.put("low", 0);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public abstract AdDesignType getDesignType();

    private void initProperly() {
        if (!isInitialized) {
            synchronized (main_lock) {
                if (!isInitialized) {
                    isInitialized = true;
                }
            }
            if (isInitialized) {
                timerCheck();
            }
        }
    }

    @Override
    public void onAdFailed(ALM adsLoadingHandler) {
        almSet.remove(adsLoadingHandler);
    }

    @Override
    public void onAdLoaded(ALM adsLoadingHandler, MaxNativeAdView ad) {
        almSet.remove(adsLoadingHandler);

        publishAd(ad);

        if (initialLoadAmount() > initialAdsLoadCount) {
            initialAdsLoadCount++;
        }

        loadNewAds();
    }

    @Override
    public void onAdStartedLoading() {
        System.nanoTime();
    }

    public void loadNewAds() {
        if (MNAL.this.canLoadAds()) {
            synchronized (show_ads_lock) {
                while (MNAL.this.canLoadAds()) {
                    ALM ALM = new ALM(context, MNAL.this, getDesignType(), activity);
                    almSet.add(ALM);
                    ALM.loadAds();
                }
            }
        }
    }

    private boolean canLoadAds() {
        return getAdLoadCount() > 0 && adsEnabled;
    }

    private final static Set<MNAL> currentLoaders = new HashSet<>();

    private volatile int initialAdsLoadCount = 0;

    protected boolean hasDoneInitialLoad() {
        return initialAdsLoadCount >= initialLoadAmount();
    }

    abstract int initialLoadAmount();

    abstract int secondaryLoadAmount();

    private int getMaxAdsLoadedInForeground() {
        if (hasDoneInitialLoad()) {
            return secondaryLoadAmount();
        } else {
            return initialLoadAmount();
        }
    }

    private int getAdLoadCount() {
        synchronized (show_ads_lock) {
            return getMaxAdLoad() - (loadedAds.size() + getLoadingCount());
        }
    }

    private int getLoadingCount() {
        return this.almSet.size();
    }


    private int getMaxAdLoad() {
        if (isInForeground) {
            int additionalLoadCount = 0;

            if (loadedAds.size() == 0 && moPubShownAdMap.size() < 3) {
                additionalLoadCount = 1;
            }

            long timeSinceAdRequest = TimeUtil.getTimeElapsed(lastAdRequestTimeNanos);
            boolean wasRecentlyRequested = timeSinceAdRequest < 1_000;
            if (wasRecentlyRequested) {
                return getMaxAdsLoadedInForeground() + additionalLoadCount;
            }

            return 1 + additionalLoadCount;
        }

        if (moPubShownAdMap.size() > 0) {
            return 0;
        }
        return getMaxAdLoadInBackground();

    }

    public void setForeground(boolean isInForeground) {
        this.isInForeground = isInForeground;
    }

    private int getAdLayoutResource(DesignUnitType designUnitType) {
        return R.layout.ad_sample_ad;
    }

    private void publishAd(MaxNativeAdView NativeAd) {
        synchronized (show_ads_lock) {
            addAd(NativeAd);
            showAds();
        }
    }

    private Random generator = new Random();

    private void showAds() {
        showAds(false, -1);
    }

    @Override
    public void onAdsFailedAboveThreshold(boolean isForced, int errorCode) {
        showAds(isForced, errorCode);
    }

    private void showAds(boolean forceAdPublish,
                         int errorCode) {
        synchronized (show_ads_lock) {
            while (true) {
                if (loadedAds.size() == 0 && moPubShownAdMap.size() == 0) {
                    break;
                }
                LoadingAdViews loadingAdViews = this.loadingAdViews.poll();
                if (loadingAdViews == null) {
                    break;
                }

                boolean foundAdToBind = false;

                try {
                    String adKey = loadingAdViews.getAdKey();

                    MaxNativeAdView ad = null;
                    Pair<FacebookCPMType, MaxNativeAdView> facebookAd = getBestLoadedAd();

                    View view = loadingAdViews.getView();
                    if (facebookAd != null) {
                        ad = facebookAd.second;
                        FacebookCPMType loadedFacebookCPMType = facebookAd.first;

                        if (TextUtils.isEmpty(adKey)) {
                            adKey = UUID.randomUUID().toString();
                        }

                        view = ad;
                        moPubShownAdMap.put(adKey, new MoPubShownAd(view, ad, loadedFacebookCPMType, System.nanoTime()));
                    } else {
                        boolean hasRequestExpired = loadingAdViews.getTimeSinceRequestStart() > 25_000;

                        if (moPubShownAdMap.size() > 0 && (forceAdPublish || hasRequestExpired)) {

                            MoPubShownAd mopubShownAd = selectRandomMopubShownAd();

                            ad = mopubShownAd.cachedAd;
                            view = ad;
                        }
                    }

                    if (ad == null) {
                        break;
                    }

                    foundAdToBind = true;

                    final View finalView = view;
                    Utility.executeOnUIThread(() -> MNAL.this.bindAdToView(finalView, loadingAdViews.getAdLayout()));

                } finally {
                    if (!foundAdToBind) {
                        this.loadingAdViews.addFirst(loadingAdViews);
                    }
                }
            }
        }
    }

    private Pair<FacebookCPMType, MaxNativeAdView> getBestLoadedAd() {
        synchronized (show_ads_lock) {
            Pair<FacebookCPMType, MaxNativeAdView> bestLoadedAd = loadedAds.pollFirst();
            return bestLoadedAd;
        }
    }

    @Override
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private View tryGetFacebookShownAdView(String adKey, ViewGroup adLayout) {
        MoPubShownAd moPubShownAd = moPubShownAdMap.get(adKey);
        if (moPubShownAd == null) {
            return null;
        }

        View adView;

        adView = moPubShownAd.cachedAd;

        return adView;
    }

    private MoPubShownAd selectRandomMopubShownAd() {
        int minimumFacebookAdCountForPrioritization = 3;

        List<MoPubShownAd> values = new ArrayList<>(moPubShownAdMap.values());
        List<MoPubShownAd> adsWithoutImpressionList = new ArrayList<>();

        for (MoPubShownAd FacebookShownAd : values) {
            if (!FacebookShownAd.hasImpression) {
                adsWithoutImpressionList.add(FacebookShownAd);
            }
        }

        if (adsWithoutImpressionList.size() == 1) {
            return adsWithoutImpressionList.get(0);
        }

        if (!adsWithoutImpressionList.isEmpty()) {
            values = adsWithoutImpressionList;
        }

        List<MoPubShownAd> higherPriorityFacebookShownAds = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            MoPubShownAd FacebookShownAd = values.get(i);

            if (FacebookShownAd.canBeBoundAgain()) {
                higherPriorityFacebookShownAds.add(FacebookShownAd);
            }
        }

        if (higherPriorityFacebookShownAds.size() >= minimumFacebookAdCountForPrioritization) {
            values = higherPriorityFacebookShownAds;
        }

        List<Integer> highCpmAdIndexes = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            highCpmAdIndexes.add(i);
        }

        generator.nextInt(values.size());

        int adIndex = -1;

        if (!highCpmAdIndexes.isEmpty()) {
            adIndex = getAdIndex(highCpmAdIndexes);
        }

        if (adIndex == -1) {
            adIndex = 0;
        }

        MoPubShownAd moPubShownAd = values.get(adIndex);

        return moPubShownAd;
    }

    private Integer getAdIndex(List<Integer> adIndexesList) {
        return adIndexesList.get(generator.nextInt(adIndexesList.size()));
    }

    private void addAd(MaxNativeAdView ad) {
        synchronized (show_ads_lock) {
            loadedAds.add(new Pair<>(FacebookCPMType.MAX, ad));
        }
    }

    public void startInit() {
        MNAL.this.initProperly();
    }

    private volatile boolean hasPendingPeriodicCheck = false;
    private final Object periodicCheckLock = new Object();

    private void timerCheck() {
        synchronized (periodicCheckLock) {
            loadNewAds();
            if (!hasPendingPeriodicCheck) {
                TimerUtils.postDelayedInBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (periodicCheckLock) {
                            hasPendingPeriodicCheck = false;
                            MNAL.this.timerCheck();
                        }
                    }
                }, 10_000);
                hasPendingPeriodicCheck = true;
            }
        }
    }

    private final LinkedList<LoadingAdViews> loadingAdViews = new LinkedList<>();

    public View loadNewAd(LayoutInflater inflater, final ViewGroup adLayout, final String
            adAdditionalKey, View existingView, DesignUnitType designUnitType, final String adSignature,
                          boolean withLoader) {
        synchronized (show_ads_lock) {

            if (existingView != null) {
                if (checkForMatchingAds(existingView)) {
                    return existingView;
                }
            }

            if (adSignature != null) {
                final View cachedView = tryGetFacebookShownAdView(adAdditionalKey + adSignature, adLayout);
                if (cachedView != null) {
                    Utility.executeOnUIThread(() -> MNAL.this.insertAdView(cachedView, adLayout));
                    return cachedView;
                }
            }

            if (existingView == null) {
                existingView = createAdView(inflater, designUnitType);
            }

            final View finalAdView = existingView;

            Runnable requestNewAction = () -> {
                MNAL.this.initProperly();
                synchronized (show_ads_lock) {
                    loadingAdViews.add(new LoadingAdViews(finalAdView, adLayout, adAdditionalKey, adSignature));
                    MNAL.this.showAds();
                    MNAL.this.loadNewAds();
                }
            };

            synchronized (show_ads_lock) {
                if (loadedAds.size() > 0) {
                    requestNewAction.run();
                } else {

                    lastAdRequestTimeNanos = System.nanoTime();
                    Utility.executeOnBGThread(requestNewAction);
                }
            }
            return existingView;
        }
    }

    private boolean checkForMatchingAds(View existingView) {
        for (LoadingAdViews loadingAdViews : this.loadingAdViews) {
            if (loadingAdViews.getView().equals(existingView)) {
                showAds();
                return true;
            }
        }

        return false;
    }

    private View createAdView(LayoutInflater inflater, DesignUnitType designUnitType) {
        int adLayoutResource = getAdLayoutResource(designUnitType);

        return inflater.inflate(adLayoutResource, null);
    }

    private void bindAdToView(View adView, ViewGroup adLayout
            ) {
        try {
            insertAdView(adLayout, adView);
        } catch (Exception e) {
        }
    }

    private void insertAdView(ViewGroup adLayout, View adView) {
        boolean adViewHasAlreadyBeenAddedToLayout = false;
        if (adLayout.getChildCount() > 0) {
            if (adLayout.getChildAt(0).equals(adView)) {
                adViewHasAlreadyBeenAddedToLayout = true;
            } else {
                adLayout.removeAllViews();
            }
        }

        if (!adViewHasAlreadyBeenAddedToLayout) {
            adLayout.addView(adView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            adLayout.setVisibility(View.VISIBLE);
        }
    }

    private void insertAdView(View adView, ViewGroup adLayout) {
        insertAdView(adLayout, adView);
    }

    public boolean hasAds() {
        return adsEnabled && (!loadedAds.isEmpty() || !moPubShownAdMap.isEmpty());
    }

    abstract int getImpressionCount();

    abstract int getMaxAdLoadInBackground();

    abstract void clear();

    @Override
    public void onDestroy() {
        moPubShownAdMap.clear();
    }
}