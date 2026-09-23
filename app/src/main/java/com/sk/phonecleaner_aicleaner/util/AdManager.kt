package com.sk.phonecleaner_aicleaner.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun initialize(context: Context) {
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob MobileAds initialized: $status")
                loadInterstitial(context.applicationContext)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MobileAds", e)
        }
    }

    fun loadInterstitial(context: Context) {
        if (interstitialAd != null || isLoading) return
        isLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d(TAG, "Interstitial Ad Loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.e(TAG, "Interstitial Ad failed to load: ${error.message} (code ${error.code})")
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial Ad dismissed by user")
                    interstitialAd = null
                    loadInterstitial(activity.applicationContext)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.e(TAG, "Interstitial Ad failed to show: ${error.message}")
                    interstitialAd = null
                    loadInterstitial(activity.applicationContext)
                    onAdClosed()
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "Interstitial Ad was not ready yet, proceeding with action")
            loadInterstitial(activity.applicationContext)
            onAdClosed()
        }
    }
}