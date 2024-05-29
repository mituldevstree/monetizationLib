package com.monetizationlib.data.ads;

import android.view.View;
import android.view.ViewGroup;

import com.monetizationlib.data.ads.TimeUtil;


public final class LoadingAdViews {
    private View view;
    private final ViewGroup adLayout;
    private final String eventNameSuffix;
    private String adKey;
    private long requestStartTimeNanos = System.nanoTime();

    LoadingAdViews(View view, ViewGroup adLayout, String eventNameSuffix, String adKey) {
        this.view = view;
        this.adLayout = adLayout;
        this.eventNameSuffix = eventNameSuffix;
        // Add eventNameSuffix to make the key globally unique.
        if (adKey != null) {
            this.adKey = eventNameSuffix + adKey;
        }
    }

    public View getView() {
        return view;
    }

    public ViewGroup getAdLayout() {
        return adLayout;
    }

    String getAdKey() {
        return adKey;
    }

    long getTimeSinceRequestStart() {
        return TimeUtil.getTimeElapsed(requestStartTimeNanos);
    }

    public void setView(View view) {
        this.view = view;
    }
}
