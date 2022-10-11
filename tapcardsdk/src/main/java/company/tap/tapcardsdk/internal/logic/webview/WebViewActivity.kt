package company.tap.tapcardsdk.internal.logic.webview

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import company.tap.tapcardformkit.internal.webview.TapCustomWebViewClient
import company.tap.tapcardsdk.R
import company.tap.tapcardsdk.internal.logic.api.CardViewModel
import company.tap.tapcardsdk.internal.logic.api.models.Charge

import company.tap.tapuilibrary.themekit.ThemeManager


class WebViewActivity : AppCompatActivity() , CustomWebViewClientContract {
    private var webViewUrl: String? = null
    private var chargeResponse: Charge? = null
    lateinit var progressBar : ProgressBar
    lateinit var web_view : WebView
    lateinit var topLinear : LinearLayout
   // lateinit var draggerView : View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        web_view = findViewById(R.id.web_view)
        progressBar = findViewById(R.id.progressBar)
        topLinear = findViewById(R.id.topLinear)
       // draggerView = findViewById(R.id.draggerView)
        progressBar.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        progressBar.progressDrawable?.setColorFilter(
            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.progressTintList = ColorStateList.valueOf(Color.RED);
        val intent = intent
        webViewUrl = intent?.getStringExtra("webviewURL")
       // chargeResponse = arguments?.getSerializable(WebFragment.CHARGE) as Charge?
        progressBar.max = 100
        progressBar.progress = 20
        if (TextUtils.isEmpty(webViewUrl)) {
            throw IllegalArgumentException("Empty URL passed to WebViewFragment!")
        }
        webViewUrl?.let { setUpWebView(it) }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(mUrl: String) {
        web_view.settings.javaScriptEnabled = true
        web_view.webChromeClient = WebChromeClient()
        web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
         web_view.webViewClient =   TapCustomWebViewClient(this, CardViewModel())
        web_view.settings.loadWithOverviewMode = true
        webViewUrl?.let { web_view.loadUrl(it) }
        web_view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (web_view.canGoBack() == true) {
                    web_view.goBack()
                    /**
                     * put here listener or delegate thT process cancelled **/
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            false
        }



        web_view.webChromeClient = object : WebChromeClient() {
            /*
                    public void onProgressChanged (WebView view, int newProgress)
                        Tell the host application the current progress of loading a page.

                    Parameters
                        view : The WebView that initiated the callback.
                        newProgress : Current page loading progress, represented by an integer
                            between 0 and 100.
                */
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // Update the progress bar with page loading progress
                progressBar.progress =newProgress
                if (newProgress == 100) {
                    // Hide the progressbar
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun submitResponseStatus(success: Boolean) {
        web_view.clearHistory() // clear history
        finish() // finish activity
    }

    override fun getRedirectedURL(url: String) {
        println("call here2")

    }

    override fun showLoading(showLoading: Boolean ,authId:String?) {
        if(showLoading && authId?.contains("auth_TS")==true){
            web_view.clearHistory() // clear history
            finish() // finish activity
        }
    }

    fun finishWebActivity(){
       finishAndRemoveTask()
      //  web_view.clearHistory() // clear history
        finish() // finish activity

    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            web_view.clearHistory() // clear history
            finish() // finish activity
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}