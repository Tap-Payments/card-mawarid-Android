package company.tap.tapcardsdk.internal.logic.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment


import company.tap.tapcardsdk.R
import company.tap.tapcardsdk.internal.logic.api.models.Charge
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import company.tap.tapuilibrary.uikit.ktx.setTopBorders

class WebFragment(private val webViewContract: WebViewContract?, context: Context?) : Fragment(),
    CustomWebViewClientContract {

    private var webViewUrl: String? = null
    private var chargeResponse: Charge? = null
    lateinit var progressBar :ProgressBar
   lateinit var web_view :WebView
    lateinit var topLinear :LinearLayout
    lateinit var draggerView :View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      val   view  = inflater.inflate(R.layout.fragment_web, container, false)
        web_view = view.findViewById(R.id.web_view)
        progressBar = view.findViewById(R.id.progressBar)
        topLinear = view.findViewById(R.id.topLinear)
        draggerView = view.findViewById(R.id.draggerView)
        println("view here"+view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTopDraggerView()
        println("view created"+view)

        progressBar.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        progressBar.progressDrawable?.setColorFilter(
            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.progressTintList = ColorStateList.valueOf(Color.RED);

        webViewUrl = arguments?.getString(KEY_URL)
        chargeResponse = arguments?.getSerializable(CHARGE) as Charge?
        progressBar.max = 100
        progressBar.progress = 20
        if (TextUtils.isEmpty(webViewUrl)) {
            throw IllegalArgumentException("Empty URL passed to WebViewFragment!")
        }
        webViewUrl?.let { setUpWebView(it) }

    }

    private fun setTopDraggerView() {
        topLinear.let {
            setTopBorders(
                it,
                40f,// corner raduis
                0.0f,
                Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")),// stroke color
                Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")),// tint color
                Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor"))
            )
        }//

        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            draggerView.let {
                setBorderedView(
                    it,
                    40f,// corner raduis
                    0.0f,
                    Color.parseColor("#3b3b3c"),// stroke color
                    Color.parseColor("#3b3b3c"),// tint color
                    Color.parseColor("#3b3b3c")
                )
            }//
        } else {
            draggerView.let {
                setBorderedView(
                    it,
                    40f,// corner raduis
                    0.0f,
                    Color.parseColor("#e0e0e0"),// stroke color
                    Color.parseColor("#e0e0e0"),// tint color
                    Color.parseColor("#e0e0e0")
                )
            }//
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(mUrl: String) {
        web_view?.settings?.javaScriptEnabled = true
        web_view?.webChromeClient = WebChromeClient()
        web_view?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
       // web_view?.webViewClient = cardViewModel?.let { TapCustomWebViewClient(this, it) }!!
        web_view?.settings?.loadWithOverviewMode = true
        webViewUrl?.let { web_view?.loadUrl(it) }
        web_view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (web_view?.canGoBack() == true) {
                    web_view?.goBack()
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


    /**
     * change action button status with success or failed
    if success == true show success gif of action button
    if success == false show error gif of action button
     */
    override fun submitResponseStatus(success: Boolean) {
        /*  val intent = Intent(activity, CheckoutFragment::class.java)
          startActivity(intent)*/
       // webViewContract?.redirectLoadingFinished(success, chargeResponse, contextSDK)
    }

    override fun getRedirectedURL(url: String) {
        // webViewContract.redirectLoadingFinished(url.contains("https://www.google.com/search?"))
        if(url.contains("gosellsdk://return_url")){

//        webViewContract.redirectLoadingFinished(url.contains("gosellsdk://return_url"), chargeResponse)
        }else{
//            webViewContract.directLoadingFinished(true)
        }
    }

    override fun showLoading(showLoading: Boolean, authId: String?) {

    }


    companion object {
        const val KEY_URL = "key:url"
        const val CHARGE = "charge_response"

        fun newInstance(url: String, webViewContract: WebViewContract, chargeResponse: Charge?, context : Context?): WebFragment {
            println("url"+url)
            val fragment = WebFragment(webViewContract,context)
            val args = Bundle()
            args.putString(KEY_URL, url)
            args.putSerializable(CHARGE, chargeResponse)
            fragment.arguments = args
            return fragment
        }
    }

}