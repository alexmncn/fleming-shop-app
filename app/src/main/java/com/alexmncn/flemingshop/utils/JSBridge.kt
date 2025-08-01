package com.alexmncn.flemingshop.utils

import android.webkit.JavascriptInterface

public class JSBridge(private val onTokenReady: (String) -> Unit)  {
    @JavascriptInterface
    fun onTokenReceived(token: String) {
        onTokenReady(token)
    }
}