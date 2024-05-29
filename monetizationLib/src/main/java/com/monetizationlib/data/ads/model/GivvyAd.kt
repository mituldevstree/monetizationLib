package com.monetizationlib.data.ads.model
import androidx.annotation.Keep

@Keep
data class GivvyAd(
    val providerName: String? = "unknown",
    val isShown: Boolean = false,
    val givvyAdType: GivvyAdType = GivvyAdType.None
) {
    public fun isFacebookAd(): Boolean {
        return providerName?.contains("meta", true) == true || providerName?.contains(
            "facebook", true
        ) == true
    }
}

public enum class GivvyAdType {
    Interstitial, Video, None
}