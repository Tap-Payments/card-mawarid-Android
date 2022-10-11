package company.tap.tapcardformkit.internal.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import company.tap.tapcardsdk.internal.logic.api.ApiService
import company.tap.tapcardsdk.internal.logic.api.CardViewEvent
import company.tap.tapcardsdk.internal.logic.api.CardViewModel

import company.tap.tapcardsdk.internal.logic.webview.CustomWebViewClientContract


/**
 * Created by OlaMonir on 7/27/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class TapCustomWebViewClient constructor(private val customWebViewClientContract: CustomWebViewClientContract, private val cardViewModel: CardViewModel) : WebViewClient() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if(request.url.toString().contains("gosellsdk://return_url?tap_id=auth_TS")){
            return false
        }else
        checkPaymentError(request.url.toString().toLowerCase())
        checkKnetPaymentStatus(request.url.toString().toLowerCase())
        Log.e("urlincustom",request.url.toString())
        getCustomHeaders()?.let { view.loadUrl(request.url.toString(), it) }
        return true
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, @NonNull url: String): Boolean {
        if(url.toString().contains("gosellsdk://return_url?tap_id=auth_TS")){
            return false
        }else
        checkPaymentError(url.toLowerCase())
        checkKnetPaymentStatus(url.toLowerCase())

        Log.d("url", url)
        getCustomHeaders()?.let { view.loadUrl(url, it) }
        return true
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkKnetPaymentStatus(url: String) {
       // if (url.contains("response/receiptKnet".toLowerCase())) {
        val urlIsReturnURL: Boolean = url.startsWith(ApiService.RETURN_URL)
        val shouldLoad = !urlIsReturnURL
        val redirectionFinished = urlIsReturnURL
        val shouldCloseWebPaymentScreen = false
        if (urlIsReturnURL) {
            if (checkPaymentSuccess(url)) {
                customWebViewClientContract.submitResponseStatus(true)
            } else {
                val urlQuerySanitizer: Uri = Uri.parse(url)
                val msg: String = urlQuerySanitizer.getQueryParameter("message").toString()
                customWebViewClientContract.submitResponseStatus(false)
            }
        }
    }

    private fun checkPaymentError(url: String) {
        if (url.contains("errorPage".toLowerCase())) {
            val urlQuerySanitizer: Uri = Uri.parse(url)
            val msg: String = urlQuerySanitizer.getQueryParameter("message").toString()
            if (msg.isNotEmpty()) customWebViewClientContract.submitResponseStatus(true) else customWebViewClientContract.submitResponseStatus(false)
        }
    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkPaymentSuccess(url: String): Boolean {
        return try {
            val urlQuerySanitizer: Uri = Uri.parse(url)
            println("urlQuerySanitizer on checkpayment" + urlQuerySanitizer)
            when {

                url.contains("auth") -> {
                    CardViewModel().processEvent(CardViewEvent.RetreiveSaveCardEvent, null, null, null, null, null,null)

                }

            }
            val status: String = urlQuerySanitizer.getQueryParameter("tap_id").toString()
            println("status on checkpayment" + status)
            status.equals("CAPTURED", ignoreCase = true) || status.equals(
                    "0",
                    ignoreCase = true
            )
        } catch (ex: Exception) {
            false
        }
    }


    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Log.d("url", url.toString())
        customWebViewClientContract.showLoading(true)

     //   url?.let { customWebViewClientContract.getRedirectedURL(it) }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPageFinished(@NonNull view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d("onPageFinished", url.toString())
        if(url?.contains("gosellsdk://return_url?tap_id=auth_TS") == true){
          //  customWebViewClientContract.showLoading(false,"auth_TS")
            val parts: List<String> =
                url.split("=") // escape .

            val part1 = parts[0]
            val savedResponseId = parts[1]

            customWebViewClientContract.submitResponseStatus(false)

            CardViewModel().processEvent(CardViewEvent.RetreiveSaveCardEvent, null, null, null, null, null,null,savedResponseId)
            return
        }
      //  customWebViewClientContract.showLoading(false,"auth_TS")
    /*    if(url?.contains("auth") == true){
            val parts: List<String> =
                url.split("=") // escape .

            val part1 = parts[0]
            val savedResponseId = parts[1]


            CardViewModel().processEvent(CardViewEvent.RetreiveSaveCardEvent, null, null, null, null, null,null,savedResponseId)


        }*/

      //  url?.let { customWebViewClientContract.getRedirectedURL(it) }

    }


    @SuppressLint("NewApi")
    override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
    ): WebResourceResponse? {
        return null
    }

    private fun getCustomHeaders(): Map<String, String>? {
        val headers: MutableMap<String, String> = HashMap()
        //  headers["cid"] =  ""
        return headers
    }


}