package com.sk.phonecleaner_aicleaner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sk.phonecleaner_aicleaner.util.AdManager

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = AdManager.TEST_BANNER_ID
) {
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val adWidthPixels = displayMetrics.widthPixels
    val adWidthDp = (adWidthPixels / displayMetrics.density).toInt()
    val adaptiveAdSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthDp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(adaptiveAdSize)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}