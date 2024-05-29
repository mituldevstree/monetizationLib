package com.monetizationlib.data.ads

import android.app.Activity
import android.util.Log
import com.applovin.sdk.AppLovinPrivacySettings
import com.fyber.fairbid.user.UserInfo
import com.ironsource.mediationsdk.IronSource
import com.michaelflisar.dialogs.DialogGDPR
import com.michaelflisar.dialogs.GDPR
import com.michaelflisar.dialogs.classes.GDPRConsent
import com.michaelflisar.dialogs.classes.GDPRDefinitions
import com.michaelflisar.dialogs.classes.GDPRSetup
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.presenters.DialogStyle
import com.michaelflisar.dialogs.presenters.showAlertDialog
import com.monetizationlib.data.Monetization

object ConsentHandler {
    var setup: GDPRSetup? = null

    fun initialize() {
        setup = GDPRSetup(
            networks = listOf(
                GDPRDefinitions.FAN,
                GDPRDefinitions.APPLOVIN,
                GDPRDefinitions.IRONSOURCE,
                GDPRDefinitions.ADCOLONY,
                GDPRDefinitions.APPODEAL,
                GDPRDefinitions.INMOBI,
                GDPRDefinitions.TAPJOY,
                GDPRDefinitions.MINTEGRAL,
                GDPRDefinitions.VUNGLE,
            ),
            policyLink = Monetization.monetizationConfig?.privacyPolicy
                ?: "https://givvy-prod.herokuapp.com/privacy-policy.pdf",
            //explicitAgeConfirmation = true,
            //hasPaidVersion = true,
            //allowNoConsent = true,
            explicitNonPersonalisedConfirmation = true
        )
    }

    fun shouldAskForConsentGeneral(activity: Activity): Boolean {
        return setup?.let { GDPR.shouldAskForConsent(activity, it) } ?: false
    }

    fun shouldAskForConsentApplovin(activity: Activity): Boolean {
        Monetization.logWtfIfNeeded(
            "shouldAskForConsentApplovin !AppLovinPrivacySettings.hasUserConsent = ${
                !AppLovinPrivacySettings.hasUserConsent(
                    activity
                )
            } &&  shouldAskForConsentGeneral = ${
                shouldAskForConsentGeneral(activity)
            }"
        )

        return !AppLovinPrivacySettings.hasUserConsent(activity) && shouldAskForConsentGeneral(
            activity
        )
    }

    fun showConsent(activity: Activity, initAds: (activity: Activity) -> Unit? = {}) {
        val setupCopy = setup ?: return
        val dialogGDPR = DialogGDPR(
            1100, setup = setupCopy, cancelable = false
        )
        Utility.executeOnUIThread {
            dialogGDPR.showAlertDialog(activity,
                DialogStyle(),
                callback = { event: IMaterialDialogEvent ->
                    run {
                        Log.wtf("GDPR", event.toString())
                        when (GDPR.getCurrentConsentState(activity, setupCopy).consent) {
                            GDPRConsent.UNKNOWN -> {
                            }

                            GDPRConsent.NO_CONSENT, GDPRConsent.NON_PERSONAL_CONSENT_ONLY -> {
                                AppLovinPrivacySettings.setHasUserConsent(false, activity)
                                UserInfo.setGdprConsent(false, activity);
                                UserInfo.setLgpdConsent(false, activity);
                                IronSource.setConsent(false);
                                IronSource.setMetaData("do_not_sell","true");
                            }

                            GDPRConsent.PERSONAL_CONSENT, GDPRConsent.AUTOMATIC_PERSONAL_CONSENT -> {
                                AppLovinPrivacySettings.setHasUserConsent(true, activity)
                                UserInfo.setGdprConsent(true, activity);
                                UserInfo.setLgpdConsent(true, activity);
                                IronSource.setConsent(true);
                                IronSource.setMetaData("do_not_sell","false");
                            }
                        }

                        initAds(activity)
                    }
                })
        }
    }

    fun handleConsent(activity: Activity, initAds: (activity: Activity) -> Unit? = {}) {
        val setupCopy = setup ?: return

        when (GDPR.getCurrentConsentState(activity, setupCopy).consent) {
            GDPRConsent.UNKNOWN -> {
            }

            GDPRConsent.NO_CONSENT, GDPRConsent.NON_PERSONAL_CONSENT_ONLY -> {
                AppLovinPrivacySettings.setHasUserConsent(false, activity)
                UserInfo.setGdprConsent(false, activity);
                UserInfo.setLgpdConsent(false, activity);
                IronSource.setConsent(false);
                IronSource.setMetaData("do_not_sell","true");

                initAds(activity)
            }

            GDPRConsent.PERSONAL_CONSENT, GDPRConsent.AUTOMATIC_PERSONAL_CONSENT -> {
                AppLovinPrivacySettings.setHasUserConsent(true, activity)
                UserInfo.setGdprConsent(true, activity);
                UserInfo.setLgpdConsent(true, activity);
                IronSource.setConsent(true);
                IronSource.setMetaData("do_not_sell","false");

                initAds(activity)
            }
        }
    }
}